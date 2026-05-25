# Calorie Calculator & Nutrition Tracker

## Status: <span style="color:orange;">DRAFT - REFINED MVP</span>

## Product Goal

Give users a useful nutrition estimate without making recipe creation slower, expensive, or dependent on AI
for every interaction.

The first version should show:

- A nutrition summary on the recipe details page when nutrition data is available.
- A live nutrition estimate on the create/edit recipe screen that updates as ingredient lines change.
- Clear confidence messaging so users understand that early results are estimates, not medical-grade analysis.

Serving-size recalculation, daily goal tracking, allergens, vitamins, minerals, and fitness integrations are
out of scope for the first implementation.

## Recommended Direction

Use a database-first nutrition system.

The app should not call AI for every recipe view, every ingredient edit, or every user. At scale that creates
avoidable cost, latency, quality-control, and reliability problems. Instead, normalise ingredient nutrition
data into our own database on a scheduled basis, calculate recipe totals with deterministic code, and use AI
only later for offline backfill or low-confidence matching.

The first implementation should solve the missing-data problem directly: seed our own ingredient nutrition
table from a small USDA FoodData Central import, calculate recipe totals deterministically, and store
recipe-level summaries as a cache/output. A tiny curated fallback table can be used while the importer is
being built, but the product path should be designed around imported ingredient data rather than scraped
recipe nutrition.

The backend is the first implementation step, not the last. The feature is not finished until the backend can
store measured ingredients, calculate recipe nutrition, and backfill the existing recipe database.

## Current Codebase Starting Point

The backend already has a `nutrition` table:

- `recipe_id`
- `calories`
- `protein`
- `carbohydrates`
- `fat`
- `fiber`
- `sugar`
- `sodium`

The recipe scraper can insert scraped nutrition into this table when source websites provide it, but the
currently scraped websites do not provide nutrition data, so this table is effectively empty. That means
exposing existing recipe nutrition alone is not enough for the MVP.

`RecipeDetails` currently exposes `calorieRange`, but not the actual nutrition totals. Feature 14 should add
a shared nutrition model and include it in recipe details, but the main first-day work is to create nutrition
totals from ingredients.

## Completion Requirements

This feature is considered finished only when all of these are true:

- All recipe ingredients have a raw text value plus parsed measurement data: quantity, unit, and ingredient
  name.
- Ingredients that cannot be measured are stored as incomplete and prevent that recipe from being marked as a
  complete nutrition estimate.
- The backend can match measured ingredients to canonical nutrition foods and calculate recipe totals.
- The recipe scraper writes ingredient measurement data and triggers nutrition calculation for new recipes.
- Existing scraped recipes are backfilled through the same backend calculation path.
- The app can show which ingredients were included, which were missing, and whether the total is complete or
  partial.

## Revised MVP Data Strategy

Use USDA FoodData Central downloadable data as the first source of truth.

For today's implementation, prefer a small seed import over a full provider-sync pipeline:

- Download/import Foundation Foods and optionally SR Legacy from FoodData Central.
- Keep only the nutrients needed for the MVP: calories, protein, carbohydrates, fat, fibre, sugar, and sodium.
- Create canonical food rows with names and nutrients per 100g.
- Use the existing ingredient catalogue from `IngredientFilterSection` as the initial coverage checklist.
- Add a handwritten alias list for common recipe terms, for example "caster sugar", "olive oil", "egg",
  "flour", "onion", "garlic", "chicken breast", "butter", and "milk".
- Add a small measure table for common conversions, for example cup, tbsp, tsp, clove, egg, and piece.
- Calculate recipe totals from parsed ingredient lines and matched canonical foods.

This is not a dynamic API call. The external source is used to populate/update our own data. Runtime recipe
details and create-form estimates should read our data and run local/backend arithmetic.

The first version can be intentionally narrow. It is better to estimate 20 common ingredients
deterministically with clear match coverage than to pretend every ingredient is accurately covered.

## Seed Importer

A seed importer is a nutrition-specific import tool, similar in spirit to the recipe scraper but focused only
on provider nutrition data. It should be a script or standalone Gradle/JVM tool that:

- Reads FoodData Central CSV or JSON downloads.
- Extracts only the foods and nutrients we need.
- Normalises names into canonical food rows.
- Writes local seed data or backend database rows for foods, aliases, and measure conversions.

