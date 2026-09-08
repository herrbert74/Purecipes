# Purecipes database

This is the product map of the PostgreSQL database the backend serves to the apps. It explains what each table is for and what each column means. It does not cover how to run SQL, how tables are created, or how the server is started — those live in [`README.md`](README.md). USDA import, `calculateRecipeNutrition`, and `reportIngredientFoodMatches` live in [`NUTRITION.md`](NUTRITION.md).

The phones, iOS app, and web app never talk to this database directly. They call the backend HTTP API. The backend reads and writes these tables.

There is no separate “search index.” Search, pantry matching, and nutrition all run against this same database.

---

## How data gets here

Four independent pipelines write into the same database.

### 1. Website scraping

The scraper (`scripts/scraping/recipe_site_scraper.main.kts`) discovers recipe pages, extracts structured recipe data, cleans ingredient lines, and inserts:

- the recipe itself (`recipes`)
- ingredient groups and lines (`ingredient_groups`, `ingredients`)
- cooking steps (`instruction_steps`)
- nutrition numbers from the website when they exist (`nutrition`, marked as scraped)

It does **not** match ingredients to the food catalogue or to pantry names. After each import batch it usually asks the backend to calculate nutrition (see below).

### 2. Nutrition seed import

A one-time / occasional import loads USDA FoodData Central foods into `nutrition_foods`, plus:

- alternative names (`nutrition_food_aliases`)
- household measures such as cup / tablespoon (`nutrition_food_measures`)

This is the **food table**. Recipe ingredients are later matched *to* these rows. How to run the importer is in [`NUTRITION.md`](NUTRITION.md).

### 3. Nutrition calculation

`./gradlew calculateRecipeNutrition` (also run after scraping) parses each ingredient line, tries to find a food, converts the amount to grams, and stores:

- the parse (`ingredient_measurements`)
- the food match (`ingredient_nutrition_matches`)
- per-ingredient calories (`ingredient_nutrition_contributions`)
- recipe totals (`nutrition`), unless the recipe already has scraped website nutrition

`./gradlew reportIngredientFoodMatches` reads those stored rows and prints a gap report to stdout. Pass `-Preport.output=/tmp/ingredient-food-match-report.txt` to also write a file. Commands are in [`NUTRITION.md`](NUTRITION.md).

### 4. Users and enrichment

- People create accounts, save favorites, cookbooks, pantry lists, and search filters through the app.
- People can also upload their own recipes (those have an owner and can be private).
- The enrichment tool fills empty recipe attributes such as cuisine, meal type, and difficulty. It never overwrites a value that is already set.

---

## How the app uses it

| App need | Tables |
|----------|--------|
| Browse / search recipes | `recipes` plus filters on cuisine, time, meal type, and similar columns |
| Recipe details and cooking steps | `recipes`, `ingredient_groups`, `ingredients`, `instruction_steps`, `nutrition` |
| “Recipes I can make with what I have” | `user_pantry` compared to `ingredients` **by name text**, not via the food table |
| Hide recipes with unwanted ingredients | `user_excluded_ingredients` compared to `ingredients` by name text |
| Nutrition card | `nutrition`, `ingredient_nutrition_matches`, `ingredient_nutrition_contributions` |
| Sign-in, premium, settings | `app_users`, `auth_sessions`, `measurement_preferences`, `search_filters` |
| Library | `favorites`, `cookbooks`, `cookbook_recipes`, and sharing tables |

Two matching problems are easy to confuse:

1. **Pantry / search matching.** Does this recipe line look like a name in the app’s ingredient catalogue (Chicken, Tomato, …) or like something in the user’s pantry? If a required line is “unknown,” pantry search cannot treat the recipe as fully makeable. That is why some recipes disappear from pantry search even though they exist in the database.
2. **Food-table matching.** After a line is parsed (`2 tbsp olive oil` → quantity, unit, name), can we point the name at a row in `nutrition_foods` and convert the unit to grams? That drives calories, not search visibility.

---

## Table groups

**Accounts**

- `app_users`, `auth_sessions`

**Recipes**

- `recipes`, `ingredient_groups`, `ingredients`, `instruction_steps`

**Food catalogue and recipe nutrition**

- `nutrition_foods`, `nutrition_food_aliases`, `nutrition_food_measures`
- `ingredient_measurements`, `ingredient_nutrition_matches`, `ingredient_nutrition_contributions`, `nutrition`

**Library and sharing**

- `favorites`, `cookbooks`, `cookbook_recipes`, `cookbook_shares`, `cookbook_share_imports`

