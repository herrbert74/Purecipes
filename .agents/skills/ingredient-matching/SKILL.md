---
name: ingredient-matching
description: Review, match, pin, and override ingredient lines and names using the reproducible ingredient pipeline.
---

# Skill: Ingredient Matching & Pipeline Maintenance

Use this skill when auditing ingredient matches, fixing misclassified ingredient lines or headings, resolving false pantry/food matches, or processing new scrape batches.

## Core Principle: "Pin Before Rule"

In Purecipes, ingredient matching and classification must be reproducible and regression-safe:

1. **Specific beats general:** A reviewed decision belongs in a committed data file, not in global heuristic code.
2. **Blast radius awareness:** Every change to a global regex, normalizer token, or alias dictionary risks silently breaking hundreds of unrelated ingredients across other recipes.
3. **ML suggests, humans or agents decide:** The offline suggestion tool and local ML models draft candidates into `suggestions.tsv`. Only reviewed and accepted rows become pins in `names.tsv`.

### Decision Hierarchy

When addressing an ingredient matching or parsing issue, follow this order:

```
Is the issue specific to ONE idiosyncratic recipe line?
├── YES ──► Add row to line-overrides.tsv (override kind, group_name, quantity, unit, or lookup_name)
└── NO
     ├── Does a specific food name need a USDA food match or pantry category?
     │    └── YES ──► Pin the lookup_name in names.tsv (status = pinned)
     └── Is there a truly general linguistic pattern across multiple unrelated recipes?
          └── YES ──► Update parser/normalizer rules in code, add comprehensive unit tests, and verify diff
```

---

## Data Files (`backend/data/ingredients/`)

All files are UTF-8 tab-separated values (TSV) with headers, sorted by their primary key.

| File | Purpose | Primary Key |
|---|---|---|
| [`names.tsv`](file:///Users/zsoltbertalan/git/Purecipes/backend/data/ingredients/names.tsv) | Master snapshot of all lookup names. Rows with `status = pinned` override rules with top priority. | `lookup_name` |
| [`line-overrides.tsv`](file:///Users/zsoltbertalan/git/Purecipes/backend/data/ingredients/line-overrides.tsv) | Per-line overrides for lines rules should not try to generalize. | `source_url` + `raw_text` |
| [`suggestions.tsv`](file:///Users/zsoltbertalan/git/Purecipes/backend/data/ingredients/suggestions.tsv) | Ephemeral candidate matches generated offline for review. | `lookup_name` |
| [`lines.tsv`](file:///Users/zsoltbertalan/git/Purecipes/backend/data/ingredients/lines.tsv) | Full snapshot of parsed lines and group assignments across the database. | `source_url` + `group_index` + `line_index` |

---

## Agent Playbooks

### Playbook 1: Fixing a False Food or Pantry Match

**Symptom:** An ingredient is matched to the wrong USDA food or wrong pantry category (e.g. `"beer"` matched `"root beer"`, or `"45 g butter, divided in three parts"` matched `"butter three"`).

1. **Find the lookup name:**
   Search for the ingredient in `names.tsv`:
   ```bash
   grep -P "^<lookup_name>\t" backend/data/ingredients/names.tsv
   ```
2. **Determine the correct USDA match and pantry category:**
   - Run suggestion search:
     ```bash
     ./gradlew suggestIngredientMatches -Psuggest.query="<keyword>"
     ```
   - Or query the local Postgres database directly for USDA candidates:
     ```sql
     SELECT source_name, source_id, display_name
     FROM nutrition_foods
     WHERE normalized_name LIKE '%<keyword>%'
     ORDER BY source_name ASC LIMIT 10;
     ```
3. **Pin the correct match in `names.tsv`:**
   Set `status` to `pinned`, set `pantry_item` to the exact title-cased `IngredientCatalogue` item (or leave empty if no valid pantry item), and set `fdc_source` and `fdc_id`:
   ```tsv
   butter	pinned	Butter	fdc_foundation	789828	Butter, stick, unsalted	pinned	12
   ```
4. **Verify the change:**
   ```bash
   ./gradlew diffIngredientSnapshot
   ```
   Confirm the diff shows only the targeted `~` changes and zero unexpected regressions.

---

### Playbook 2: Generating & Reviewing Candidate Matches

**Symptom:** Unmatched ingredients or weakly matched lines (`match_source = tokens`) need review.

1. **Generate suggestions:**
   ```bash
   # Generate for all unmatched ingredients (uses local .use_v4 model automatically):
   ./gradlew suggestIngredientMatches

   # Or target token-guessed matches:
   ./gradlew suggestIngredientMatches -Psuggest.scope=tokens

   # Or filter by query:
   ./gradlew suggestIngredientMatches -Psuggest.query=vinegar
   ```
2. **Review `suggestions.tsv`:**
   Open `backend/data/ingredients/suggestions.tsv`. For each candidate:
   - Check `sample_recipe_title` and `sample_raw_text` to understand the true culinary context.
   - If the candidate `fdc_description` and `pantry_item` are correct: change `status` from `suggested` to `pinned`.
   - If the candidate is slightly off but `alternative_candidates` contains the right match: copy the alternative FDC source and ID into `fdc_source` and `fdc_id`, and set `status` to `pinned`.
   - If no valid match exists: leave `status` as `suggested` (or remove the row).
3. **Promote accepted pins:**
   ```bash
   ./gradlew suggestIngredientMatches -Psuggest.accept=true
   ```
   This merges all rows with `status = pinned` into `names.tsv` while preserving alphabetical sorting.
4. **Verify the diff:**
   ```bash
   ./gradlew diffIngredientSnapshot
   ```

---

### Playbook 3: Handling Misclassified Headings or Idiosyncratic Lines

**Symptom:** A recipe instruction or non-ingredient line (e.g. `"Chimichurri"` or `"For the glaze"`) was stored as an ingredient, or an author wrote an unusual composite line that the parser cannot parse.

1. **Check if it is a general heading pattern:**
   - If the line is clearly a section heading (ends with `:`, or matches existing patterns like `"For the <group>"`), check if `isSharedSectionHeadingOrNote` in `ScrapedIngredientLines.kt` should recognize it.
   - If so, update the shared rules, run `./gradlew :backend:test`, and check `diffIngredientSnapshot`.
2. **If it is idiosyncratic to a specific recipe:**
   Add an override to `backend/data/ingredients/line-overrides.tsv`:
   ```tsv
   source_url	raw_text	kind	group_name	quantity	unit	lookup_name
   https://example.com/recipe	Chimichurri	heading	Chimichurri			
   https://example.com/recipe	Special Chef Equipment	equipment				
   ```
3. **Rebuild derived data:**
   ```bash
   ./gradlew rebuildIngredientData
   ./gradlew diffIngredientSnapshot
   ```

---

## Validation Checklist

Before finishing any ingredient matching or pipeline task, the agent must ensure:

- [ ] All new pins in `names.tsv` use valid `fdc_source` and `fdc_id` from the database.
- [ ] `./gradlew diffIngredientSnapshot` was executed and reported expected additions/improvements with no unintended regressions.
- [ ] `./gradlew :backend:test` passes all backend unit tests.
- [ ] `./gradlew detektAll` passes with 0 violations.
- [ ] Code formatting complies with project standards (tabs for indentation, sorted imports, single trailing newline).
