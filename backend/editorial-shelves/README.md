# Editorial home shelves

Home can show extra shelves that an editor writes in the database. There is no editor screen in the app. A shelf appears when its row is live and it has public recipes to show. The first twelve recipes are the horizontal row. **See all** opens that shelf on its own screen, titled with the shelf name, and keeps loading in the same order. A recipe opens its details. Back returns to Home.

Two kinds of shelf share one table:

- **Editorial.** A hand-picked list. You choose the recipes and the order.
- **Query.** A saved search. You store filters and a sort, and the shelf fills itself from public recipes.

"This week" is just a title you choose. The app does not treat that name specially.

Editors write the rows with the scripts in [`../../scripts/editorial/README.md`](../../scripts/editorial/README.md). The tables are created the next time the backend starts.

## When a shelf is on screen

A row is live when all of these are true:

- `enabled` is true
- `starts_at` is empty, or it is already in the past
- `ends_at` is empty, or it is still in the future

Dates are compared with the database clock (`CURRENT_TIMESTAMP`). An empty start means "already started". An empty end means "no end date". The end instant itself is not included: a shelf with `ends_at` of Monday 00:00 is gone at that moment.

Live shelves are appended after the automatic shelves (New, Right now, Quick, and, when the person is signed in, Recently viewed, the favorites habit shelf, habit shelves from opens and finished cooks, and Your favorites). Several live shelves can show at once. They are ordered by `position`, then by `id`.

A shelf is left out when it has nothing to show: an editorial list whose recipes are all private or missing, or a query whose filters match nothing. A query row whose `filter_payload` is not valid JSON is also left out. The rest of Home still loads.

Private recipes never appear. Deleting a recipe removes it from every editorial list.

## Slug and title

`title` is the label on the shelf, for example `This week`.

`slug` is the stable id used by **See all**. Use only lowercase letters, digits, and single hyphens, with no hyphen at either end. Up to 64 characters. `this-week` is valid. `This Week` is not, and **See all** will fail for that shelf even if the row itself is live.

Each slug can be used only once.

## A hand-picked list

`source` is `editorial`. The saved filter and sort are ignored. Order comes only from `position` on each recipe row. Lower numbers come first. If two rows share a position, the lower recipe id comes first.

`create_collection` asks for the shelf, then for recipe ids. `?carbonara` searches titles. `update_collection_recipes` replaces that whole order. The same recipe cannot be added twice to one shelf. The home screen reads the tables on the next request, so you do not restart the backend.

## A saved search

`source` is `query`. Recipe rows are ignored. The filter is JSON for the same filters as the search screen. Leave it empty, or use `{}`, to mean every public recipe.

`sort` is `catalog` or `newest`. Anything else, including a blank value, is treated as `catalog`. `newest` orders by when the recipe was added. `catalog` uses the catalog ranking.

Cuisines use the display name (`Italian`, `Middle Eastern`). Every other filter uses the enum name:

| JSON field | Example values |
|------------|----------------|
| `dietaryPreferences` | `VEGAN`, `VEGETARIAN`, `GLUTEN_FREE`, `KETO` |
| `cuisines` | `Italian`, `Japanese`, `Middle Eastern` |
| `mealTypes` | `BREAKFAST`, `LUNCH`, `DINNER`, `DESSERT` |
| `cookingTimeRanges` | `UNDER_15`, `UNDER_30`, `UNDER_60`, `OVER_60` |
| `difficultyLevels` | `EASY`, `MEDIUM`, `HARD` |
| `cookingMethods` | `BAKE`, `GRILL`, `AIR_FRY` |
| `calorieRanges` | `LOW`, `MEDIUM`, `HIGH` |
| `nutritionFilters` | `LOW_CARB`, `HIGH_PROTEIN`, `HIGH_FIBER` |

Omit a field to leave it unlimited. Values inside one field match any of the listed choices, except nutrition filters, which all have to match. Unknown JSON fields are ignored. If the JSON cannot be parsed, that shelf is hidden.

Query shelves are the same list for everyone. They do not apply a person's pantry or excluded ingredients.

## Retire a shelf

In `update_collection`, set enabled to no, or set the end to a time that has passed. `delete_collection` removes the shelf and its recipe rows.
