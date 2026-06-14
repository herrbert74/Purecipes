# Recipe website scraping

[`recipe_site_scraper.main.kts`](recipe_site_scraper.main.kts) implements [Feature 001](../../docs/features/001_recipe-website-scraping.md).

## Manual workflow: build a URL list

1. Open [simplescraper.io/extracturls](https://simplescraper.io/extracturls).
2. Enter a recipe site home or category page (for example `https://www.allrecipes.com/recipes`).
3. Crawl to the depth you need so recipe detail URLs are included.
4. Copy the extracted URLs into a plain text file, one URL per line. Empty lines are ignored.
5. Save the file locally, for example `~/documents/recipes/allrecipes.txt`.

You can also let the script discover URLs via the SimpleScraper API (`--simplescraper-api-key` or `SIMPLESCRAPER_API_KEY`) instead of maintaining a file.

## What the script does

1. Discover URLs (SimpleScraper API) or read URLs from a local file.
2. Filter URLs to recipe-detail paths (site-specific regex in `knownWebsites`).
3. Scrape recipe data with `recipe-scrapers` (Python, invoked from Kotlin).
4. Save JSON under the output folder as `{DB_ID}-{slug}.json`.
5. Insert into local PostgreSQL.

**Modes:**

- `--mode web` (default): discover or read URLs, scrape, save JSON, insert into DB.
- `--mode json`: import existing JSON from `{output_dir}/recipes/` without network calls.

## Supported websites

| Site | Notes |
|------|--------|
| allrecipes.com | |
| bonappetit.com | |
| epicurious.com | |
| foodnetwork.co.uk | |
| mob.co.uk | |
| seriouseats.com | |

Each site has a URL regex in `knownWebsites`. Override with `--recipe-url-pattern` if needed.

Category/listing URLs are filtered out; use recipe-detail URLs only.

## Configuration

Batch:

- `--mode web|json` (default `web`)
- `--max-urls <n>` (default `50`)
- `--recipe-url-pattern <regex>` (default `/recipe/`)
- `--sleep-seconds <n>` (default `0.4`)
- `--urls-file <path>` — skip API discovery
- `--precheck-db true|false` (default `true`)
- `--calculate-nutrition true|false` (default `true`; uses the same `--db-*` connection as the import)

Database (defaults shown):

- `--db-host localhost`, `--db-port 5432`, `--db-name purecipes`, `--db-user postgres`, `--db-password postgres`

SimpleScraper (web mode without `--urls-file`):

- `--simplescraper-endpoint` (default `https://simplescraper.io/extracturls`)
- `--simplescraper-api-key` or `SIMPLESCRAPER_API_KEY`
- `--simplescraper-timeout` (default `30`)

## Dependencies

```bash
python3 -m pip install recipe-scrapers
```

Also required:

- Kotlin CLI (`kotlin`)
- PostgreSQL for web/json import modes
- Network for first-time `@file:DependsOn` resolution
- Repository root with `./gradlew` when `--calculate-nutrition true` (default)

Run from the repo root or any subdirectory (the script locates `settings.gradle.kts`).

## Ingredient normalization tests

Ingredient parsing and sanitization (including inserting a space between quantities and units like `60g` → `60 g`) lives in [`RecipeIngredientNormalization.kt`](RecipeIngredientNormalization.kt). Run the offline test script from the repo root:

```bash
kotlin scripts/scraping/recipe_site_scraper_ingredient_test.main.kts
```

Add cases to `recipe_site_scraper_ingredient_test.main.kts` when fixing new ingredient formatting edge cases.

## Normalize existing ingredients

To fix ingredient text already stored in PostgreSQL and/or saved scrape JSON files (without re-scraping, nutrition, or enrichment):

```bash
./scripts/scraping/normalize_existing_ingredients.sh \
  --json-dir ~/documents/output/recipes \
  --db-name purecipes \
  --dry-run true \
  --verbose true
```

The shell wrapper clears the Kotlin script cache first. If you run `kotlin scripts/scraping/normalize_existing_ingredients.main.kts` directly, stale cached rules may cause zero updates; delete `~/Library/Caches/main.kts.compiled.cache/` when that happens.

Remove `--dry-run true` to apply changes. Repeat `--json-dir` for multiple output folders. Database flags are optional; omit them to update JSON only.

## Examples

Web mode — scrape and import:

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts allrecipes ./tmp/allrecipes \
  --mode web \
  --simplescraper-api-key "$SIMPLESCRAPER_API_KEY" \
  --max-urls 100
```

JSON mode — re-import saved files:

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts allrecipes ./tmp/allrecipes --mode json
```

Skip nutrition calculation:

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts allrecipes ./tmp/allrecipes \
  --mode json \
  --calculate-nutrition false
```

Pre-extracted URL list:

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts seriouseats ./tmp/seriouseats \
  --mode web \
  --urls-file ./tmp/seriouseats/urls.txt \
  --max-urls 50
```

Resume scraping (DB pre-check skips URLs already stored):

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts allrecipes ~/documents/output \
  --urls-file ~/documents/recipes/allrecipes.txt \
  --mode web \
  --max-urls 50
```

## Troubleshooting

- Use LF line endings in URL list files.
- Check firewall/VPN if fetches fail.
- Run with invalid args to print full usage from the script.
