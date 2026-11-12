# Ingredient Pipeline

How a scraped ingredient line becomes a classified row, a parsed quantity/unit/name, a pantry (`IngredientCatalogue`) match, and a USDA food match.

- USDA seed import, licence, and nutrition maths: [`NUTRITION.md`](NUTRITION.md)
- Database tables and columns: [`DATABASE.md`](DATABASE.md)
- Recipe website scraper usage: [`../scripts/scraping/README.md`](../scripts/scraping/README.md)
- Agent playbook for reviewing and pinning batches: [`../.agents/skills/ingredient-matching/SKILL.md`](../.agents/skills/ingredient-matching/SKILL.md)

---

## Core Principles

1. **Determinism.** Every ingredient row, pantry match, and nutrition total is a deterministic function of versioned inputs: the saved scrape JSON archive, USDA FoodData Central seed data, repository code, and the decision TSV files in `backend/data/ingredients/`. No network or LLM calls happen during import, rebuild, or nutrition calculation.
2. **Decisions are data, not code.** Reviewing an ingredient name or fixing an ambiguous recipe line records a row in `names.tsv` (`status = pinned`) or `line-overrides.tsv` instead of adding fragile global regexes that silently alter older recipes.
3. **Specific beats general.** Matching follows a strict precedence chain:
   ```text
   line-overrides.tsv ──► pinned row in names.tsv ──► handwritten alias ──► exact USDA name ──► token overlap
   ```
   Once a `lookup_name` is `pinned` in `names.tsv`, global parser or scorer changes cannot move its pantry item or USDA food match.
4. **Every rule change shows its blast radius.** `./gradlew diffIngredientSnapshot` compares the live PostgreSQL database against `names.tsv` and `lines.tsv` and fails if any ingredient name or line changed unexpectedly.
5. **Hybrid ML + k-NN suggests; humans (or agents) decide.** `./gradlew suggestIngredientMatches` writes candidate matches to `suggestions.tsv`. Only rows promoted to `pinned` become authoritative data.

---

## Architecture & Data Layers

| Layer | Contents | Identity Key | Lifecycle |
|-------|----------|--------------|-----------|
| **1. Raw source** | Saved scrape JSON (`~/Purecipes/recipeoutput/recipes/*.json`) | `source_url` | Archived outside git; raw lines preserved |
| **2. Content** | `recipes` table (title, instructions, metadata) | `source_url` (stable `recipes.id`) | Inserted once; `recipes.id` is never dropped on rebuild |
| **3. Derived** | `ingredient_groups`, `ingredients`, `ingredient_measurements`, `ingredient_nutrition_matches`, `ingredient_nutrition_contributions`, `nutrition` | `recipe_id` + group/line order | Recomputed idempotently by `rebuildIngredientData` and `calculateRecipeNutrition` |
| **4. Decisions** | `names.tsv`, `lines.tsv`, `line-overrides.tsv` | `lookup_name` / `source_url + raw_text` | Versioned in the private repo checked out at `backend/data/ingredients/` |

### Pipeline Stages

```text
Raw scraped line (JSON / DB)
  │
  ├──► 1. Classify (ingredient / heading / note / equipment)
  │         Code: scripts/scraping/ScrapedIngredientLines.kt, IngredientHeadingRules.kt
  │         Overrides: backend/data/ingredients/line-overrides.tsv
  │
  ├──► 2. Parse (quantity, unit, lookup_name)
  │         Code: feature/nutrition/IngredientLineParser.kt, IngredientLinePhraseRewrites.kt
  │         Overrides: backend/data/ingredients/line-overrides.tsv
  │
  ├──► 3. Pantry Match (IngredientCatalogue chip for "Recipes I can make")
  │         Code: shared/domain IngredientCatalogue, IngredientAliasGroups, IngredientVocabulary
  │         Pins: backend/data/ingredients/names.tsv (pantry_item column)
  │
  └──► 4. USDA Food Match & Grams (calories and macros per recipe)
            Code: NutritionLookupIndex, NutritionFoodNameScorer, NutritionSeedAliases*.kt
            Pins: backend/data/ingredients/names.tsv (fdc_source + fdc_id columns)
```

