# Batch Ingredient Review Workflow

Follow this step-by-step recipe when reviewing a batch of new or unmatched ingredients (for example after scraping a new recipe site or running `rebuildIngredientData`).

---

## Step 1: Rebuild and Inspect the Queue

1. Make sure derived data reflects the latest JSON and overrides:
   ```bash
   ./gradlew rebuildIngredientData
   ```
2. Generate `suggestions.tsv` for unmatched lookup names:
   ```bash
   ./gradlew suggestIngredientMatches -Psuggest.scope=unmatched
   ```
3. Count how many rows are in `backend/data/ingredients/suggestions.tsv`:
   ```bash
   wc -l backend/data/ingredients/suggestions.tsv
   ```

---

## Step 2: Inspect Candidates in Manageable Batches (25–50 Rows)

Do **not** try to eyeball 500 rows at once. Inspect 25–50 rows at a time with all context columns visible:

```bash
awk -F'\t' 'NR==1 || (NR>=2 && NR<=51) {
  printf "%d | name=%s | pantry=%s | fdc=%s:%s (%s) | conf=%s | why=%s | sample=%s [%s] | alt=%s\n",
    NR, $1, $3, $4, $5, $6, $9, $18, $16, $15, $17
}' backend/data/ingredients/suggestions.tsv
```

For each row, check:
1. **Is `lookup_name` actually a parser artifact or section heading?**
   - Example: `lookup_name` contains dangling prep text (`"butter melted cooled"`) or is a section heading (`"for the dressing"`).
   - If it is a single idiosyncratic line, add an override to `backend/data/ingredients/line-overrides.tsv`.
   - If it is a general parser suffix that failed to strip across multiple recipes, note it for a parser rule update (see `SKILL.md` Playbook 4).
2. **Is `rationale` a high-confidence `pinned-neighbor:<name>` match?**
   - When `rationale` starts with `pinned-neighbor:`, `PinnedIngredientIndex` found a close human-verified pin in `names.tsv` and copied its `pantry_item` and `fdc_id`. Verify that the head noun and culinary meaning match.
3. **Are both `pantry_item` and `fdc_source`/`fdc_id` accurate?**
   - Check [`review-rules.md`](review-rules.md) for common traps (`half-and-half`, dried vs fresh chiles, liquid stock vs dry bouillon, `"leaves"`).
   - If the top suggestion is wrong, check column 17 (`alternative_candidates`) or query `nutrition_foods` directly:
     ```bash
     PGPASSWORD=postgres psql -h localhost -U postgres -d purecipes -c "
     SELECT source_name, source_id, REPLACE(display_name, '\"', '″') AS display_name
     FROM nutrition_foods
     WHERE display_name ILIKE '%<keyword>%'
     ORDER BY source_name ASC, display_name ASC
     LIMIT 15;
     "
     ```

---

## Step 3: Promote Verified Rows into `names.tsv`

Once a batch of rows in `suggestions.tsv` has verified `pantry_item`, `fdc_source`, `fdc_id`, and `fdc_description`:
1. Set column 2 (`status`) to `pinned` and column 7 (`match_source`) to `pinned` for the verified rows.
2. Run:
   ```bash
   ./gradlew suggestIngredientMatches -Psuggest.accept=true
   ```
3. Recalculate nutrition, export the updated snapshot, and check the diff:
   ```bash
   ./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
   ./gradlew snapshotIngredients
   ./gradlew diffIngredientSnapshot
   ```

Repeat Steps 1–3 until `suggestIngredientMatches -Psuggest.scope=unmatched` produces 0 actionable unmatched rows and `diffIngredientSnapshot` exits `0`.
