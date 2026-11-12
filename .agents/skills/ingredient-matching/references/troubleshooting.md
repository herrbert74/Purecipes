# Troubleshooting `diffIngredientSnapshot` & TSV Issues

When `./gradlew diffIngredientSnapshot` exits non-zero or `calculateRecipeNutrition` reports incomplete recipes, use this guide to diagnose and fix the exact root cause.

---

## Problem 1: `diffIngredientSnapshot` Reports `~N changed` on Pinned Rows in `names.tsv`

**Why this happens:**
`IngredientSnapshotExporter` verifies every `pinned` row in `names.tsv` against PostgreSQL `nutrition_foods` (`source_name` + `source_id`) and recalculates `line_count`. A diff on a pinned row means one of four things occurred when editing `names.tsv`:

1. **`fdc_source` was overwritten with `"pinned"` or `"internal"`** instead of `fdc_foundation` or `fdc_sr_legacy`.
2. **`fdc_id` was set to a wrong or internal ID** that does not match `fdc_description` in `nutrition_foods`.
3. **`fdc_description` contains ASCII quotes (`"`) or CSV quote wrapping (`"..."`)** instead of Unicode double prime `″` (`\u2033`).
4. **`line_count` changed** because lines were rebuilt or re-parsed without re-running `./gradlew snapshotIngredients`.

**How to inspect the exact mismatch:**
Look at the `~ lookup_name:` lines printed by:
```bash
./gradlew diffIngredientSnapshot -Psnapshot.failOnDiff=false
```
It prints both the file value and the DB value:
```text
~ club strip: pinned|Beef|fdc_sr_legacy|171705|Beef, grass-fed, strip steaks, lean only, raw|pinned|1 -> ...
```

**How to fix:**
1. Look up the canonical `source_name`, `source_id`, and `display_name` in `nutrition_foods`:
   ```bash
   PGPASSWORD=postgres psql -h localhost -U postgres -d purecipes -c "
   SELECT source_name, source_id, REPLACE(display_name, '\"', '″') AS display_name
   FROM nutrition_foods
   WHERE source_id = 171705;
   "
   ```
2. Update `fdc_source`, `fdc_id`, and `fdc_description` in `names.tsv` (ensuring `status = pinned` and `match_source = pinned`).
3. Re-run:
   ```bash
   ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   ./gradlew snapshotIngredients
   ./gradlew diffIngredientSnapshot
   ```

---

## Problem 2: `calculateRecipeNutrition` Reports `Partial estimates` or `Ingredient issues`

**How to find which recipes and lines failed:**
Look at the summary at the bottom of `calculateRecipeNutrition` output, or query PostgreSQL for measurable required lines without a nutrition contribution:

```bash
PGPASSWORD=postgres psql -h localhost -U postgres -d purecipes -c "
SELECT r.id AS recipe_id, r.title, im.raw_text, im.quantity, im.unit, im.parsed_name
FROM ingredient_measurements im
JOIN ingredients i ON i.id = im.ingredient_id
JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
JOIN recipes r ON r.id = ig.recipe_id
LEFT JOIN ingredient_nutrition_contributions inc ON inc.ingredient_id = i.id
WHERE i.requirement != 'OPTIONAL'
  AND im.is_measurable = true
  AND inc.ingredient_id IS NULL
ORDER BY r.id, i.order_index;
"
```

You can also run verbose calculation on a single recipe ID:
```bash
./gradlew calculateRecipeNutrition -Pnutrition.recipeId=42 -Pnutrition.verbose=true
```

**Common causes and fixes:**
- **No food match for `parsed_name`:** Pin the normalized `lookup_name` in `backend/data/ingredients/names.tsv` (see `SKILL.md` Playbook 1).
- **Non-ingredient line (heading/note/equipment) was parsed as a measurable ingredient:** Add a row to `backend/data/ingredients/line-overrides.tsv` with `kind = heading`, `note`, or `equipment` (see `SKILL.md` Playbook 3).

---

## Problem 3: `.use_v4` Semantic Reranker Did Not Run

If `suggestIngredientMatches` does not use semantic embeddings, check that `.use_v4/saved_model.pb` exists at the repository root:
```bash
ls -la .use_v4/saved_model.pb .use_v4/variables
```
If missing, download Universal Sentence Encoder v4:
```bash
mkdir -p .use_v4
curl -L "https://tfhub.dev/google/universal-sentence-encoder/4?tf-hub-format=compressed" | tar -xzv -C .use_v4
```