It should not be part of the recipe scraper at first. The scraper collects recipes; the nutrition importer
collects ingredient nutrition facts. Later, the scraper can trigger the same deterministic recipe calculator
after saving a recipe, or a backend job can recalculate all recipes in batches.

## Ingredient Catalogue Reuse

`IngredientFilterSection` currently owns a private UI-only ingredient catalogue. Feature 14 should move that
catalogue to a shared package before depending on it for nutrition work.

Suggested shape:

- Move ingredient group data to a shared/domain model, for example `IngredientCatalogue` and `IngredientGroup`.
- Keep search UI rendering in `feature/search/ui`, but consume the shared catalogue.
- Use the same catalogue as the first list of foods to match against FoodData Central.
- Track which catalogue ingredients have a canonical nutrition match and which still need aliases or review.

This gives the nutrition work an app-specific ingredient list immediately instead of starting from all USDA
foods.

## Licence Decision

Licence obligations must be clear before implementation starts.

USDA FoodData Central is the safest first source. Its data is published under CC0 1.0, which means there is no
copyright restriction and no permission requirement. USDA requests attribution to FoodData Central as the data
source, but this is not a share-alike requirement. This is not a show-stopper.

Open Food Facts is useful later for branded packaged foods, but it should not be part of the first import
unless we deliberately accept its Open Database Licence obligations. Those obligations include attribution and
share-alike. If we combine Open Food Facts data with our own nutrition database, the resulting combined
database may also need to be released as open data under compatible terms. That could be a show-stopper if we
want to keep the combined nutrition database proprietary.

MVP decision:

- Use USDA FoodData Central only for the first implementation.
- Add FoodData Central attribution in documentation and any required product/legal page.
- Do not import Open Food Facts until we decide whether its share-alike obligation is acceptable.

## Approach Options

### Option 1: AI on Every Calculation

This means sending a recipe's ingredients to an LLM whenever the user creates, edits, views, or recalculates
nutrition.

Pros:

- Fastest path to broad ingredient understanding.
- Handles messy free-text ingredients better than simple parsing.
- Can explain uncertainty and suggest corrections.

Cons:

- Poor fit for live ingredient editing because every keystroke or debounce can become a paid network request.
- Harder to make deterministic. The same input can vary between model versions or prompts.
- Requires privacy review because ingredient data and possibly health-related preferences leave our system.
- Needs caching, retries, rate limiting, abuse protection, evaluation sets, and observability from day one.
- At one million users, even cheap models become a permanent variable cost.

Cost shape:

- If one million users each trigger one AI nutrition calculation per month, and each call uses roughly
  2,000 input tokens and 300 output tokens, a cheap cloud model priced around GPT-4.1 mini levels would be
  roughly low-thousands USD per month before retries, evaluation, logging, and engineering overhead.
- If users create or edit recipes frequently, or if live editing calls AI repeatedly, costs can grow by 10x or
  more.
- Larger models can improve parsing quality but make the economics much worse.

Verdict:

Do not make this the main path. It is useful as an offline assistant for ingredient normalisation, not as the
runtime calculator.

### Option 1b: Local Small Models

This means using an on-device or self-hosted local model such as a Nano/Gemma-class model instead of a
cloud-hosted API.

Pros:

- Avoids per-call cloud AI pricing.
- Can improve privacy if inference runs fully on-device.
- Can support offline or near-offline parsing for users whose devices can run it.
- Could help normalise difficult ingredient strings without sending them to a third party.

Cons:

- Not actually free at scale. On-device inference costs battery, memory, app size, startup time, and QA across
  Android, iOS, and web targets.
- Self-hosted inference moves the cost from API bills to GPU/CPU infrastructure, autoscaling, monitoring, and
  model operations.
- Small local models may be good at parsing text, but they do not contain reliable, current nutrition facts.
  They still need our ingredient nutrition database.
- Output quality and latency will vary across user devices.
- Shipping models to clients creates versioning and update complexity.

Verdict:

Local models are more interesting than cloud APIs for privacy and long-term marginal cost, but they should not
replace the deterministic nutrition database. They are best considered later for optional ingredient parsing,
alias suggestions, or offline backfill. The first implementation should work without any model.

### Option 2: Dynamic API Calls to Nutrition Providers

