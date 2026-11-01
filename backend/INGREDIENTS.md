# Ingredient pipeline

> **Status:** proposal. This describes the target design for making scraped-ingredient processing reproducible and regression-safe. Sections marked *current* describe today's behaviour.

How a scraped ingredient line becomes a classified line, a parsed quantity/unit/name, a pantry (catalogue) match, and a USDA food match. USDA seed import, licence, and nutrition maths stay in [`NUTRITION.md`](NUTRITION.md). Scraper usage stays in [`../scripts/scraping/README.md`](../scripts/scraping/README.md).

## Problems this solves

1. **Regressions.** Matching is a pure function of hand-written global rules (aliases, phrase rewrites, normalizer tokens, scorer word lists). Every `calculateRecipeNutrition -Pnutrition.allRecipes=true` re-decides every line and overwrites `ingredient_nutrition_matches`. A rule added for one batch silently changes lines in older batches, and nothing shows the corpus-wide effect.
2. **Not reproducible.** Fixes are spread over code, one-off backfills, and DB state. Re-importing from scratch does not reliably give the same database, so the local and release databases drift.
3. **Group headings stored as ingredients.** Headings such as `Chimichurri` are hard to recognise in isolation and are only filtered at read time (`IngredientVocabulary.isIgnorableIngredientLine`), so they remain `ingredients` rows. Heading rules are also duplicated between `ScrapedIngredientLines.kt` and `IngredientHeadingRules.kt`.

## Principles

- **Determinism.** Output depends only on versioned inputs: saved scrape JSON, USDA FDC files, code, and committed data files. No network, LLM, or clock-dependent behaviour during import or rebuild.
- **Decisions are data, not code.** A reviewed decision for one name or one line is a row in a committed file, not a new global rule.
- **Specific beats general.** Precedence: line override → pinned name → alias → rule → token score.
- **Every rule change shows its blast radius.** A committed snapshot is diffed on every change.
- **ML suggests, humans decide.** Suggestions are written to a review file; only accepted rows become pinned data.

## Data layers

| Layer | Contents | Identity | Lifecycle |
|-------|----------|----------|-----------|
| **Raw source** | Saved scrape JSON (`{output}/recipes/*.json`) | `source_url` | Archived; never edited by rules |
| **Content** | `recipes` (title, instructions, metadata) | `source_url` (stable `recipes.id`) | Inserted once; ids preserved on release |
| **Derived** | Ingredient groups and lines, line classification, `ingredient_measurements`, pantry matches, `ingredient_nutrition_matches`, contributions, `nutrition`, appliances, enrichment | Recomputed from raw source + content | Dropped and rebuilt idempotently |
| **Decisions** | Pins, line overrides, snapshot | Stored under `backend/data/ingredients/` (git-ignored for now) | Diffed with `diffIngredientSnapshot` |

