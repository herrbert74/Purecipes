# USDA FoodData Central (MVP nutrition source)

## Licence decision (feature 15)

- **Source:** [USDA FoodData Central](https://fdc.nal.usda.gov/)
- **Licence:** [CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/) — no copyright restriction; no permission required for use or redistribution.
- **Attribution:** USDA requests attribution to FoodData Central as the data source. This is a courtesy, not a share-alike obligation.
- **MVP scope:** USDA FoodData Central only. **Open Food Facts is deferred** until we accept its Open Database Licence share-alike implications for a combined proprietary database.

## Product attribution (TODO)

Decide where attribution appears in the app (for example About, nutrition card footer, or legal page). Until then, this document records the obligation for implementers.

Suggested wording:

> Nutrition estimates for some ingredients use data from USDA FoodData Central.

## Importer inputs

For the seed importer (feature 15, step 4), download from FoodData Central:

- **Foundation Foods** (preferred first subset)
- **SR Legacy** (optional, for broader generic foods)

Keep only MVP nutrients: calories, protein, carbohydrates, fat, fibre, sugar, and sodium. Normalize to canonical foods per 100 g.

Downloads are not committed to the repository; the importer reads local JSON paths supplied at run time.

## Seed import (backend)

Run against a Postgres database configured with the usual `PURECIPES_DB_*` environment variables.

The importer auto-detects `FoundationFoods` vs `SRLegacyFoods` from the JSON root key. Import **SR Legacy first**, then **Foundation** (do not use `-Pnutrition.replace=true` on the second run). Catalogue aliases are reseeded after each import using all foods in the database; Foundation wins ties over SR Legacy for the same `fdcId`.

```bash
# 1) SR Legacy (clears nutrition seed tables)
./gradlew importNutritionSeed \
  -Pnutrition.fdcJson=/path/to/FoodData_Central_sr_legacy_food_json_2018-04.json \
  -Pnutrition.replace=true

# 2) Foundation (adds higher-quality foods, refreshes aliases)
./gradlew importNutritionSeed \
  -Pnutrition.fdcJson=/path/to/FoodData_Central_foundation_food_json_YYYY-MM-DD.json
```

Dry run (no database writes, prints match coverage):

```bash
./gradlew importNutritionSeed \
  -Pnutrition.fdcJson=/path/to/FoodData_Central_sr_legacy_food_json_2018-04.json \
  -Pnutrition.dryRun=true
```

Skip alias seeding on a large import (`-Pnutrition.skipAliases=true` or `--skip-aliases`) if you will run another import immediately after.

The importer loads foods with energy (kcal) data, stores per-100g nutrients, imports household measures from FDC portions (plus a small supplemental list), and links pantry catalogue names and handwritten aliases to canonical foods.
