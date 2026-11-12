# Publishing Scraped Recipes to Release Database

Idempotent workflow to publish newly scraped recipes, ingredient pins, and nutrition catalogue rows from localhost to the release database over an SSH tunnel while preserving existing `recipes.id` and `nutrition_foods.id` values, user favorites, and cookbooks.

---

## 0. Preconditions

1. **Deploy the latest backend first** if `SchemaSql.kt` or `Db.ensureSchema()` changed, so any new columns or tables exist before syncing data.
2. **Finish local review and pinning first** ([`INGREDIENTS.md` Workflow 1](INGREDIENTS.md#workflow-1-after-scraping-new-recipes)): local `names.tsv`, `lines.tsv`, and `line-overrides.tsv` must be up to date (`./gradlew diffIngredientSnapshot` exits `0` against local Postgres).

---

## 1. Tunnel & Environment Setup

### Open SSH Tunnel (Terminal 1)
Keep this running in a separate terminal:
```bash
ssh -N -L 15432:localhost:5432 "${PURECIPES_VPS_SSH_TARGET}"
```
*(Set `PURECIPES_VPS_SSH_TARGET` to `<ssh-user>@<vps-host>` in your local shell profile).*

### Connection Environment (Terminal 2)
Both `psql`/`pg_dump` (which read `PGPASSWORD`) and the backend Gradle tasks (which read `PURECIPES_DB_*`) require the release database credentials:
```bash
export PURECIPES_DB_URL="jdbc:postgresql://localhost:15432/purecipes"
export PURECIPES_DB_USER="<RELEASE_DB_USER>"
export PURECIPES_DB_PASSWORD="<RELEASE_DB_PASSWORD>"
export PGPASSWORD="${PURECIPES_DB_PASSWORD}"
```

Verify the tunnel connection:
```bash
psql -h localhost -p 15432 -U "$PURECIPES_DB_USER" -d purecipes -c "SELECT count(*) FROM recipes;"
```

---

## 2. Backup the Release Database

```bash
pg_dump -h localhost -p 15432 -U "$PURECIPES_DB_USER" -d purecipes -Fc \
  -f ~/Purecipes/vps/var/backups/purecipes-full-$(date +%Y%m%d).dump
```

---

## 3. Synchronize Nutrition Catalogue (`nutrition_foods`, `nutrition_food_aliases`, `nutrition_food_measures`)

Ingredient pins in `names.tsv` bind to `(source_name, source_id)` rather than serial `nutrition_foods.id` values, while recipe nutrition calculation also relies on `nutrition_food_aliases` and household gram weights in `nutrition_food_measures`.

With the SSH tunnel from Step 1 open and `PURECIPES_DB_*` exported in Terminal 2, `syncNutritionCatalogue` connects to both your local Postgres (`localhost:5432`, user `postgres`) and the release database over the SSH tunnel (`localhost:15432`), syncing all three tables and remapping `food_id` foreign keys automatically while preserving existing release `nutrition_foods.id` rows:

```bash
# Preview changes first (optional)
./gradlew syncNutritionCatalogue -PnutritionSync.dryRun=true

# Apply catalogue sync to release
./gradlew syncNutritionCatalogue
```

---

## 4. Import New JSON Recipes

Inserts newly scraped JSON files from your local archive while skipping recipes whose `source_url` already exists on release:

```bash
kotlin scripts/scraping/recipe_site_scraper.main.kts allrecipes ~/Purecipes/recipeoutput/recipes \
  --mode json \
  --upsert-existing false \
  --calculate-nutrition false \
  --db-host localhost \
  --db-port 15432 \
  --db-name purecipes \
  --db-user "$PURECIPES_DB_USER" \
  --db-password "$PURECIPES_DB_PASSWORD"
```

> **Note:** In `--mode json`, the script walks **every** site subfolder under `~/Purecipes/recipeoutput/recipes` regardless of the first positional site argument (`allrecipes`).

---

## 5. Rebuild Lines, Apply Pins & Calculate Nutrition

Applies local `backend/data/ingredients/` pins and line overrides, rebuilds ingredient groups/lines, and recalculates nutrition for all scraped recipes on release in one step:

```bash
./gradlew rebuildIngredientData
```

*(Note: `rebuildIngredientData` recalculates nutrition across all scraped recipes by default. You do not need a separate `calculateRecipeNutrition` run unless you passed `-Prebuild.onlyChangedLines=true`.)*

### Verify Release Snapshot Sync
Verify that the release database's ingredient names and parsed lines match your committed local snapshot:

```bash
./gradlew diffIngredientSnapshot
```
*Expected output: `Names: +0 added, -0 removed, ~0 changed` and `Lines: +0 added, -0 removed, ~0 changed`.*

---

## 6. Enrich Missing Recipe Attributes

Fills missing `cuisine`, `meal_type`, `difficulty`, `cooking_method`, `calorie_range`, and `dietary_preferences` on newly imported rows using the local Universal Sentence Encoder v4 model in `.use_v4`:

```bash
USE_MODEL_PATH=.use_v4 ./gradlew :enrichment:run
```

---

## 7. Align Local Database with Release (Optional)

If you want your local `recipes.id` serial values to match the release database after publishing:

```bash
pg_dump -h localhost -p 15432 -U "$PURECIPES_DB_USER" -d purecipes -Fc -f /tmp/release_sync.dump
dropdb -h localhost -p 5432 -U postgres purecipes
createdb -h localhost -p 5432 -U postgres purecipes
pg_restore --no-owner --no-acl -h localhost -p 5432 -U postgres -d purecipes /tmp/release_sync.dump
rm -f /tmp/release_sync.dump

# Point Gradle back to local Postgres before verifying
unset PURECIPES_DB_URL PURECIPES_DB_USER PURECIPES_DB_PASSWORD PGPASSWORD
./gradlew diffIngredientSnapshot
```