**Personal cooking preferences**

- `measurement_preferences`, `measurement_preference_seen_recipes`
- `search_filters`, `user_pantry`, `user_excluded_ingredients`

---

## Accounts

### `app_users`

One row per signed-in person. The same human can have more than one row if they sign in with different providers (Google vs email, for example).

| Column | Meaning |
|--------|---------|
| `id` | Internal user id. Other tables point at this. |
| `provider` | How they signed in: `EMAIL`, `GOOGLE`, `APPLE`, or `FACEBOOK`. |
| `external_user_id` | The id from that provider (Google subject, email address for email sign-in, and so on). Together with `provider`, this uniquely identifies the account. |
| `email` | Email on the account. |
| `display_name` | Full name shown in the app. |
| `first_name` | Given name, when the provider supplies it. |
| `family_name` | Family name, when the provider supplies it. |
| `profile_image_url` | Avatar URL, when the provider supplies it. |
| `is_premium` | Whether this account currently has premium. |
| `created_at` | When the account was first created. |
| `updated_at` | When the account was last changed. |

### `auth_sessions`

A logged-in session. Signing out or deleting the account ends these.

| Column | Meaning |
|--------|---------|
| `id` | Session id. |
| `user_id` | Whose session this is. |
| `access_token_hash` | Stored form of the session token. The raw token is what the app sends on API calls; only the hash is kept here. |
| `created_at` | When the session started. |
| `expires_at` | When the session stops being valid. |
| `revoked_at` | Set when the session is cancelled early (sign-out, password change, account deletion). Empty while the session is still live. |

---

## Recipes

### `recipes`

One row per recipe, whether scraped from a website or created by a user.

| Column | Meaning |
|--------|---------|
| `id` | Recipe id used everywhere else (search results, favorites, nutrition). |
| `title` | Recipe name shown in lists and on the details screen. |
| `description` | Short blurb, when we have one. |
| `instructions` | Full method as a single block of text. Step-by-step cooking prefers `instruction_steps` instead. |
| `total_time` | Total minutes, if known. Search “under 30 minutes” uses this. |
| `prep_time` | Prep minutes, if known. |
| `cook_time` | Cook minutes, if known. |
| `yields` | Serving text from the source, for example `4 servings`. Nutrition uses this to estimate serving count when it can parse it. |
| `image_url` | Photo URL. |
| `language` | Recipe language, usually `en`. |
| `cuisine` | Cuisine label used as a search filter (Italian, Indian, Mexican, …). Often filled by enrichment if scraping left it empty. |
| `meal_type` | Breakfast, Lunch, Dinner, Snack, Dessert, Brunch, Appetizer, Drink, or Side Dish. |
| `difficulty` | Easy, Medium, or Hard. |
| `cooking_method` | Bake, Grill, Fry, Stir-Fry, Slow Cook, Steam, Boil, Roast, Pressure Cook, Air Fry, Smoke, Microwave, or Raw. |
| `calorie_range` | Low (under 300 kcal), Medium (300–600), or High (over 600). Derived from recipe calories when enrichment runs. |
| `dietary_preferences` | Zero or more labels such as Vegan, Vegetarian, Gluten-Free, Keto. Used as search chips. |
| `tags` | Extra free-form tags. Not the main search filter set. |
| `source_url` | Original website URL for scraped recipes. Each URL can only be imported once, which is how the scraper avoids duplicates. Empty for user-created recipes. |
| `measurement_system` | Whether the written amounts look Imperial, Metric, or Mixed. Used with the user’s measurement preferences. |
| `created_by_user_id` | Owner, for user-uploaded recipes. Empty for scraped catalogue recipes. |
| `is_private` | If true, only the owner sees it. Scraped recipes are public. |
| `scraped_at` | When this row was scraped. Empty for user-created recipes. |
| `created_at` | When the row first appeared in our database. |

Public search only returns recipes that are not private (or that belong to the signed-in user). That is *privacy* visibility. A public recipe can still fail *pantry* search if required ingredients do not match the catalogue or the user’s pantry.

### `ingredient_groups`

A named section on a recipe, such as “For the sauce” or “Dough.” Every recipe has at least one group, even if the name is empty.

| Column | Meaning |
|--------|---------|
| `id` | Group id. Ingredient lines point here, not directly at the recipe. |
| `recipe_id` | Which recipe this section belongs to. |
| `name` | Section title. May be empty. |
| `order_index` | Display order (0, 1, 2, …). |

### `ingredients`

