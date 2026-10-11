# Editorial collection scripts

Editors use these scripts to write home shelves. The app has no editor screen. Each run asks where the database is. That answer is not stored. The database password is not put on the command line or in shell history.

Run them from the repo root. The first run downloads one Kotlin library, so it needs a network connection. The tables exist after the backend has been started once.

How a shelf gets onto Home, including dates, slugs, and filter JSON, is in [`../../backend/editorial-shelves/README.md`](../../backend/editorial-shelves/README.md).

## Connection

Every script connects before it asks for anything else. Type `local` or `remote` at `Connection:`. Enter accepts a value shown in brackets. The password is hidden when the terminal can hide it. A failed login asks for the connection again. `Connected.` is printed before the next question.

`local` uses host `localhost`, user `postgres`, and password `postgres`.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
```

`remote` asks for the host, database, user, port, SSL mode, and password. Enter accepts port `5432`, database `purecipes`, and SSL mode `require`.

```text
Connection: remote
Host: db.example.com
Database [purecipes]:
User: purecipes
Port [5432]:
SSL mode [require]:
Password:
Connected.
```

## One session

```bash
kotlin scripts/editorial/session.main.kts
```

After `Connected.`, the same login is reused for every command. The commands are `list`, `find`, `create`, `update`, `recipes`, `delete`, and `quit`.

## Separate commands

Each script asks for the connection again.

| Script | What it writes |
|--------|----------------|
| [`list_collections.main.kts`](list_collections.main.kts) | Nothing. Shows every shelf, whether it is live, and how many recipes it has. |
| [`find_recipes.main.kts`](find_recipes.main.kts) | Nothing. Looks up recipes by title and prints ids. |
| [`create_collection.main.kts`](create_collection.main.kts) | A new editorial list or a saved search. |
| [`update_collection.main.kts`](update_collection.main.kts) | Title, slug, source, dates, position, enabled, sort, and query filters. |
| [`update_collection_recipes.main.kts`](update_collection_recipes.main.kts) | Replaces the ordered recipe list of an editorial shelf. |
| [`delete_collection.main.kts`](delete_collection.main.kts) | Deletes a shelf after you type its slug again. |

A standalone script is `kotlin scripts/editorial/<name>.main.kts`. It asks the same questions as the session and skips `Command:`.

## Examples

These transcripts use `local`. A line that stops at the colon means Enter. In a session, later commands skip the connection block and start at `Command:`.

`list` prints the shelves.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: list
pos  slug  title  source  state  recipes  dates
0  this-week  This week  editorial  live  2 recipes  2026-10-12 00:00:00 .. 2026-10-19 00:00:00
```

`find` prints recipe ids for a title.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: find
Title contains: carbonara
1042  Spaghetti Carbonara
880  Cacio e Pepe
```

`create` writes a shelf. Enter on `Source` keeps `editorial`. A query shelf asks for `Sort` and `Filter` instead of recipe ids. `Save` defaults to no.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: create
Slug: this-week
Title: This week
Source [editorial]:
Position [0]:
Enabled [Y/n]:
Starts at is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS. Blank keeps the shown value. - clears it.
Starts at: 2026-10-12
Ends at is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS. Blank keeps the shown value. - clears it.
Ends at: 2026-10-19
Recipe ids in order, one per line. ?text searches titles. A blank line finishes.
Recipe: 1042
Recipe: 880
Recipe:
0  1042  Spaghetti Carbonara
1  880  Cacio e Pepe
this-week  This week  editorial  position 0  enabled true  starts 2026-10-12 00:00:00, ends 2026-10-19 00:00:00  sort catalog  filter none
Save [y/N]: y
BEGIN
INSERT 0 1
INSERT 0 2
COMMIT
Saved this-week.
```

`update` changes the metadata. Enter keeps the value in brackets. This example moves the end date.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: update
Slug: this-week
0  this-week  This week  editorial  live  2 recipes  2026-10-12 00:00:00 .. 2026-10-19 00:00:00
Slug [this-week]:
Title [This week]:
Source [editorial]:
Position [0]:
Enabled [Y/n]:
Starts at is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS. Blank keeps the shown value. - clears it.
Starts at [2026-10-12 00:00:00]:
Ends at is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS. Blank keeps the shown value. - clears it.
Ends at [2026-10-19 00:00:00]: 2026-10-26
this-week  This week  editorial  position 0  enabled true  starts 2026-10-12 00:00:00, ends 2026-10-26 00:00:00  sort catalog  filter none
Save [y/N]: y
UPDATE 1
Updated this-week.
```

`recipes` replaces the hand-picked list. `?cacio` searches, then a number appends that recipe.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: recipes
Slug: this-week
0  1042  Spaghetti Carbonara
1  880  Cacio e Pepe
Recipe ids in order, one per line. ?text searches titles. A blank line finishes.
Recipe: 1042
Recipe: ?cacio
1  880  Cacio e Pepe
Pick a number, or leave blank to skip: 1
Recipe:
0  1042  Spaghetti Carbonara
1  880  Cacio e Pepe
Replace the recipe list for this-week?
Save [y/N]: y
BEGIN
DELETE 2
INSERT 0 2
COMMIT
Updated recipes for this-week.
```

`delete` removes the shelf after the slug is typed again.

```text
Connection: local
Database [purecipes]:
Password [postgres]:
Connected.
Command: delete
Slug: this-week
Delete This week (this-week) and its recipe list.
Type the slug to confirm: this-week
DELETE 1
Deleted this-week.
```

`quit` ends the session.

```text
Command: quit
```

## What the prompts expect

- **Slug.** Lowercase letters, digits, and single hyphens, such as `this-week`. This is what **See all** uses.
- **Source.** `editorial` for a hand-picked list, or `query` for a saved search.
- **Dates.** `YYYY-MM-DD` or `YYYY-MM-DD HH:MM:SS`, in the database clock. Blank keeps the shown value. `-` clears it. An empty start means already started. An empty end means no end date. The end instant itself is not included.
- **Position.** Lower numbers appear first among these shelves. They still come after New, Right now, Quick, and the signed-in personal shelves.
- **Editorial recipes.** One recipe id per line, in the order the shelf should show. `?carbonara` searches titles. A blank line finishes. Private recipes can be stored, and the script warns that they stay hidden.
- **Query filter.** One line of JSON. Blank means every public recipe. Sort is `catalog` or `newest`.

Example filter:

```json
{"cuisines":["Italian"],"mealTypes":["DINNER"]}
```

Cuisines use the display name (`Italian`, `Middle Eastern`). Other filters use the enum name (`DINNER`, `UNDER_30`, `VEGAN`). A query shelf ignores any hand-picked rows. An editorial shelf ignores the filter and the sort. **See all** shows the shelf on its own screen. It does not open search.

## Checks

```bash
kotlin scripts/editorial/editorial_collections_test.main.kts
```
