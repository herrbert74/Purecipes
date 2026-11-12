# Recipe website scraping

Kotlin scripts for importing recipes from external websites into the Purecipes PostgreSQL database. Implements [Feature 001](../../docs/features/001_recipe-website-scraping.md).

Agent maintenance rules (schema sync, site patterns, validation) live in [`AGENTS.md`](AGENTS.md). End-to-end ingredient rebuilding, snapshots (`names.tsv` / `lines.tsv`), and USDA/pantry pinning live in [`../../backend/INGREDIENTS.md`](../../backend/INGREDIENTS.md).

---

## Overview: what lives here

```
scripts/scraping/
├── ScrapedAppliances.kt               # shared appliance extraction library (not runnable)
├── ScrapedIngredientLines.kt          # shared ingredient library (not runnable)
├── recipe_site_scraper.main.kts       # primary scraper (ongoing)
├── recipe_site_scraper_ingredient_test.main.kts
└── oneoff/
    ├── backfill_recipe_appliances.main.kts               # backfill appliances column for existing recipes
    └── backfill_scraped_nutrition_serving_totals.main.kts # rescale scraped whole-recipe nutrition to per-serving
```

| File | Role | When to use | Examples |
|------|------|-------------|----------|
| [`ScrapedAppliances.kt`](ScrapedAppliances.kt) | **Shared library** — extract appliance names (`OVEN`, `AIR_FRYER`, `BLENDER`, etc.) from recipe instructions, titles, descriptions, and cooking methods | Not run directly. Imported by `recipe_site_scraper.main.kts` and `backfill_recipe_appliances.main.kts`. | `preheat oven to 180°C` → `OVEN`; `pulse in food processor` → `FOOD_PROCESSOR` |
| [`ScrapedIngredientLines.kt`](ScrapedIngredientLines.kt) | **Shared library** — turn raw scraped ingredient strings into structured import rows (text cleanup, sanitization, optional/requirement detection, OR groups) | Not run directly. Imported by `recipe_site_scraper.main.kts`, `recipe_site_scraper_ingredient_test.main.kts`, and compiled into `:backend` tools (`./gradlew rebuildIngredientData`). | `60g Gruyère` → `60 g Gruyère`; `For the sauce:` → dropped; `parsley or tarragon` → two `ALTERNATIVE` rows |
| [`recipe_site_scraper.main.kts`](recipe_site_scraper.main.kts) | **Primary scraper** — discover URLs, scrape sites, write JSON, import into Postgres | Normal day-to-day imports and re-imports. | `--mode web` + URL list; `--mode json` re-import from `{output}/recipes/` |
| [`recipe_site_scraper_ingredient_test.main.kts`](recipe_site_scraper_ingredient_test.main.kts) | **Offline tests** for `ScrapedIngredientLines.kt` | After changing scraped-ingredient rules; before running `./gradlew rebuildIngredientData`. | Asserts cases in that file (spacing, fractions, alternatives, splits) |
| [`oneoff/backfill_recipe_appliances.main.kts`](oneoff/backfill_recipe_appliances.main.kts) | **One-off backfill** — populate `recipes.appliances` array from instructions and metadata | Legacy data or after appliance keyword rule changes. | Scans `instruction_steps` and updates `recipes.appliances` |
| [`oneoff/backfill_scraped_nutrition_serving_totals.main.kts`](oneoff/backfill_scraped_nutrition_serving_totals.main.kts) | **One-off backfill** — rescale scraped `nutrition` rows that stored whole-recipe totals instead of per-serving values | Legacy scraped nutrition cleanup. | Divides `nutrition` columns by parsed serving count |

### `ScrapedAppliances.kt`

This is the **shared library for appliance detection** on imports and backfills. It is `@file:Import`‑ed by:

- `recipe_site_scraper.main.kts`
- `oneoff/backfill_recipe_appliances.main.kts`

It extracts standard appliance identifiers from recipe instruction lines, instructions text, titles, descriptions, and cooking methods:

- **Supported appliances:** `AIR_FRYER`, `BLENDER`, `DEEP_FRYER`, `FOOD_PROCESSOR`, `GRILL`, `HAND_MIXER`, `IMMERSION_BLENDER`, `JUICER`, `KETTLE`, `MICROWAVE`, `OVEN`, `PASTA_MAKER`, `PRESSURE_COOKER`, `SANDWICH_MAKER`, `SLOW_COOKER`, `SPICE_GRINDER`, `STEAMER`, `STOVETOP`, `WAFFLE_MAKER`.
- **False positive guards:**
  - Dutch oven mentions are stripped before oven keyword matching so Dutch oven pots on stovetops do not trigger `OVEN`.
  - Immersion/stick/hand blenders are extracted as `IMMERSION_BLENDER` and stripped before matching standard `BLENDER`.
  - Cooking method metadata (e.g. `BAKE`, `ROAST`, `AIR_FRY`, `GRILL`, `SLOW_COOK`) triggers corresponding appliances even if the appliance noun is omitted from the instructions.

