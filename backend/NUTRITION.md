# USDA FoodData Central and recipe nutrition

How to load USDA foods, calculate recipe nutrition, and inspect food-table match gaps. Table and column meanings live in [`DATABASE.md`](DATABASE.md).

## Licence decision (feature 14)

- **Source:** [USDA FoodData Central](https://fdc.nal.usda.gov/)
- **Licence:** [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/) — no copyright restriction; no permission required for use or redistribution.
- **Attribution:** USDA requests attribution to FoodData Central as the data source. This is a courtesy, not a share-alike obligation.
- **MVP scope:** USDA FoodData Central only. **Open Food Facts is deferred** until we accept its Open Database Licence share-alike implications for a combined proprietary database.

## Product attribution

The app shows this wording on nutrition cards, the nutrition facts dialog, and the About screen:

> Nutrition estimates for some ingredients use data from USDA FoodData Central.

## Importer inputs

For the seed importer (feature 14, step 4), download from FoodData Central:

- **Foundation Foods** (preferred first subset)
- **SR Legacy** (optional, for broader generic foods)

Keep only MVP nutrients: calories, protein, carbohydrates, fat, fibre, sugar, and sodium. Normalize to canonical foods per 100 g.

Downloads are not committed to the repository; the importer reads local JSON paths supplied at run time. The commands below use the files on this machine.

## Seed import

Run against a Postgres database configured with the usual `PURECIPES_DB_*` environment variables.

The importer auto-detects `FoundationFoods` vs `SRLegacyFoods` from the JSON root key. Import **SR Legacy first**, then **Foundation** (do not use `-Pnutrition.replace=true` on the second run). Catalogue aliases are reseeded after each import using all foods in the database; Foundation wins ties over SR Legacy for the same `fdcId`.

```bash
./gradlew importNutritionSeed -Pnutrition.fdcJson=/Users/zsoltbertalan/Documents/purecipes/fooddata/FoodData_Central_sr_legacy_food_json_2018-04.json -Pnutrition.replace=true
./gradlew importNutritionSeed -Pnutrition.fdcJson=/Users/zsoltbertalan/Documents/purecipes/fooddata/FoodData_Central_foundation_food_json_2026-04-30.json
```

The first command clears nutrition seed tables. The second adds Foundation foods and refreshes aliases.

Dry run (no database writes, prints match coverage):

```bash
./gradlew importNutritionSeed -Pnutrition.fdcJson=/Users/zsoltbertalan/Documents/purecipes/fooddata/FoodData_Central_sr_legacy_food_json_2018-04.json -Pnutrition.dryRun=true
```

Skip alias seeding on a large import (`-Pnutrition.skipAliases=true` or `--skip-aliases`) if you will run another import immediately after.

The importer loads foods with energy (kcal) data, stores per-100g nutrients, imports household measures from FDC portions (plus a small supplemental list), and links pantry catalogue names and handwritten aliases to canonical foods.

SR Legacy JSON sets `measureUnit` to `undetermined` and puts the unit in `modifier`. Import reads that modifier and stores only household units the recipe parser uses, including count portions (`fruit`, `whole`, `each`, `medium`) as `piece`. Mass units and unusable portions (`cake`, `NLEA serving`, slices) are dropped. After an importer change, re-run SR Legacy then Foundation import so `nutrition_food_measures` is rebuilt.

Handwritten aliases are also applied when calculating or estimating nutrition, so adding one in code takes effect on the next `calculateRecipeNutrition` run even if you do not re-import USDA JSON.

If a matched food has no named household measure for the recipe unit, calculation uses water-density defaults for ml / tsp / tbsp / cup. The chosen method is stored as `ingredient_nutrition_contributions.grams_source` (`mass`, `measure`, or `density`). It does not change name-match confidence.

## Recipe nutrition backfill

After seed data is loaded, parse ingredient lines, persist measurements and matches, and calculate totals for existing recipes:

```bash
./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
```

Single recipe with per-line issue details:

```bash
./gradlew calculateRecipeNutrition -Pnutrition.recipeId=42 -Pnutrition.verbose=true
```

Explicit subset:

```bash
./gradlew calculateRecipeNutrition -Pnutrition.recipeIds=1,2,3
```

The calculator writes `ingredient_measurements`, `ingredient_nutrition_matches`, and upserts `nutrition` when at least one ingredient matched. Backfill output lists recipes with partial or missing totals and aggregates unmatched parsed ingredient names so aliases and measures can be improved.

New scraped recipes are calculated automatically when using the recipe scraper with default `--calculate-nutrition true` (see [`../scripts/scraping/README.md`](../scripts/scraping/README.md)).

## Ingredient food-table matching report

Read stored parse and match rows without recalculating nutrition. With no extra params, the report prints to stdout only. `-Preport.output` also writes the same text to a file:

```bash
./gradlew reportIngredientFoodMatches
./gradlew reportIngredientFoodMatches -Preport.output=/tmp/ingredient-food-match-report.txt
```

The report counts countable ingredient lines (required, plus one option per alternative group) that were never parsed, were not measurable, had no food match, or had a food but no gram weight. It also lists frequent unmatched names and recipes whose nutrition totals came from the website (`scraped`) rather than calculation.

Run `calculateRecipeNutrition` first when you want the report to reflect a fresh matcher pass.

## Typical local sequence

```bash
./gradlew importNutritionSeed -Pnutrition.fdcJson=/Users/zsoltbertalan/Documents/purecipes/fooddata/FoodData_Central_sr_legacy_food_json_2018-04.json -Pnutrition.replace=true
./gradlew importNutritionSeed -Pnutrition.fdcJson=/Users/zsoltbertalan/Documents/purecipes/fooddata/FoodData_Central_foundation_food_json_2026-04-30.json
./gradlew calculateRecipeNutrition -Pnutrition.allRecipes=true
./gradlew reportIngredientFoodMatches -Preport.output=/tmp/ingredient-food-match-report.txt
```
