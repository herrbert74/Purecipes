# Recipe website scraping

Kotlin scripts for importing recipes from external websites into the Purecipes PostgreSQL database. Implements [Feature 001](../../docs/features/001_recipe-website-scraping.md).

Agent maintenance rules (schema sync, site patterns, validation) live in [`AGENTS.md`](AGENTS.md).

---

## Overview: what lives here

```
scripts/scraping/
├── ScrapedIngredientLines.kt          # shared library (not runnable)
├── recipe_site_scraper.main.kts       # primary scraper (ongoing)
├── recipe_site_scraper_ingredient_test.main.kts
└── oneoff/
    ├── normalize_existing_ingredients.main.kts   # text backfill (historical)
    ├── normalize_existing_ingredients.sh         # wrapper for the above
    └── backfill_ingredient_requirements.main.kts # optional-flag backfill
```

| File | Role | When to use |
|------|------|-------------|
| [`ScrapedIngredientLines.kt`](ScrapedIngredientLines.kt) | **Shared library** — turn raw scraped ingredient strings into structured import rows (text cleanup, sanitization, optional/requirement detection) | Not run directly. Imported by every `.main.kts` script below. |
| [`recipe_site_scraper.main.kts`](recipe_site_scraper.main.kts) | **Primary scraper** — discover URLs, scrape sites, write JSON, import into Postgres | Normal day-to-day imports and re-imports. |
| [`recipe_site_scraper_ingredient_test.main.kts`](recipe_site_scraper_ingredient_test.main.kts) | **Offline tests** for `ScrapedIngredientLines.kt` | After changing scraped-ingredient rules; before bulk imports. |
| [`oneoff/normalize_existing_ingredients.main.kts`](oneoff/normalize_existing_ingredients.main.kts) | **One-off backfill** — re-apply *text* cleanup on data imported *before* current rules | Legacy archives or DB rows that pre-date rule changes. Prefer the main scraper for new work. |
| [`oneoff/normalize_existing_ingredients.sh`](oneoff/normalize_existing_ingredients.sh) | Shell wrapper that clears the Kotlin script cache, then runs the script above | Same as above; avoids stale cached library code on macOS. |
| [`oneoff/backfill_ingredient_requirements.main.kts`](oneoff/backfill_ingredient_requirements.main.kts) | **One-off backfill** — set `ingredients.requirement` (optional vs required) on existing DB rows | After deploying optional-ingredient schema on an existing database. |

### `ScrapedIngredientLines.kt`

This is the **single source of scraped-ingredient processing** for imports. It is `@file:Import`‑ed by:

- `recipe_site_scraper.main.kts`
- `recipe_site_scraper_ingredient_test.main.kts`
- `oneoff/normalize_existing_ingredients.main.kts`
- `oneoff/backfill_ingredient_requirements.main.kts`

**Naming:** the old name `RecipeIngredientNormalization.kt` suggested text formatting only. The library also filters headings/equipment, splits concatenated lines, and detects optional ingredients. `ScrapedIngredientLines` reflects that scope: raw **lines** from a scrape → `ProcessedScrapedIngredient` rows (display `text` + `requirement`).

App/backend code uses [`IngredientLineParser`](../../shared/domain/src/commonMain/kotlin/app/purecipes/shared/domain/model/ingredient/IngredientLineParser.kt) in `shared/domain` for user recipes and API semantics. Keep scraper-specific heuristics here; keep shared domain rules in `shared/domain`.

It handles, among other things:

- Splitting and sanitizing raw ingredient lines (headings, equipment, concatenated lines)
- Spacing between quantities and units (`60g` → `60 g`)
- Restoring cooking fractions (`0.333… cup` → `1/3 cup`)
- Detecting optional ingredients (`optional: …`, `(optional)`, garnish/serve suffixes) and emitting `requirement`

**New imports:** the main scraper runs this library on every recipe during web/json import. You do **not** need a separate backfill pass for freshly scraped data.

### Ongoing workflow vs one-off scripts