**New imports:** the main scraper automatically runs appliance detection on every recipe during web import or JSON re-import (when `appliances` is missing from the JSON). Detected appliances are written to `{id}-{slug}.json` and inserted into PostgreSQL `recipes.appliances`. You do **not** need a separate backfill for freshly scraped recipes.

### `ScrapedIngredientLines.kt`

This is the **single source of scraped-ingredient processing** for imports and rebuilds. It is `@file:Import`‑ed by `recipe_site_scraper.main.kts` and `recipe_site_scraper_ingredient_test.main.kts`, and compiled into `:backend`'s `tools` source set for `./gradlew rebuildIngredientData`.

**Naming:** the old name `RecipeIngredientNormalization.kt` suggested text formatting only. The library also filters headings/equipment, splits concatenated lines, detects optional ingredients, and splits OR alternatives. `ScrapedIngredientLines` reflects that scope: raw **lines** from a scrape → `ProcessedScrapedIngredient` rows (display `text` + `requirement` + optional `alternative_group_key`).

App/backend code uses [`IngredientLineParser`](../../shared/domain/src/commonMain/kotlin/app/purecipes/shared/domain/ingredient/IngredientLineParser.kt) in `shared/domain` for user recipes and API semantics. Keep scraper-specific heuristics here; keep shared domain rules in `shared/domain`.

It handles, among other things:

- Splitting and sanitizing raw ingredient lines (headings, equipment, concatenated lines)
- Spacing between quantities and units (`60g` → `60 g`)
- Restoring cooking fractions (`0.333… cup` → `1/3 cup`)
- Detecting optional ingredients (`optional: …`, `(optional)`, garnish/serve suffixes) and emitting `requirement`
- Splitting OR alternatives (`parsley or tarragon`) into multiple `ALTERNATIVE` rows

**New imports:** the main scraper runs this library on every recipe during web/json import. You do **not** need a separate backfill pass for freshly scraped data.

### Ingredient processing examples

Representative inputs and outcomes (from [`recipe_site_scraper_ingredient_test.main.kts`](recipe_site_scraper_ingredient_test.main.kts) and [`IngredientLineParserTest`](../../shared/domain/src/commonTest/kotlin/app/purecipes/shared/domain/ingredient/IngredientLineParserTest.kt)). On import, sanitized lines become DB rows with `requirement` and optionally `alternative_group_key`.

| Use case | Raw input | Result |
|----------|-----------|--------|
| Quantity + unit spacing | `60g Gruyère`, `2tbsp Chipotle Paste`, `500ml water` | `60 g Gruyère`, `2 tbsp Chipotle Paste`, `500 ml water` |
| Everyday unit words | `12Rasher bacon`, `1Knob butter`, `3Sprig thyme` | `12 Rasher …`, `1 Knob …`, `3 Sprig …` |
| Punctuation after quantity | `250g, peeled and diced onions` | `250 g, peeled and diced onions` |
| Multiple measurements in one line | `60g butter and 30g sugar` | `60 g butter and 30 g sugar` |
| Float → cooking fraction | `0.666666668653488 cup all-purpose flour` | `2/3 cup all-purpose flour` |
| Mixed number fraction | `1.5 cup chopped nuts` | `1 1/2 cup chopped nuts` |
| Imperial decimal fraction | `0.75 teaspoon ground mace` | `3/4 teaspoon ground mace` |
| Non-standard decimal (unchanged) | `1.7 cups water` | `1.7 cups water` (left as-is) |
| Glued quantity + name (unchanged) | `250gonions, diced` | `250gonions, diced` (no false split) |
| Heading / group label | `For the sauce:` | Dropped (not stored as ingredient) |
| Equipment line | `Large mixing bowl` | Dropped |
| Concatenated ingredients | `flour60g butter2tbsp` | Split into `flour`, `60 g butter`, `2 tbsp` |
| Optional prefix | `optional: parsley, to garnish` | `OPTIONAL`, text without prefix |
| Optional parenthetical | `1 tbsp honey (optional)` | `OPTIONAL`, `(optional)` removed from display text |
| Optional serve suffix | `olive oil, plus extra to serve` | `OPTIONAL` |
| Required (default) | `2 chicken thighs` | `REQUIRED` |
| OR alternatives (simple) | `parsley or tarragon` | Two `ALTERNATIVE` rows: `parsley`, `tarragon` |
| OR + shared quantity | `2 tbsp parsley or fresh tarragon` | `2 tbsp parsley`, `2 tbsp fresh tarragon` |
| OR + shared “of” + suffix | `small bunch of parsley or tarragon (about 30g)` | `small bunch of parsley (about 30g)`, `small bunch of tarragon (about 30g)` |

