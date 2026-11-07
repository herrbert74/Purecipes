---
name: ingredient-matching
description: Review, match, pin, and override ingredient lines and names using the reproducible ingredient pipeline.
---

# Skill: Ingredient Matching & Pipeline Maintenance

Use this skill whenever you need to:
1. Match or pin unmatched/new recipe ingredients (`names.tsv` / `suggestions.tsv`).
2. Fix a wrong pantry item (`IngredientCatalogue`) or wrong USDA food match (`fdc_id`).
3. Fix a misclassified recipe line (section heading, note, equipment) or broken line parse (`line-overrides.tsv`).
4. Modify scraper or parser rules (`ScrapedIngredientLines.kt`, `IngredientLineParser.kt`) and verify zero regressions across the corpus.

For architecture background, see [`../../../backend/INGREDIENTS.md`](../../../backend/INGREDIENTS.md).
For detailed domain rulings, batch review steps, and error recovery, read:
- [`references/review-rules.md`](references/review-rules.md) — **Mandatory culinary & parser rules** (read before editing pins or parser rules)
- [`references/batch-review.md`](references/batch-review.md) — Step-by-step batch review workflow
- [`references/troubleshooting.md`](references/troubleshooting.md) — Fixing `diffIngredientSnapshot` failures and TSV corruption

---

## Step 0: Choose the Right Action (Decision Tree)

Follow this strict order (**Pin Before Rule**):

```text
What are you fixing?
│
├── 1. ONE recipe has a bad line (e.g. "Chimichurri" is a heading, or one weird line won't parse)
│     └──► Edit backend/data/ingredients/line-overrides.tsv (DO NOT edit Kotlin code)
│          Jump to Playbook 3.
│
├── 2. An ingredient lookup_name has a missing or wrong pantry_item or USDA food match
│     └──► Pin the row in backend/data/ingredients/names.tsv (status = pinned)
│          Jump to Playbook 1 (single name) or Playbook 2 (batch suggestions).
│
└── 3. A general formatting/parsing bug affects MANY recipes (e.g. dangling prep words)
      └──► First read references/review-rules.md, edit Kotlin rules + unit tests,
           then run rebuildIngredientData + diffIngredientSnapshot.
           Jump to Playbook 4.
```

---

## Hard Constraints (NEVER Violate These)

1. **NEVER put `"pinned"` or `"internal"` in the `fdc_source` column of `names.tsv`.**
   - Column 4 (`fdc_source`) **MUST** be the exact `nutrition_foods.source_name` from PostgreSQL (`fdc_foundation` or `fdc_sr_legacy`, or empty `""` for zero-calorie non-foods like `ice`).
   - Column 2 (`status`) and Column 7 (`match_source`) are `"pinned"`.
2. **NEVER use internal `nutrition_foods.id` in `names.tsv`.**
   - Column 5 (`fdc_id`) **MUST** be the external USDA `nutrition_foods.source_id` (e.g. `789828` or `171705`).
3. **NEVER wrap TSV fields in CSV quotes (`"..."`).**
   - `IngredientSnapshotExporter` replaces ASCII `"` with Unicode double prime `″` (`\u2033`) and writes raw tab-separated columns. If you use Python's `csv.writer`, it will wrap fields containing `"` in `"..."` and break `./gradlew diffIngredientSnapshot`.
4. **NEVER use `grep -P` or `sudo` in shell commands.**
   - macOS BSD `grep` does not support `-P`. Use `awk -F'\t'` or `grep -E`.
   - Connect to PostgreSQL with `PGPASSWORD=postgres psql -h localhost -U postgres -d purecipes`.
5. **NEVER finish a task until `./gradlew diffIngredientSnapshot` exits `0` with `+0 added, -0 removed, ~0 changed`.**

---

## TSV Schema Reference (`backend/data/ingredients/names.tsv`)

Every row in `names.tsv` has **8 tab-separated columns**:

| Col # | Column Name | Allowed Values when Pinned | Example |
|-------|-------------|----------------------------|---------|
| 1 | `lookup_name` | Lowercase normalized name (sorted lexicographically) | `olive oil` |
| 2 | `status` | `pinned` | `pinned` |
| 3 | `pantry_item` | Exact `IngredientCatalogue` title-cased name, or empty `""` | `Olive Oil` |
| 4 | `fdc_source` | `fdc_foundation`, `fdc_sr_legacy`, or empty `""` | `fdc_foundation` |
| 5 | `fdc_id` | External `nutrition_foods.source_id` integer, or empty `""` | `748608` |
| 6 | `fdc_description` | Exact `nutrition_foods.display_name` with `"` replaced by `″` | `Oil, olive, extra virgin` |
| 7 | `match_source` | `pinned` | `pinned` |
| 8 | `line_count` | Integer count of lines using this `lookup_name` | `412` |

---

## Playbooks

### Playbook 1: Pin or Fix Specific Lookup Names in `names.tsv`

1. **Inspect current row(s) in `names.tsv`:**
   ```bash
   awk -F'\t' 'NR==1 || $1 == "olive oil"' backend/data/ingredients/names.tsv
   ```