> [!WARNING]
> *Current:* JSON mode inserts recipes with new serial ids, so re-importing from scratch changes `recipes.id`. On the release database that would cascade-delete favorites and cookbook membership and break shared deep links. The release flow must keep recipe ids stable (see [Workflow](#workflow-local-experiment-release-rebuild)).

## Pipeline stages

```text
Raw line (JSON)
  └──► Classify (ingredient / heading / note / equipment)
         └──► Parse (quantity, unit, name)
                ├──► Pantry match (IngredientCatalogue)
                └──► Food match (USDA FDC) ──► Grams + nutrition
```

| Stage | *Current* code | Decision data |
|-------|----------------|---------------|
| Classify | `ScrapedIngredientLines.kt` (scrape time), `IngredientHeadingRules.kt` / `IngredientVocabulary.kt` (read time) | `line-overrides.tsv` |
| Parse | `feature/nutrition/IngredientLineParser.kt`, `IngredientLinePhraseRewrites.kt` | `line-overrides.tsv` |
| Pantry match | `shared/domain` `IngredientCatalogue`, `IngredientAliasGroups`, `IngredientVocabulary.matchesAnyIngredient` | `names.tsv` (`pantry_item`) |
| Food match | `NutritionLookupIndex`, `NutritionFoodNameScorer`, `NutritionSeedAliases*.kt` | `names.tsv` (`fdc_source`, `fdc_id`) |

## Snapshot and decision files (`backend/data/ingredients/`)

All files are UTF-8, tab-separated, with a header row, sorted by their key columns so diffs are stable. Food references use FDC `source_name` + `source_id`, never internal `nutrition_foods.id` (serial ids differ between databases). The folder is git-ignored for now; `./gradlew diffIngredientSnapshot` compares the current database against the files on disk.

### `names.tsv` — pins and name snapshot

One row per distinct lookup name (`NutritionNameNormalizer.forLookup`).

| Column | Meaning |
|--------|---------|
| `lookup_name` | Key |
| `status` | `pinned` (reviewed; overrides rules) or `auto` (rule output; asserted by the snapshot) |
| `pantry_item` | Matched `IngredientCatalogue` item, empty if none |
| `fdc_source` | `fdc_foundation`, `fdc_sr_legacy`, `fdc_survey`, `fdc_branded`, `purecipes_custom` |
| `fdc_id` | FDC source id |
| `fdc_description` | For readability only |
| `match_source` | `pinned`, `alias`, `name`, `tokens`, or empty |
| `line_count` | Number of lines using this name (helps prioritise review) |

Promoting an `auto` row to `pinned` freezes it. Rule changes cannot move pinned rows.

### `lines.tsv` — full line snapshot

One row per scraped ingredient line. Catches classification and parser drift that `names.tsv` cannot see.

| Column | Meaning |
|--------|---------|
| `source_url` | Recipe key (stable across databases) |
| `group_index`, `line_index` | Position |
| `raw_text` | Line as scraped |
| `kind` | `ingredient`, `heading`, `note`, `equipment` |
| `group_name` | Resolved group heading |
| `quantity`, `unit`, `lookup_name` | Parser output |
| `requirement`, `alternative_group_key` | Optional / alternative handling |

### `line-overrides.tsv` — per-line decisions

For the few lines rules should not try to generalise. Keyed by `source_url` + `raw_text`; non-empty columns override `kind`, `group_name`, `quantity`, `unit`, or `lookup_name` during `rebuildIngredientData`, `calculateRecipeNutrition`, and `snapshotIngredients`. Example: mark `Chimichurri` as a `heading` in one recipe and keep it as an `ingredient` in another.

| Column | Meaning |
|--------|---------|
| `source_url` | Recipe key (stable across databases) |
| `raw_text` | Exact ingredient line text |
| `kind` | Optional override: `ingredient`, `heading`, `note`, or `equipment` |
| `group_name` | Optional override for `ingredient_groups.name` |
| `quantity`, `unit`, `lookup_name` | Optional parser overrides when `kind = ingredient` |

### Snapshot and rebuild commands

Recompute the derived ingredient layer (`ingredient_groups`, `ingredients`, `ingredient_measurements`, `ingredient_nutrition_matches`, `ingredient_nutrition_contributions`, and `nutrition`) for all scraped recipes from the saved JSON archive (`~/Purecipes/recipeoutput/recipes`, falling back to current database rows) while preserving `recipes.id`:

```bash
./gradlew rebuildIngredientData
```

Write or refresh `backend/data/ingredients/names.tsv` and `backend/data/ingredients/lines.tsv` from the current database (preserving any `pinned` rows in `names.tsv` and printing a diff summary if previous files exist; pass `-Psnapshot.seedPins=true` to promote confirmed matches to `pinned`):

```bash
./gradlew snapshotIngredients
./gradlew snapshotIngredients -Psnapshot.seedPins=true
```

Diff the current database against the committed `names.tsv` and `lines.tsv` without modifying files (exits non-zero if differences are found; pass `-Psnapshot.failOnDiff=false` for report-only):

```bash
./gradlew diffIngredientSnapshot
```

## Group headings in context

Classify headings with the whole recipe as context, not line by line. Apply signals in order:

1. **Source structure.** Groups from `recipe_scrapers` `ingredient_groups()`, JSON-LD, or site DOM (as already done for Jamie Oliver). Most reliable.
2. **Explicit form.** Trailing colon, `For the …`, all caps, existing structural rules.
3. **Composite names.** A shared list of names that can be either a ready-made ingredient or a sub-recipe (`chimichurri`, `pesto`, `salsa`, `dressing`, `aioli`, `curry paste`, `stock`, `pastry`, `dough`, `sauce`, …). For these, treat the line as a heading when **all** of these hold:
   - no quantity or unit
   - followed by at least two quantified lines
   - the instructions prepare it (`make the chimichurri`, `for the chimichurri, …`, `to make the …`)

   `2 tbsp chimichurri` stays an ingredient.
4. **Line override** in `line-overrides.tsv`.

Classification moves fully to import/rebuild time, so headings become `ingredient_groups.name` instead of `ingredients` rows. Scraper and backend share one heading rule set instead of two copies.

## Workflow: local experiment, release rebuild

```text
Local: scrape / edit rules
  └──► rebuild derived data
         └──► diffIngredientSnapshot (names.tsv, lines.tsv)
                ├── unexpected changes ──► adjust rules / pins
                └── accepted ──► snapshotIngredients + commit code
                                   └──► Release: import new JSON (upsert by source_url)
                                          └──► Release: rebuild derived data + verify diff
                                                 └──► Backup release DB, restore locally
```

- **Local** is disposable: drop and rebuild freely.
- **Release** never deletes scraped recipes. It inserts new `source_url`s, keeps existing recipe ids, and rebuilds only the derived layer. User-created recipes (`created_by_user_id IS NOT NULL`) are never touched.
- Because derived data depends only on committed inputs, the release rebuild produces exactly the committed `names.tsv` / `lines.tsv`. A mismatch fails the run before backup.
- Restoring the release backup locally then gives identical data on both sides.

> [!IMPORTANT]
> The saved scrape JSON is the raw source of truth and must be archived (see [Decisions](#decisions)). The `{DB_ID}-` filename prefix is not a stable key across databases; `source_url` is.

## Review loop for unmatched names

1. Run the rebuild; the snapshot tool lists new or unmatched `lookup_name`s and new `heading` candidates.
2. A suggestion tool (offline, not part of import) proposes candidates: lexical candidates (existing scorer, `pg_trgm`) reranked by an LLM with the raw line and recipe title as context. Output: `suggestions.tsv`.
3. Accept suggestions by moving rows into `names.tsv` as `pinned` (or `line-overrides.tsv`).
4. Change global rules only when one change fixes many names and the snapshot diff is clean.

## Rollout steps

Each step is a separate commit or PR, validated and reviewed before the next.

1. **Snapshot tool.** Gradle task that writes `names.tsv` (including `pantry_item`) and `lines.tsv` from the current DB, plus a diff mode. Commit the first snapshot.
2. **Stable recipe identity and JSON line re-processing.** JSON import upserts by `source_url` and preserves ids. Add a `rebuildIngredientData` task that recomputes the derived layer for scraped recipes from saved JSON (`~/Purecipes/recipeoutput/recipes`), rejoining any previously over-split `ALTERNATIVE` clusters and re-running `ScrapedIngredientLines` rules (so bad splits like `"1 pound (450 g) ground beef, pork, veal, or any combination"` → `"1 pound any combination"` are repaired automatically).
3. **Pins.** Load `names.tsv` rows where `status = pinned` in the import-time food and pantry matchers with top precedence. Seed pins from currently confirmed matches.
4. **Contextual headings.** Single shared heading rule set, composite-name rule, `line-overrides.tsv`.
5. **Suggestion tool.** Offline candidate generation and LLM reranking into `suggestions.tsv`.
6. **Agent skill.** Codify the review loop and the "pin before rule" policy under `.agents/skills/`.

## Decisions

- `names.tsv` pins apply to **imported data only**. Runtime pantry search (backend `IngredientMatchCorpus`) is unchanged.
- Saved scrape JSON stays in `~/Purecipes/recipeoutput/recipes` (outside the repository).
- `backend/data/ingredients/` (snapshot and decision TSVs) is git-ignored for now because this repository is public; back it up alongside `~/Purecipes/recipeoutput`.

## Open questions

- Is `lines.tsv` small enough to commit at the target corpus size, or should it be split per site?
- Do any scraped recipes carry user nutrition overrides (`ingredient_nutrition_contributions.uses_user_override`) that a rebuild must preserve?