> [!NOTE]
> Offline CLI tools (`rebuildIngredientData`, `snapshotIngredients`, `diffIngredientSnapshot`, `suggestIngredientMatches`, `calculateRecipeNutrition`, `importNutritionSeed`) live in `backend/src/tools/kotlin/app/purecipes/backend/tools/` (`tools` Gradle source set). Neither those CLI classes nor `tensorflow-java` are bundled into the production Ktor `:backend:shadowJar`.

---

## How `suggestIngredientMatches` Works (k-NN + Frozen USE v4)

When new recipes introduce unseen ingredient names, `./gradlew suggestIngredientMatches` proposes both a `pantry_item` (`IngredientCatalogue` entry) and a USDA food (`fdc_source` + `fdc_id`) into `backend/data/ingredients/suggestions.tsv`.

**Does the ML model train on your edits?**
No neural network weights are trained or fine-tuned locally. Instead, the pipeline uses a **frozen pre-trained Universal Sentence Encoder v4** (`.use_v4`) combined with a **Pinned Nearest-Neighbor (k-NN) Retrieval Index** (`PinnedIngredientIndex`) that reads every `status = pinned` row in `names.tsv` on every run:

1. **Pinned Nearest-Neighbor (k-NN) Retrieval (`PinnedIngredientIndex`):**
   - Indexes all `pinned` rows in `names.tsv`.
   - For any new `lookup_name`, computes a hybrid similarity score against every pinned row using:
     - Synonym-aware token F1 overlap (`aubergine` ↔ `eggplant`, `courgette` ↔ `zucchini`, `prawns` ↔ `shrimp`, `caster` ↔ `granulated`, `coriander` ↔ `cilantro`, `mangetout` ↔ `snow peas`, etc.)
     - Exact head-noun agreement (the trailing noun in English ingredient phrases, with a penalty when head nouns conflict)
     - Character trigram Jaccard similarity
     - Universal Sentence Encoder v4 cosine similarity (when `.use_v4` is present)
   - If a high-confidence pinned neighbor is found (`similarity >= 0.62`), its human-verified `pantry_item` and USDA `fdc_id` are transferred directly to the suggestion (`rationale = pinned-neighbor:<lookup_name>`). Every new pin you commit to `names.tsv` immediately improves future suggestions without retraining any model.
2. **Lexical, Alias & Trigram Candidate Generation (`IngredientSuggestionTool`):**
   - Queries `IngredientAliasGroups`, `NutritionSeedAliases`, trailing head-noun sub-phrases (for example `chopped curly kale` → `curly kale` → `kale`), and PostgreSQL `pg_trgm` similarity across `nutrition_foods` and `nutrition_food_aliases`.
3. **Semantic Reranking with Culinary Guards (`IngredientSuggestionSemanticReranker`):**
   - Embeds the query and candidate USDA descriptions into 512-dimensional vectors using `.use_v4` (`tensorflow-java` SavedModel bundle).
   - Reranks candidates while enforcing culinary compatibility guards: penalizes dish/prepared traps (`soup`, `babyfood`, `crackers`, `seasoning mix`, `frozen Meal`) when the ingredient is a raw staple, and prevents modifier matches (`olive` in `carrot olive oil` matching `Olives, ripe`) from overriding the true head noun (`oil`).

---

## Daily Workflows

### Workflow 1: After Scraping New Recipes

1. **Rebuild derived ingredient data** from the saved JSON archive (`~/Purecipes/recipeoutput/recipes`) and current DB rows:
   ```bash
   PGPASSWORD=postgres ./gradlew rebuildIngredientData
   ```
2. **Check the snapshot diff** to see newly added or unmatched ingredient names:
   ```bash
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot -Psnapshot.failOnDiff=false
   ```
3. **Generate suggestions** for unmatched (or token-guessed) names:
   ```bash
   PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.scope=unmatched
   PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.scope=tokens
   ```
4. **Review `backend/data/ingredients/suggestions.tsv`:**
   - For each row, verify `pantry_item`, `fdc_source`, `fdc_id`, and `fdc_description` against `sample_recipe_title` and `sample_raw_text` (or inspect `alternative_candidates`).
   - Change `status` from `suggested` to `pinned` on verified rows, then promote them into `names.tsv`:
     ```bash
     PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.accept=true
     ```
5. **Recalculate nutrition and write the updated snapshot:**
   ```bash
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```
6. **Commit the updated TSVs** inside `backend/data/ingredients/` (private data repo).