One row per ingredient line as shown to the cook. This is the raw cooking text, not a link to the food table.

| Column | Meaning |
|--------|---------|
| `id` | Ingredient line id. Nutrition parse/match rows point here. |
| `ingredient_group_id` | Which section this line sits in. |
| `ingredient` | Display text, for example `2 tbsp olive oil` or `parsley`. This is what pantry search and food matching both start from. |
| `order_index` | Order inside the group. |
| `requirement` | `REQUIRED` (must have), `OPTIONAL` (ignored for pantry coverage and nutrition totals), or `ALTERNATIVE` (one of several options). |
| `alternative_group_key` | Shared number for an OR group (`parsley or tarragon`). All alternatives in that choice share the same key. Empty when the line is not part of an OR group. |

Pantry search treats an alternative group as covered if **any** option matches the pantry. Nutrition calculation counts **one** option from the group, not all of them.

### `instruction_steps`

Ordered cooking steps.

| Column | Meaning |
|--------|---------|
| `id` | Step id. |
| `recipe_id` | Which recipe. |
| `step` | The instruction text. |
| `order_index` | Step number order. |

---

## Food catalogue (USDA) and recipe nutrition

These tables are the calorie pipeline. They are **not** what pantry chips use. Import, backfill, and the matching report are documented in [`NUTRITION.md`](NUTRITION.md).

Matching order at calculation time:

1. Read `ingredients.ingredient`.
2. Parse quantity, unit, and a cleaned name into `ingredient_measurements`.
3. Look up the cleaned name in handwritten seed aliases and `nutrition_food_aliases`, then in `nutrition_foods` names (exact, then whole-token overlap).
4. Convert the unit to grams using `nutrition_food_measures` (or a built-in gram/ml conversion).
5. Store the match and the calorie contribution.

A line can fail at parse (no quantity/unit we understand), at food match (name not in the food table), or at grams (food found but that unit has no gram weight).

### `nutrition_foods`

Canonical foods, mostly from USDA FoodData Central. Nutrients are stored **per 100 grams**.

| Column | Meaning |
|--------|---------|
| `id` | Food id. Matches and aliases point here. |
| `source_name` | Dataset key: `fdc_foundation`, `fdc_sr_legacy`, `fdc_survey`, or `fdc_branded`. Foundation is preferred over SR Legacy, then Survey, then branded products. |
| `source_id` | USDA FDC id. Together with `source_name`, uniquely identifies the imported food. |
| `display_name` | USDA description, for example `Oil, olive, extra virgin`. |
| `normalized_name` | Lowercased, punctuation-stripped form used for matching. |
| `calories_per_100g` | Kilocalories per 100 g. Foods without this are ignored during matching. |
| `protein_per_100g` | Protein grams per 100 g. |
| `carbohydrates_per_100g` | Carbohydrate grams per 100 g. |
| `fat_per_100g` | Fat grams per 100 g. |
| `fiber_per_100g` | Fibre grams per 100 g. |
| `sugar_per_100g` | Sugar grams per 100 g. |
| `sodium_per_100g` | Sodium milligrams per 100 g. |
| `source_metadata` | Extra importer notes. Not shown in the app. |
| `updated_at` | When this food row was last written. |

### `nutrition_food_aliases`

Other spellings that should resolve to a food. This is the main lever for improving food-table matches: add `olive oil` → extra virgin olive oil, `caster sugar` → granulated sugar, and so on.

Handwritten aliases live in backend seed code and are applied at lookup time, so `calculateRecipeNutrition` picks them up without a USDA re-import. The same list is also written into this table during USDA import when those foods exist.

Catalogue names from the app pantry list are also seeded here during USDA import, when the importer can guess a food.

| Column | Meaning |
|--------|---------|
| `id` | Alias row id. |
| `food_id` | Which canonical food this name means. |
| `alias` | The human spelling, for example `olive oil`. |
| `normalized_alias` | Matching form of that spelling. Each normalized alias can point to only one food. |

### `nutrition_food_measures`

How to turn “1 cup of this food” into grams. Needed whenever the recipe does not already use a mass unit (g, kg, oz, lb). Those mass units convert with fixed factors and are not stored here.

