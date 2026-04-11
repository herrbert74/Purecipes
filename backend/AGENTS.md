# Backend Agent Guidelines

Backend-specific guidance for maintaining the Purecipes Ktor server and database layer.

## Rule: Database Schema Synchronization with Recipe Scraper

The backend database schema (`src/main/kotlin/com/purecipes/db/SchemaSql.kt`) is the authoritative deployment source for the application's data model. However, schema changes can originate from either:
- Backend API requirements (adding new fields needed by the backend)
- Scraper requirements (new recipe attributes to extract and store)

When modifying the database schema:

1. **Update schema definition** - Add or modify SQL table definitions in `SchemaSql.kt`
2. **Update migration statements** - Add new `ALTER TABLE` or other migration SQL statements to `Db.kt`'s `ensureSchema()` function
3. **Sync the scraper** - The recipe scraper script at `scripts/recipe_site_scraper.main.kts` maintains its own embedded schema and insertion logic. Review and update:
   - The `ensureSchema()` function and SQL statements in the scraper
   - The `saveRecipe()` function to insert values into new columns
   - The `RecipeData` class to include new fields parsed from recipes

**Why this matters**: The scraper runs independently and can insert data into the database concurrently with backend operations. If the schema diverges between backend and scraper, one or both will fail when trying to read/write mismatched columns.

**Verification**: After schema changes, test both directions:
- Backend can read recipes inserted by the scraper with new columns populated
- Scraper can insert recipes using the updated schema
- Run backend's `ensureSchema()` to verify migrations apply successfully

## Rule: Recipe Data Consistency

Recipe data flows through two pipelines:

1. **Web Scraper Pipeline** (`scripts/recipe_site_scraper.main.kts`):
   - Fetches recipes from external websites
   - Scrapes and normalizes recipe data
   - Inserts into database via JDBC

2. **API Pipeline** (Ktor backend):
   - Serves recipe data via REST/GraphQL endpoints
   - May load, transform, or enrich recipes

Both pipelines deposit into the same database, so ensure:
- Column types and constraints are consistently enforced in both code paths
- NULL/NOT NULL defaults align between scraper and backend queries
- Any business logic constraints (e.g., duplicate detection via `source_url UNIQUE`) exist in both layers
