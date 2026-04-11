# Scripts Agent Guidelines

Guidelines for maintaining and extending the Purecipes recipe scraper and related scripts.

## Rule: Recipe Scraper Schema Synchronization

The backend database schema (`backend/src/main/kotlin/com/purecipes/db/SchemaSql.kt`) is the authoritative deployment source. However, schema changes can originate from either:
- Backend API requirements (new fields needed by the API)
- Scraper requirements (new recipe attributes to extract and store)

The scraper script (`recipe_site_scraper.main.kts`) embeds its own database schema and insertion logic and runs independently of the backend codebase.

When the backend database schema changes (from either direction):

1. **Verify backend schema first** - Check `SchemaSql.kt` for all table, column, and type definitions
2. **Update scraper schema** - Locate SQL `CREATE TABLE` and `ALTER TABLE` statements in the scraper (in `ensureSchema()`) and align them exactly:
   - Add new columns with matching types, constraints, and defaults
   - Update column sizes for VARCHAR limits
   - Maintain NOT NULL / NULL alignment
3. **Update scraper insertion logic** - Ensure `saveRecipe()` function and `RecipeData` class include all required columns
4. **Test roundtrip** - Import previously saved JSON files (`--mode json`) to verify all columns are correctly populated

**Why this matters**: The scraper and backend can insert/read data concurrently. If the schema diverges, one or both will fail when encountering mismatched columns or types.

## Rule: Per-Site URL Pattern Configuration

Website URL patterns are defined in the `knownWebsites` map at the top of the scraper script. Each site requires a unique regex pattern to identify recipe pages.

When adding support for a new website:

1. **Add SiteConfig entry** to `knownWebsites` with:
   - Website base URL
   - Regex pattern for recipe page URLs (test thoroughly, as overly broad patterns cause false positives)
2. **Test URL filtering** with actual website URLs to verify the regex correctly matches recipe pages and filters out listing/category pages
3. **Document the pattern** with inline comments if the regex is complex

Example patterns:
- allrecipes: `^/recipe/\d+/` (numeric recipe IDs)
- bonappetit: `^/recipe/[^/]+$` (slug-based URLs, single path segment)
- epicurious: `^/recipes/food/views/` (specific path prefix)
- mob: `^/recipes/(?!collections|categories)[^/]+$` (negative lookahead to exclude listing pages)

## Rule: Python Scraping Integration

The scraper uses embedded Python code executed via `ProcessBuilder` to extract recipe data using the `recipe_scrapers` library. The Python snippet handles:
- Fetching HTML with proper User-Agent headers
- Removing empty JSON-LD blocks that cause parser crashes (site-specific workaround for foodnetwork.co.uk)
- Parsing Recipe schema microdata
- Converting to JSON output

**Known issues and workarounds**:
- Some sites (foodnetwork.co.uk) include empty `<script type="application/ld+json"></script>` tags that crash the `extruct` parser before reaching valid Recipe blocks. The workaround strips empty JSON-LD blocks via regex before parsing.
- The `recipe_scrapers` library version 15.11.0 does not support `wild_mode` parameter; use `scrape_html()` instead of `scrape_me()` for manual HTML.

When updating the Python code, test against multiple recipe pages per site to ensure robustness.

## Rule: Mode-Specific Behavior

The scraper supports two operating modes:

- **web mode** (`--mode web`):
  - Discovers recipe URLs from websites
  - Scrapes each URL with Python/recipe_scrapers
  - Saves JSON files to disk (naming convention: `{DB_ID}-{recipe_slug}.json`)
  - Inserts into database, using DB ID for file naming (idempotent, no duplicates)

- **json mode** (`--mode json`):
  - Reads previously saved JSON files from `{output_dir}/recipes/`
  - Parses and inserts into database
  - Useful for re-importing batches without re-scraping (faster, no network calls)

Both modes share the same `saveRecipe()` insertion logic, so they must remain in sync regarding schema and duplicate detection.

## Rule: Change Validation for Scraper Changes

For scraper script changes:
- **Detekt compliance**: Run `./gradlew detektAll` to verify linting rules pass
- **Functional testing**: Test changes against actual recipe websites (or use `--mode json` with previously saved files for offline testing)
- **Schema alignment**: After backend schema changes, verify scraper schema and insertion logic still work end-to-end