SR Legacy portions often have `measureUnit` set to `undetermined` and put the real unit in `modifier` (`cup`, `tbsp`, `egg`). The importer copies that unit when it is one the recipe parser uses (`tsp`, `tbsp`, `cup`, `ml`, `l`, `egg`, `clove`, `piece`) and drops everything else. Count portions such as `fruit`, `whole`, `each`, size words (`medium`, `large`, `small`), heads, ears, stalks, and named produce counts (`avocado`, `potato`, `cucumber`, `leek`) are stored as `piece`. When several of those map to `piece`, import keeps the medium head, ear, or stalk. A few missing whole-produce `piece` weights (and 30 g per shallot) are also applied at lookup, like handwritten aliases. Foundation portions already use a named `measureUnit`. After changing this import, re-run USDA seed import so existing `undetermined` rows are replaced.

If there is still no named measure, calculation falls back to water density for volume units. The method used is stored on `ingredient_nutrition_contributions.grams_source`.

| Column | Meaning |
|--------|---------|
| `id` | Measure row id. |
| `food_id` | Which food. |
| `measure_name` | Unit name after normalisation, for example `cup`, `tbsp`, `tsp`, `clove`, `egg`, `piece`. |
| `grams_per_measure` | Grams for one of that unit of this food. One cup of flour and one cup of olive oil are different weights. |

### `ingredient_measurements`

The parse of one recipe line. Written by nutrition calculation. This is “what the line says,” not “which food it is.”

| Column | Meaning |
|--------|---------|
| `ingredient_id` | The recipe line. One parse per line. |
| `raw_text` | Copy of the line as parsed. |
| `quantity` | Number of units, for example `2`. Empty if the line could not be parsed as an amount. |
| `unit` | Unit word, for example `tbsp`. Empty if none was recognised. |
| `parsed_name` | Remainder after stripping quantity and unit, for example `olive oil`. This is what is looked up in the food table. |
| `is_measurable` | True only when quantity and a known unit were both found. Unmeasurable lines never get a nutrition match. |

Known units: g, kg, ml, l, tsp, tbsp, cup, oz, lb, egg, clove, piece. Size words such as `large` are not units. The parser also reads unicode and mixed fractions (`1½`, `3 1/2`), parenthetical weights (`(120 ml)`, `(2½ lb.)`), and pack sizes (`1 x 400 g`, `1 400 g`, `.5x 400g`). After a mass or volume unit it drops container words (`can`, `tin`, `jar`).

### `ingredient_nutrition_matches`

Which food we chose for a measurable line.

| Column | Meaning |
|--------|---------|
| `id` | Match row id. |
| `ingredient_id` | The recipe line. At most one match per line. |
| `raw_text` | Line text at match time. |
| `quantity` | Parsed quantity copied onto the match. |
| `unit` | Parsed unit copied onto the match. |
| `parsed_name` | Name we tried to match. |
| `food_id` | The chosen `nutrition_foods` row. Empty if we stored a match attempt without a food (current calculator deletes the row instead when there is no food). |
| `confidence` | How sure the matcher was: `1.00` alias, `0.90` exact name, `0.80` whole-token overlap. |
| `match_source` | `alias`, `name`, or `tokens`. |
| `updated_at` | When this match was last written. |

If this row is missing, the line was not measurable or no food was found.

### `ingredient_nutrition_contributions`

Calories and macros for one matched line, after converting to grams.

| Column | Meaning |
|--------|---------|
| `ingredient_id` | The recipe line. One contribution per line. |
| `grams_resolved` | Grams used in the arithmetic. |
| `grams_source` | How those grams were chosen: `mass` (g/kg/oz/lb), `measure` (this food’s tsp/tbsp/cup/egg/clove), or `density` (water-density fallback: 1 g/ml, 5 g/tsp, 15 g/tbsp, 240 g/cup). This is separate from name-match `confidence`. |
| `calories` … `sodium` | Calculated nutrients for this line. |
| `override_calories` … `override_sodium` | Manual replacements, if a person later corrects a line. Unused in the current calculator path. |
| `uses_user_override` | Whether those override columns are in effect. Currently always false. |
| `updated_at` | When this contribution was last written. |

Optional ingredients are skipped for recipe totals. Alternative groups contribute only one option.

### `nutrition`

Cached nutrition for the whole recipe. Shown on recipe details.

| Column | Meaning |
|--------|---------|
| `id` | Row id. |
| `recipe_id` | Which recipe. At most one nutrition row per recipe. |
| `calories` … `sodium` | Recipe totals (same nutrients as the food table). |
| `matched_ingredient_count` | How many countable lines got both a food and a gram weight. |
| `total_ingredient_count` | How many countable lines were considered (ignores headings/equipment; skips optional). |
| `calculation_source` | `scraped` (copied from the website; calculation will not overwrite) or `calculated` (from our food matches). |
| `confidence` | `complete` if every countable measurable line matched, otherwise `partial`. |
| `is_complete` | True only for a full estimate: every countable line was measurable *and* matched *and* converted to grams. |
| `total_weight_grams` | Sum of matched ingredient grams. |
| `serving_count` | Parsed from `recipes.yields` when possible. |
| `updated_at` | When totals were last written. |