2. **Look up the exact USDA food in PostgreSQL (`source_name`, `source_id`, `display_name`):**
   ```bash
   PGPASSWORD=postgres psql -h localhost -U postgres -d purecipes -c "
   SELECT source_name, source_id, REPLACE(display_name, '\"', '″') AS display_name
   FROM nutrition_foods
   WHERE display_name ILIKE '%olive%'
   ORDER BY source_name ASC, display_name ASC
   LIMIT 15;
   "
   ```
   *(Or run `PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.query="olive oil"` and inspect `backend/data/ingredients/suggestions.tsv`.)*
3. **Verify the `pantry_item` spelling** against `shared/domain/src/commonMain/kotlin/app/purecipes/shared/domain/ingredient/IngredientCatalogue.kt` (or leave `pantry_item` empty `""` if no catalogue chip applies).
4. **Update `names.tsv`** (keeping rows sorted by `lookup_name` and replacing any `"` with `″`).
5. **Apply to the database and verify 0 diff:**
   ```bash
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```

---

### Playbook 2: Generate & Review Candidate Suggestions (`suggestions.tsv`)

1. **Generate suggestions** (uses the Pinned Nearest-Neighbor `PinnedIngredientIndex` + `.use_v4` semantic reranker automatically):
   ```bash
   # Unmatched lookup names only:
   PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.scope=unmatched

   # Or token-guessed lookup names:
   PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.scope=tokens
   ```
2. **Read `backend/data/ingredients/suggestions.tsv` and apply [`references/review-rules.md`](references/review-rules.md):**
   - Check `sample_recipe_title`, `sample_raw_text`, `rationale` (e.g. `pinned-neighbor:<name>`), and `alternative_candidates`.
   - If the proposed `pantry_item`, `fdc_source`, `fdc_id`, and `fdc_description` are accurate: change column 2 (`status`) from `suggested` to `pinned`, and set column 7 (`match_source`) to `pinned`.
   - If an entry in `alternative_candidates` is better: look up its exact `source_name`, `source_id`, and `display_name` in `nutrition_foods`, write them into columns 4–6, and set `status = pinned` and `match_source = pinned`.
3. **Promote accepted pins into `names.tsv`:**
   ```bash
   PGPASSWORD=postgres ./gradlew suggestIngredientMatches -Psuggest.accept=true
   ```
4. **Recalculate nutrition, refresh line counts, and verify 0 diff:**
   ```bash
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```

---

### Playbook 3: Override an Idiosyncratic Recipe Line (`line-overrides.tsv`)

Use when a single recipe has a section heading (`Chimichurri`), note, equipment line, or unparseable line that should **not** become a global rule:

1. **Find the exact `source_url` and `raw_text` in `lines.tsv`:**
   ```bash
   awk -F'\t' 'NR==1 || $4 ~ /Chimichurri/' backend/data/ingredients/lines.tsv
   ```
2. **Add a row to `backend/data/ingredients/line-overrides.tsv`:**
   Columns: `source_url`, `raw_text`, `kind` (`ingredient`, `heading`, `note`, `equipment`), `group_name`, `quantity`, `unit`, `lookup_name`.
3. **Rebuild changed lines, recalculate nutrition, and refresh snapshot:**
   ```bash
   PGPASSWORD=postgres ./gradlew rebuildIngredientData -Prebuild.onlyChangedLines=true
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```

---

### Playbook 4: Modify Global Scraper / Parser Rules Safely

Before editing `scripts/scraping/ScrapedIngredientLines.kt`, `IngredientLineParser.kt`, or `IngredientLinePhraseRewrites.kt`, **read [`references/review-rules.md`](references/review-rules.md)**.

1. **Make the minimal code change and add unit test cases** to `scripts/scraping/recipe_site_scraper_ingredient_test.main.kts` or `IngredientLineParserTest.kt`.
2. **Run fast offline tests & Detekt:**
   ```bash
   kotlin scripts/scraping/recipe_site_scraper_ingredient_test.main.kts
   ./gradlew :backend:test :shared:domain:jvmTest detektAll
   ```
3. **Rebuild the database and inspect the corpus diff:**
   ```bash
   PGPASSWORD=postgres ./gradlew rebuildIngredientData
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot -Psnapshot.failOnDiff=false
   ```
4. **Inspect every changed `lookup_name`:**
   - Any newly created `lookup_name` must be pinned in `names.tsv` (via Playbook 1 or 2).
   - If the rule broke existing clean lines, revert or narrow the rule immediately.
5. **Finalize snapshot and verify 0 diff:**
   ```bash
   PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   PGPASSWORD=postgres ./gradlew snapshotIngredients
   PGPASSWORD=postgres ./gradlew diffIngredientSnapshot
   ```

---

## Mandatory Completion Checklist

Before reporting completion to the user, verify **all** of the following:

- [ ] `PGPASSWORD=postgres ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true` reports `Partial estimates: 0`, `Without totals: 0`, `Ingredient issues: 0`.
- [ ] `PGPASSWORD=postgres ./gradlew diffIngredientSnapshot` exits `0` with `Names: +0 added, -0 removed, ~0 changed` and `Lines: +0 added, -0 removed, ~0 changed`.
- [ ] If Kotlin or Gradle files were touched, `./gradlew detektAll` and `./gradlew :backend:test` pass with 0 errors.