### Workflow 2: Fixing a Single Misclassified Line or Group Heading

When a specific line in one recipe is a section heading (for example `Chimichurri` or `For the glaze`) or parses poorly, do **not** write a risky global rule. Add a row to `backend/data/ingredients/line-overrides.tsv`:

1. Look up the recipe's `source_url` and exact `raw_text` in `lines.tsv`.
2. Add a tab-separated row to `backend/data/ingredients/line-overrides.tsv` setting `kind` (`heading`, `note`, `equipment`, or `ingredient`) or overriding `group_name`, `quantity`, `unit`, or `lookup_name`.
3. Apply and verify:
   ```bash
   PGPASSWORD=postgres ./gradlew rebuildIngredientData -Prebuild.onlyChangedLines=true
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```

### Workflow 3: Improving a Global Scraper or Parser Rule

Only change global code (`scripts/scraping/ScrapedIngredientLines.kt`, `IngredientLineParser.kt`, `IngredientLinePhraseRewrites.kt`) when one pattern cleanly fixes many lines across the corpus:

1. Edit the rule and run offline unit tests:
   ```bash
   kotlin scripts/scraping/recipe_site_scraper_ingredient_test.main.kts
   ./gradlew :backend:test :shared:domain:jvmTest
   ```
2. Rebuild derived data and inspect the corpus-wide blast radius:
   ```bash
   PGPASSWORD=postgres ./gradlew rebuildIngredientData
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot -Psnapshot.failOnDiff=false
   ```
3. Review every changed line reported by `diffIngredientSnapshot`. Note that `pinned` rows in `names.tsv` will never lose their food/pantry match unless their parsed `lookup_name` itself changed.
4. Pin any newly exposed `lookup_name` rows, run `snapshotIngredients`, and verify `diffIngredientSnapshot` exits `0`.

### Workflow 4: Release Database Sync

See [`RELEASE-DATA-SYNC.md`](RELEASE-DATA-SYNC.md) for the step-by-step release runbook (SSH tunnel, `./gradlew syncNutritionCatalogue`, JSON recipe import, `./gradlew rebuildIngredientData`, `./gradlew diffIngredientSnapshot`, and enrichment).

---

## Files in `backend/data/ingredients/`

All files are UTF-8, tab-separated (`.tsv`), with a header row, sorted by their primary key columns so `git diff` is deterministic. Double quotes inside descriptions are normalized to `″` (`\u2033`) and written without CSV quote wrapping.

### 1. `names.tsv` — Verified Pins & Lookup Name Snapshot

One row per distinct normalized ingredient name (`NutritionNameNormalizer.forLookup`).

| Column | Meaning |
|--------|---------|
| `lookup_name` | Normalized lookup key (primary key, sorted lexicographically) |
| `status` | `pinned` (human/agent verified; overrides rules) or `auto` (rule output) |
| `pantry_item` | Canonical `IngredientCatalogue` name (for example `Olive Oil`), or empty if none |
| `fdc_source` | USDA dataset key: `fdc_foundation` or `fdc_sr_legacy` (or empty if intentionally unmatched, such as ice) |
| `fdc_id` | External USDA FDC ID (`nutrition_foods.source_id`, **never** the internal serial `nutrition_foods.id`) |
| `fdc_description` | Exact `nutrition_foods.display_name` from the database (with `"` replaced by `″`) |
| `match_source` | `pinned` for pinned rows; `alias`, `name`, `tokens`, or empty for `auto` rows |
| `line_count` | Number of recipe lines currently resolving to this `lookup_name` |

### 2. `lines.tsv` — Full Corpus Line Snapshot

One row per scraped ingredient line across all recipes. Catches classification, splitting, and parser drift.

| Column | Meaning |
|--------|---------|
| `source_url` | Recipe URL (stable across databases) |
| `group_index`, `line_index` | 0-based group and line position |
| `raw_text` | Ingredient line text |
| `kind` | `ingredient`, `heading`, `note`, or `equipment` |
| `group_name` | Resolved `ingredient_groups.name` |
| `quantity`, `unit`, `lookup_name` | Parsed measurement and normalized lookup key |
| `requirement`, `alternative_group_key` | `REQUIRED`, `OPTIONAL`, or `ALTERNATIVE` + OR-group key |

### 3. `line-overrides.tsv` — Per-Line Exceptions