A recipe can have a nutrition row and still be a poor estimate if `matched_ingredient_count` is much smaller than `total_ingredient_count`.

---

## Library and sharing

### `favorites`

Recipes the user starred.

| Column | Meaning |
|--------|---------|
| `user_id` | Who starred it. |
| `recipe_id` | Which recipe. |
| `created_at` | When it was starred. |

The same recipe can only be favorited once per user.

### `cookbooks`

A named collection owned by a user.

| Column | Meaning |
|--------|---------|
| `id` | Cookbook id. |
| `user_id` | Owner. |
| `name` | Display name. |
| `created_at` | When the cookbook was created. |
| `updated_at` | When it was last changed. |

### `cookbook_recipes`

Which recipes sit in which cookbook.

| Column | Meaning |
|--------|---------|
| `cookbook_id` | The cookbook. |
| `recipe_id` | The recipe. |
| `added_at` | When it was added. |

A recipe can appear in many cookbooks; once per cookbook.

### `cookbook_shares`

A share link for a cookbook.

| Column | Meaning |
|--------|---------|
| `token` | Secret in the share URL. |
| `cookbook_id` | Cookbook being shared. |
| `created_by_user_id` | Who created the link. |
| `created_at` | When the link was created. |

### `cookbook_share_imports`

Records that a user imported someone else’s shared cookbook.

| Column | Meaning |
|--------|---------|
| `user_id` | Who imported it. |
| `share_token` | Which share link they used. |
| `imported_cookbook_id` | The copy created in their library. |
| `created_at` | When they imported it. |

Each user can import a given share link only once.

---

## Personal cooking preferences

### `measurement_preferences`

How this user wants amounts displayed.

| Column | Meaning |
|--------|---------|
| `user_id` | Whose preferences. One row per user. |
| `preferred_system` | `IMPERIAL`, `METRIC`, or `MIXED`. |
| `format_handling` | What to do with recipes written in the other system: keep them (`KEEP_AS_IS`), hide them (`FILTER_OUT`), or convert them (`CONVERT_TO_PREFERRED`). |
| `detected_country_code` | Country used to guess the first preference, when we have it. |
| `updated_at` | When preferences were last saved. |

### `measurement_preference_seen_recipes`

Recipes for which the “this recipe uses a different unit system” prompt has already been shown, so it is not shown again.

| Column | Meaning |
|--------|---------|
| `user_id` | Who saw the prompt. |
| `recipe_id` | Which recipe. Stored as a number only; deleting the recipe does not clean these rows. |

### `search_filters`

The user’s last non-ingredient search filters (diet, cuisine, time, and so on), so the filter sheet can restore them. Pantry, exclusions, and key ingredients are **not** stored here.

| Column | Meaning |
|--------|---------|
| `user_id` | Whose saved filters. One row per user. |
| `filters_json` | The filter chip set as JSON. |
| `updated_at` | When it was last saved. |

### `user_pantry`

Ingredients this user says they have. Names are free text. Catalogue chips store the catalogue spelling (`Chicken`); custom pantry items store whatever the user typed.

Pantry search asks: for every required ingredient slot on a recipe, does some pantry string (or a built-in staple such as salt/water) cover that line? Coverage is **name matching**, not `food_id`.

| Column | Meaning |
|--------|---------|
| `user_id` | Whose pantry. |
| `ingredient` | The ingredient name as stored. |

The same name can appear only once per user.

### `user_excluded_ingredients`

Ingredients the user wants recipes to avoid. Same text-matching rules as the pantry.

| Column | Meaning |
|--------|---------|
| `user_id` | Whose exclusions. |
| `ingredient` | Name to exclude. |

A name should not sit in both pantry and exclusions at once; the app keeps those mutually exclusive.

---

## Related reading

- Running the backend: [`README.md`](README.md)
- USDA import, nutrition backfill, and `reportIngredientFoodMatches`: [`NUTRITION.md`](NUTRITION.md)
- Scraping and ingredient-line cleanup: [`../scripts/scraping/README.md`](../scripts/scraping/README.md)
- Filling cuisine / meal type / diet chips: [`../enrichment/README.md`](../enrichment/README.md)