This means calling USDA FoodData Central, Open Food Facts, or similar providers as users edit or view recipes.

Pros:

- Uses authoritative external data.
- Lower direct cost than AI.
- Good for lookup screens, admin tooling, and missing-data workflows.

Cons:

- Not suitable as the hot path for live ingredient editing at scale.
- External APIs have rate limits, latency, outages, schema differences, and attribution/licence requirements.
- Ingredient matching still remains our problem. "1 cup chopped onion" has to become a canonical food plus
  grams.
- Open Food Facts is strongest for branded packaged foods, while USDA is stronger for generic foods. Recipes
  need both, but they are not interchangeable.

Cost shape:

- Direct API cost may be low or free, but operational cost appears in caching, queueing, rate-limit handling,
  provider-specific normalisation, and support when upstream data changes.
- Open Food Facts bulk data has licence obligations, including attribution and share-alike considerations for
  derived combined databases.

Verdict:

Use dynamic API calls only in backend/admin workflows and cache everything we accept. Do not call external
providers from clients or per keystroke.

### Option 3: Periodic Sync Into Our Database

This means importing nutrition data from external sources into our own canonical ingredient nutrition tables,
then calculating locally/backend-side from that data.

Pros:

- Best long-term cost profile.
- Fast enough for live UI calculation when the relevant food table is local or already loaded.
- Deterministic and testable.
- Works offline or with poor network if a small nutrition dataset ships with the app.
- Lets us inspect, correct, and version ingredient matches.

Cons:

- Requires schema design for canonical foods, aliases, nutrients per 100g, density/common measure mappings,
  and source metadata.
- Initial matching quality will be uneven until we improve aliases and unit conversion.
- Bulk data import and licence compliance need deliberate handling.

Cost shape:

- Mostly engineering and storage cost.
- Runtime cost is small because recipe totals are simple arithmetic after matching ingredient quantities to
  food nutrition per 100g.
- Scales well to one million users because repeated views and edits do not call paid third parties.

Verdict:

This should be the main architecture.

### Option 4: Hybrid Database-First With AI Backfill

This means deterministic calculation is always the product path, while AI helps offline when confidence is low.

Examples:

- Batch-process unmatched ingredient strings and propose canonical food matches.
- Suggest aliases such as "caster sugar" -> "sugar, granulated" for human review or confidence-gated
  acceptance.
- Parse difficult recipe imports once, store the structured result, and never recompute through AI unless the
  source changes.

Pros:

- Gets the quality benefits of AI without paying for every user interaction.
- Keeps the user experience fast and predictable.
- Allows human review and confidence thresholds.

Cons:

- Needs tooling to review and accept AI suggestions.
- Still needs evaluation and monitoring to prevent bad matches.

Verdict:

This is the best long-term plan after the deterministic MVP.

## MVP Scope To Build Now

### User-Facing Behavior

Recipe details page:

- Show a nutrition card when the recipe has calculated or imported nutrition data.
- Include calories, protein, carbohydrates, fat, fibre, sugar, and sodium when available.
- Hide missing nutrients instead of showing zero.
- Label the card as "Nutrition estimate" and show match coverage when it comes from ingredient matching.

Create/edit recipe screen:

- Show a live nutrition estimate below the ingredients field.
- Recalculate locally when `ingredientsInput` changes.
- Display matched ingredient count, for example "Estimated from 3 of 5 ingredients".
- If nothing matches, show a helpful empty state instead of an error.
- Do not block saving when nutrition cannot be calculated.

### Technical Slice

Shared domain:

- Add `NutritionSummary` to `shared/domain`.
- Add `nutrition: NutritionSummary? = null` to `RecipeDetails`.
- Keep values nullable so partially known nutrition can be represented honestly.
- Add estimate metadata: matched ingredient count, total ingredient count, and source/confidence.

Backend:

- Add minimal canonical nutrition tables for foods, aliases, and measures.
- Add parsed ingredient measurement storage for quantity, unit, parsed ingredient name, and raw text.
- Add a seed importer for a small USDA FoodData Central subset or checked-in seed file generated from that
  subset.
- Add deterministic ingredient matching and nutrition calculation.
- Store calculated totals in the existing `nutrition` table or a new recipe-nutrition cache table.

Scraper and backfill:

- Update the recipe scraper to parse and save ingredient measurements for new scraped recipes.
- Run a backfill over existing recipes to parse ingredient measurements and calculate nutrition totals.
- Keep unmatched or unmeasured ingredients visible in the backfill output so aliases and measures can be
  improved.

Recipe details UI:

- Add a reusable `NutritionSummaryCard`.
- Render it near the metadata/ingredients area when `recipe.nutrition != null`.

Create recipe UI:

- Add a deterministic `EstimateRecipeNutritionUseCase`.
- Back it with the same backend/domain nutrition repository that is populated by the FoodData Central seed
  importer.
- Parse simple ingredient lines first: amount, unit, ingredient name.
- Support a narrow unit set initially: `g`, `kg`, `ml`, `l`, `tsp`, `tbsp`, `cup`, `oz`, `lb`, and common
  count-based items like egg.
- Return both totals and match confidence metadata.

Tests:

- Unit-test the nutrition calculator with a few representative ingredient lines.
- Unit-test details mapping from backend nutrition rows.
- Add UI/view-model tests for live create-form updates.

## Suggested First-Day Implementation Order

1. Confirm the licence decision: USDA FoodData Central only for MVP, Open Food Facts deferred.
2. Move the ingredient catalogue out of `IngredientFilterSection` into a shared package.
3. Add backend tables for canonical foods, aliases, measures, parsed ingredients, and recipe nutrition totals.
4. Add the FoodData Central seed importer and import the first common-food subset.
5. Add backend deterministic parsing, matching, and nutrition calculation.
6. Update the recipe scraper to write parsed ingredient measurements and calculated nutrition.
7. Run a backfill job for existing scraped recipes.
8. Expose nutrition and estimate metadata through recipe details.
9. Add the create/edit recipe live estimate UI using the same backend/domain calculation path.

This gives users visible value while ensuring the database is populated and the scraper continues to produce
nutrition data for new recipes.

## Future Database-First Architecture

Add canonical ingredient nutrition tables:

- `nutrition_foods`: source id, source name, display name, normalised name, nutrients per 100g, source
  metadata, updated timestamp.
- `nutrition_food_aliases`: alternate names and regional terms mapped to canonical food ids.
- `nutrition_food_measures`: common measures mapped to grams, for example cup, tbsp, tsp, piece, clove.
- `recipe_nutrition`: calculated totals for saved recipes, including matched ingredient count, total
  ingredient count, confidence, source, and updated timestamp.
- `ingredient_nutrition_matches`: raw ingredient text, parsed quantity/unit/name, matched food id,
  confidence, and match source.

Periodic jobs should import or refresh provider data, normalise it into these tables, and recalculate affected
recipe totals. Runtime app screens should read our stored data, not providers directly.

## Deferred Scope

- Serving-size recalculation.
- Daily nutrition goal tracking.
- User health profiles.
- Allergy alerts.
- Full micronutrients.
- Fitness app integrations.
- AI-powered explanations.
- User correction workflow.
- Full provider sync pipeline.

## Accuracy And Trust

Nutrition should be presented as an estimate unless all inputs are provider-supplied and quantity parsing is
high confidence.

Important uncertainty sources:

- Raw ingredient text may omit quantities.
- Household measures vary by ingredient.
- Preparation changes nutrition values.
- Branded and generic foods can differ materially.
- Recipes often list garnish, optional ingredients, or "to taste" quantities.

The UI should avoid medical claims and should not imply clinical precision.

## Success Metrics

MVP:

- Backend nutrition tables and the FoodData Central seed import are in place.
- The scraper writes measured ingredients and calculated nutrition for new recipes.
- Existing recipes are backfilled with measured ingredients and calculated nutrition where possible.
- Details page shows calculated nutrition for recipes whose ingredients can be matched.
- Create recipe form recalculates without AI or external provider calls.
- Calculator reports match coverage.
- No measurable slowdown in recipe creation.

Later:

- More than 70% of common user-entered ingredient lines match a known food.
- Users can correct low-confidence matches.
- Saved recipe nutrition is stable and reproducible across app sessions.
- AI spend remains bounded because AI is batch/offline only.

## Open Questions

- Where should FoodData Central attribution appear in the product and documentation?
- Should the create form call the backend once per debounced ingredient-list change, or should it use an
  embedded subset for offline previews after the backend calculator exists?
- What confidence threshold should allow automatic recipe nutrition persistence?
