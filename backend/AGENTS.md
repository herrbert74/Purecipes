# Backend Agent Guidelines

Backend-specific guidance for maintaining the Purecipes Ktor server, database layer, and offline CLI tools.

See [`README.md`](README.md#documentation--pipeline-map) for the full documentation and pipeline map (`DATABASE.md`, `INGREDIENTS.md`, `NUTRITION.md`, `ENRICHMENT.md`, `RELEASE-DATA-SYNC.md`, `EDITORIAL-SHELVES.md`).

## Rule: Source Sets (`main` vs `tools`)

`:backend` has two Kotlin source sets in [`build.gradle.kts`](build.gradle.kts):

1. **`main` (`src/main/kotlin`)** — The production Ktor server packaged into `:backend:shadowJar`. Keep offline CLI utilities and heavy ML dependencies out of `main`.
2. **`tools` (`src/tools/kotlin/app/purecipes/backend/tools/`)** — Offline CLI entry points and helpers (`importScrapedJson`, `rebuildIngredientData`, `snapshotIngredients`, `diffIngredientSnapshot`, `suggestIngredientMatches`, `calculateRecipeNutrition`, `importNutritionSeed`, `syncNutritionCatalogue`, `enrichRecipes`, `deleteAccount`, and report tasks) plus `"toolsImplementation"(libs.tensorflowJava)`.

When adding or modifying offline CLI tools, place them under `src/tools/kotlin/app/purecipes/backend/tools/` so they are excluded from the production server JAR.

## Rule: Database Schema Synchronization

`src/main/kotlin/app/purecipes/backend/db/SchemaSql.kt` and `Db.ensureSchema()` are the single authoritative schema definition used by the server and all `./gradlew` offline tools (including `./gradlew importScrapedJson`).

When modifying the database schema:

1. Add or update table definitions in `SchemaSql.kt` and additive `ALTER TABLE … IF NOT EXISTS` migrations in `Db.ensureSchema()`.
2. If new recipe columns are populated during scraping or JSON import, update `ScrapedRecipeJsonImporter.kt` and the web-mode insert logic in [`../scripts/scraping/AGENTS.md`](../scripts/scraping/AGENTS.md#rule-recipe-scraper-schema-synchronization).

## Rule: Ingredient Pipeline & Pin Before Rule

When fixing ingredient classification, parsing, pantry matching, or USDA nutrition matching on scraped recipes, follow [`INGREDIENTS.md`](INGREDIENTS.md) and the [`ingredient-matching`](../.agents/skills/ingredient-matching/SKILL.md) skill:
- Prefer pinning individual lookup names in `backend/data/ingredients/names.tsv` or overriding single recipe lines in `backend/data/ingredients/line-overrides.tsv` over adding fragile global rules.
- Whenever a global rule in `ScrapedIngredientLines.kt`, `IngredientLineParser.kt`, or `NutritionFoodNameScorer.kt` is changed, run `./gradlew rebuildIngredientData` and `./gradlew diffIngredientSnapshot` to inspect the full corpus blast radius.