| Concern | Handled by | Notes |
|---------|------------|--------|
| Scrape + import recipes | `recipe_site_scraper.main.kts` | Default path for all new data. |
| Ingredient text cleanup on import | `ScrapedIngredientLines.kt` (via main scraper) | Always applied at import time. |
| Optional/required flags on import | Same library + main scraper DB insert | New rows get `requirement` automatically. |
| Fix *old* ingredient text in DB/JSON | `oneoff/normalize_existing_ingredients.main.kts` | Only when historical data was imported under older rules. |
| Fix *old* optional flags in DB only | `oneoff/backfill_ingredient_requirements.main.kts` | Database-only; does not touch JSON files. |
| Verify scraped-ingredient rules | `recipe_site_scraper_ingredient_test.main.kts` | No database or network. |

---

## Main scraper (`recipe_site_scraper.main.kts`)

### Manual workflow: build a URL list

1. Open [simplescraper.io/extracturls](https://simplescraper.io/extracturls).
2. Enter a recipe site home or category page (for example `https://www.allrecipes.com/recipes`).
3. Crawl to the depth you need so recipe detail URLs are included.
4. Copy the extracted URLs into a plain text file, one URL per line. Empty lines are ignored.
5. Save the file locally, for example `~/documents/recipes/allrecipes.txt`.

You can also let the script discover URLs via the SimpleScraper API (`--simplescraper-api-key` or `SIMPLESCRAPER_API_KEY`) instead of maintaining a file.

### What the script does

1. Discover URLs (SimpleScraper API) or read URLs from a local file.
2. Filter URLs to recipe-detail paths (site-specific regex in `knownWebsites`).
3. Scrape recipe data with `recipe-scrapers` (Python, invoked from Kotlin).
4. Process ingredient lines via `ScrapedIngredientLines.kt`.
5. Save JSON under the output folder as `{DB_ID}-{slug}.json`.
6. Insert into local PostgreSQL.
7. Optionally run `./gradlew calculateRecipeNutrition` for imported recipe IDs.

**Modes:**

- `--mode web` (default): discover or read URLs, scrape, save JSON, insert into DB.
- `--mode json`: import existing JSON from `{output_dir}/recipes/` without network calls.

### Supported websites

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

### Configuration

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

### Dependencies

```bash
python3 -m pip install recipe-scrapers
```

Also required:

- Kotlin CLI (`kotlin`)
- PostgreSQL for web/json import modes
- Network for first-time `@file:DependsOn` resolution
- Repository root with `./gradlew` when `--calculate-nutrition true` (default)

Run from the repo root or any subdirectory (the script locates `settings.gradle.kts`).

### Examples

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

---

## Scraped ingredient line tests

Run offline tests after editing [`ScrapedIngredientLines.kt`](ScrapedIngredientLines.kt):

```bash
kotlin scripts/scraping/recipe_site_scraper_ingredient_test.main.kts
```

Add cases to `recipe_site_scraper_ingredient_test.main.kts` when fixing new ingredient formatting edge cases.

---

## One-off maintenance scripts

Use scripts under [`oneoff/`](oneoff/) only when **existing** database rows or saved JSON need to catch up with logic that the main scraper already applies to **new** imports.

### Re-normalize ingredient text (`normalize_existing_ingredients`)

Fixes quantity/unit spacing and fraction formatting in place. Can target PostgreSQL, saved scrape JSON, or both.

```bash
./scripts/scraping/oneoff/normalize_existing_ingredients.sh \
  --json-dir ~/documents/output/recipes \
  --db-name purecipes \
  --dry-run true \
  --verbose true
```

The shell wrapper clears the Kotlin script cache first. If you run `kotlin scripts/scraping/oneoff/normalize_existing_ingredients.main.kts` directly, stale cached rules may cause zero updates; delete `~/Library/Caches/main.kts.compiled.cache/` when that happens.

Remove `--dry-run true` to apply changes. Repeat `--json-dir` for multiple output folders. Database flags are optional; omit them to update JSON only.

Does **not** recalculate nutrition or run enrichment.

### Backfill optional ingredient flags (`backfill_ingredient_requirements`)

Database-only. Re-parses `ingredients.ingredient` text and updates `ingredients.requirement` (and cleaned display text). Required after optional-ingredient schema changes on an existing DB.

```bash
kotlin scripts/scraping/oneoff/backfill_ingredient_requirements.main.kts \
  --db-name purecipes \
  --dry-run true \
  --verbose true
```

---

## Troubleshooting

- Use LF line endings in URL list files.
- Check firewall/VPN if fetches fail.
- Run with invalid args to print full usage from the script.
- Ingredient rule changes not showing up: run the ingredient test script, then clear the Kotlin script cache (see normalize shell wrapper) before one-off backfills.