Keyed by `source_url` + `raw_text`. Non-empty columns override `kind`, `group_name`, `quantity`, `unit`, or `lookup_name` during `rebuildIngredientData`, `calculateRecipeNutrition`, and `snapshotIngredients`.

### 4. `suggestions.tsv` — Offline Review Queue

Generated by `./gradlew suggestIngredientMatches`. Its first 8 columns match `names.tsv` (`status = suggested`), followed by `confidence`, `current_*` columns, `sample_recipe_title`, `sample_raw_text`, `alternative_candidates`, and `rationale`.

---

## Gradle Commands Reference

| Command | Purpose | Key `-P` Properties |
|---------|---------|---------------------|
| `./gradlew rebuildIngredientData` | Recomputes `ingredient_groups`, `ingredients`, `ingredient_measurements`, and nutrition from saved scrape JSON (`~/Purecipes/recipeoutput/recipes`) + DB while preserving `recipes.id` | `-Prebuild.jsonDir=...`, `-Prebuild.onlyChangedLines=true`, `-Prebuild.noJson=true`, `-Prebuild.restoreLinesTsv=...` |
| `./gradlew calculateRecipeNutrition` | Parses ingredient lines, applies `names.tsv` pins and aliases, and writes `ingredient_measurements`, `ingredient_nutrition_matches`, `ingredient_nutrition_contributions`, and `nutrition` | `-Pnutrition.allRecipes=true`, `-Pnutrition.recipeId=42`, `-Pnutrition.recipeIds=1,2,3`, `-Pnutrition.verbose=true` |
| `./gradlew snapshotIngredients` | Exports `names.tsv` and `lines.tsv` from the current DB while preserving `pinned` rows in `names.tsv` | `-Psnapshot.dir=...`, `-Psnapshot.seedPins=true`, `-Psnapshot.failOnDiff=true` |
| `./gradlew diffIngredientSnapshot` | Compares current DB state against `names.tsv` and `lines.tsv` without modifying files (exits non-zero on any diff) | `-Psnapshot.failOnDiff=false` (report-only) |
| `./gradlew suggestIngredientMatches` | Generates candidate `pantry_item` + USDA food matches into `suggestions.tsv`, or promotes reviewed `pinned` rows into `names.tsv` | `-Psuggest.scope=unmatched\|tokens\|unpinned\|all`, `-Psuggest.query=...`, `-Psuggest.limit=200`, `-Psuggest.modelPath=...`, `-Psuggest.accept=true` |
| `./gradlew syncNutritionCatalogue` | Syncs `nutrition_foods`, `nutrition_food_aliases`, and `nutrition_food_measures` from local DB to the target DB in `PURECIPES_DB_*` by `(source_name, source_id)` | `-PnutritionSync.dryRun=true`, `-PnutritionSync.sourceUrl=...`, `-PnutritionSync.sourceUser=...`, `-PnutritionSync.sourcePassword=...` |

---

## One-Time Setup on a New Machine

### 1. Private Decision-Data Repository (`backend/data/ingredients/`)

`/backend/data/ingredients/` is listed in the root `.gitignore` of the public `Purecipes` repository so curated pins and corpus snapshots stay private. It is versioned as its own standalone private Git repository (`herrbert74/purecipes-ingredients-data`):

```bash
git clone git@github.com:herrbert74/purecipes-ingredients-data.git backend/data/ingredients
```

To commit and push decision updates after a pinning session:

```bash
cd backend/data/ingredients
git status
git add names.tsv lines.tsv line-overrides.tsv
git commit -m "Pin new scraped ingredients"
git push
cd ../../..
```

### 2. Local Universal Sentence Encoder v4 Model (`.use_v4/`)

`suggestIngredientMatches` automatically uses the TensorFlow SavedModel in `.use_v4/` at the repository root when present (also git-ignored). To download and unpack it on a new machine:

```bash
mkdir -p .use_v4
curl -L "https://tfhub.dev/google/universal-sentence-encoder/4?tf-hub-format=compressed" | tar -xzv -C .use_v4
```

Verify that `.use_v4/saved_model.pb` and `.use_v4/variables/` exist. If `.use_v4/` is absent, `suggestIngredientMatches` still runs using the pinned k-NN lexical/trigram index and PostgreSQL `pg_trgm`.
