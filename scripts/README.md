# Scripts

## Feature 001 - Recipe Website Scraping

`scripts/recipe_site_scraper.main.kts` is the Feature 001 implementation and follows the Kotlin-script MVP direction from `001_recipe-website-scraping.md`.

It orchestrates the full pipeline in Kotlin:

1. Discover URLs from a recipe site (SimpleScraper API), or read URLs from a local file.
2. Filter URLs to recipe-detail paths (default pattern: `/recipe/`).
3. Scrape recipe data with `recipe-scrapers` (invoked one-by-one through Python from the Kotlin script).
4. Save each recipe JSON file under the requested output folder.
5. Either:
   - import into local PostgreSQL (`--mode postgres`), or
   - write an NDJSON import file (`--mode ndjson`).

Batch controls:
- `--max-urls <n>` batch size (default `50`)
- `--precheck-db true|false` skip URLs already in DB before scraping (default `true` in postgres mode)

### Dependencies

```bash
python3 -m pip install recipe-scrapers
```

Kotlin runner requirements:
- Kotlin script runtime (`kotlin` command)
- internet access for first-time `@file:DependsOn` dependency resolution

Default PostgreSQL options (can be overridden):
- `--db-host localhost`
- `--db-port 5432`
- `--db-name purecipes`
- `--db-user postgres`
- `--db-password postgres`

### Run with local PostgreSQL import

```bash
kotlin scripts/recipe_site_scraper.main.kts -- allrecipes ./tmp/allrecipes \
  --mode postgres \
  --simplescraper-api-key "$SIMPLESCRAPER_API_KEY"
```

### Run with file import format output (NDJSON)

```bash
kotlin scripts/recipe_site_scraper.main.kts -- foodnetwork ./tmp/foodnetwork \
  --mode ndjson \
  --import-file ./tmp/foodnetwork/recipes.ndjson \
  --simplescraper-api-key "$SIMPLESCRAPER_API_KEY"
```

### Use a pre-extracted URL list instead of URL discovery

```bash
kotlin scripts/recipe_site_scraper.main.kts -- https://www.seriouseats.com ./tmp/seriouseats \
  --urls-file ./tmp/seriouseats/urls.txt \
  --mode ndjson
```

### Important URL note

- Category/listing URLs (example: `https://www.allrecipes.com/recipes/...`) are not recipe-detail pages and will be filtered/skipped.
- For best results, keep URL lists to recipe pages (typically containing `/recipe/`).

### Next 50 URLs on next run

Run the same command again in `postgres` mode:

```bash
kotlin scripts/recipe_site_scraper.main.kts -- allrecipes ~/documents/output \
  --urls-file ~/documents/recipes/allrecipes.txt \
  --mode postgres \
  --max-urls 50
```

Because DB pre-check is enabled by default, each run processes the next 50 URLs not yet present in the database.
