# Scripts

## Recipe Website Scraping

`scripts/recipe_site_scraper.main.kts` is the Feature 001 implementation and follows the Kotlin-script MVP direction from `001_recipe-website-scraping.md`.

It orchestrates the full pipeline in Kotlin:

1. Discover URLs from a recipe site (SimpleScraper API), or read URLs from a local file.
2. Filter URLs to recipe-detail paths (site-specific regex patterns in `knownWebsites` map).
3. Scrape recipe data with `recipe-scrapers` (invoked one-by-one through Python from the Kotlin script).
4. Save each recipe JSON file under the requested output folder with DB ID-based naming (`{DB_ID}-{slug}.json`).
5. Insert into local PostgreSQL database.

**Two operating modes**:

- `--mode web` (default): Discover URLs from websites, scrape, save JSON files, and insert into DB (full pipeline)
- `--mode json`: Read previously saved JSON files from disk and insert into DB (offline re-import, useful for testing)

### Supported Websites

The scraper supports multiple recipe websites with per-site URL pattern configuration:
- allrecipes.com
- bonappetit.com
- epicurious.com
- foodnetwork.co.uk
- mob.co.uk
- seriouseats.com

Each site has a unique URL regex pattern to identify recipe pages and filter out listing/category pages.

### Configuration Options

Batch controls:
- `--mode web|json` operating mode (default: `web`)
- `--max-urls <n>` batch size (default `50`)
- `--recipe-url-pattern <regex>` custom URL pattern filter (default: `/recipe/`)
- `--sleep-seconds <n>` delay between scrapes in seconds (default: `0.4`)
- `--urls-file <path>` use pre-extracted URL list instead of discovery
- `--precheck-db true|false` skip URLs already in DB before scraping (default: `true`)

Database options (optional, all have sensible defaults):
- `--db-host localhost` 
- `--db-port 5432`
- `--db-name purecipes`
- `--db-user postgres`
- `--db-password postgres`

SimpleScraper API options (required for web mode if not using `--urls-file`):
- `--simplescraper-endpoint https://simplescraper.io/extracturls`
- `--simplescraper-api-key <your-api-key>` (or set `SIMPLESCRAPER_API_KEY` env var)
- `--simplescraper-timeout <seconds>` API timeout (default: `30`)

### Dependencies

```bash
python3 -m pip install recipe-scrapers
```

Kotlin runner requirements:
- Kotlin script runtime (`kotlin` command)
- PostgreSQL server running (for web/json modes)
- Internet access for first-time `@file:DependsOn` dependency resolution

### Run Web Mode: Scrape and Import

```bash
kotlin scripts/recipe_site_scraper.main.kts allrecipes ./tmp/allrecipes \
  --mode web \
  --simplescraper-api-key "$SIMPLESCRAPER_API_KEY" \
  --max-urls 100
```

This discovers URLs, scrapes recipes, saves JSON files, and inserts into the database in one pass. JSON files are saved with DB ID-based names for idempotent re-imports.

### Run JSON Mode: Re-Import Previously Saved Recipes

```bash
kotlin scripts/recipe_site_scraper.main.kts allrecipes ./tmp/allrecipes \
  --mode json
```

This imports all `.json` files from `./tmp/allrecipes/recipes/` into the database without making any network requests. Useful for testing or re-syncing without re-scraping.

### Use a Pre-Extracted URL List

```bash
kotlin scripts/recipe_site_scraper.main.kts seriouseats ./tmp/seriouseats \
  --mode web \
  --urls-file ./tmp/seriouseats/urls.txt \
  --max-urls 50
```

### Continue Scraping from Where You Left Off

Run the same command again in web mode:

```bash
kotlin scripts/recipe_site_scraper.main.kts allrecipes ~/documents/output \
  --urls-file ~/documents/recipes/allrecipes.txt \
  --mode web \
  --max-urls 50
```

Because DB pre-check is enabled by default, each run processes the next 50 URLs not yet present in the database (identified by `source_url` UNIQUE constraint).

### Important URL Note

- Category/listing URLs (example: `https://www.allrecipes.com/recipes/...`) are not recipe-detail pages and will be filtered/skipped.
- For best results, use SimpleScraper API or pre-filtered URL lists containing only recipe pages.
- Each site has a unique URL pattern; use `--recipe-url-pattern` to override the default if needed.
