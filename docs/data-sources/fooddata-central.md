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

Downloads are not committed to the repository; the importer reads local CSV/JSON paths supplied at run time, or imports from a generated checked-in seed file after the first import.