Real-world OR example: BBC Good Food (`parsley or tarragon` style lines).

### Ongoing workflow vs one-off scripts

| Concern | Handled by | Notes | Examples |
|---------|------------|--------|----------|
| Scrape + import recipes | `recipe_site_scraper.main.kts` | Default path for all new data. | Web scrape → JSON + Postgres |
| Ingredient text cleanup, optional flags, and OR groups on import | `ScrapedIngredientLines.kt` (via main scraper) | Always applied at import time. | `60g` → `60 g`, `optional: …` → `OPTIONAL`, `parsley or tarragon` → two linked `ALTERNATIVE` rows |
| Appliance extraction on import | `ScrapedAppliances.kt` (via main scraper) | Always applied at import time; saved to JSON and DB `recipes.appliances`. | `preheat oven to 180°C` → `OVEN` |
| Recompute all derived ingredient rows + nutrition from saved JSON + DB | `./gradlew rebuildIngredientData` | Reproducible rebuild after changing `ScrapedIngredientLines.kt` or `line-overrides.tsv`; syncs JSON and preserves `recipes.id`. See [`backend/INGREDIENTS.md`](../../backend/INGREDIENTS.md). | Re-splits lines, applies overrides, and recalculates nutrition |
| Fix *old* or *out-of-date* appliances | `oneoff/backfill_recipe_appliances.main.kts` | DB only; re-scans instructions and metadata to update `recipes.appliances` array. | After adding new appliance rules or keywords |
| Verify scraped-ingredient rules | `recipe_site_scraper_ingredient_test.main.kts` | No database or network. | Run before `./gradlew rebuildIngredientData` or bulk imports |

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
4. Process ingredient lines via `ScrapedIngredientLines.kt` and detect appliances via `ScrapedAppliances.kt`.
5. Save JSON under the output folder as `{DB_ID}-{slug}.json` (including `appliances` array).
6. Insert into local PostgreSQL (including `recipes.appliances`).
7. Optionally run `./gradlew calculateRecipeNutrition` for imported recipe IDs.

**Modes:**

- `--mode web` (default): discover or read URLs, scrape, save JSON, insert into DB.
- `--mode json`: import existing JSON from `{output_dir}/recipes/` without network calls.

### Supported websites

| Site | Notes |
|------|--------|
| allrecipes.com | |
| bbcgoodfood.com (`bbcgoodfood`) | |
| bonappetit.com | |
| breadtopia.com (`breadtopia`) | |
| epicurious.com | |
| foodnetwork.co.uk | |
| greatbritishrecipes.com (`greatbritishrecipes`) | |
| jamieoliver.com (`jamieoliver`) | |
| maryberry.co.uk (`maryberry`) | |
| mob.co.uk | |
| natashasbaking.com (`natashasbaking`) | |
| paulhollywood.com (`paulhollywood`) | |
| prue-leith.com (`prueleith`) | |
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

For any ingredient line, heading, optional-flag, or OR-alternative rule change in `ScrapedIngredientLines.kt`, use `./gradlew rebuildIngredientData` followed by `./gradlew diffIngredientSnapshot` (see [`backend/INGREDIENTS.md`](../../backend/INGREDIENTS.md)).

The remaining scripts under [`oneoff/`](oneoff/) handle non-ingredient backfills:

### Backfill recipe appliances (`backfill_recipe_appliances`)

Database-only. Scans recipe instruction steps, titles, descriptions, and cooking methods across existing recipes in PostgreSQL and populates the `recipes.appliances` text array using `ScrapedAppliances.kt`. Use after introducing appliance support or updating appliance keyword rules.

```bash
kotlin scripts/scraping/oneoff/backfill_recipe_appliances.main.kts \
  --db-name purecipes \
  --db-user postgres \
  --db-password postgres \
  --dry-run true \
  --verbose true
```

Remove `--dry-run true` to commit the updates to the database.

---

## Troubleshooting

- Use LF line endings in URL list files.
- Check firewall/VPN if fetches fail.
- Run with invalid args to print full usage from the script.
- If `kotlin` script changes to `@file:Import` files do not show up, clear `~/Library/Caches/main.kts.compiled.cache/`.
