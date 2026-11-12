# Recipe Attribute Enrichment (`./gradlew enrichRecipes`)

`./gradlew enrichRecipes` is an offline backend CLI tool (`backend/src/tools/kotlin/app/purecipes/backend/tools/EnrichRecipesMain.kt`) that uses the [Universal Sentence Encoder (USE) v4](https://tfhub.dev/google/universal-sentence-encoder/4) — a locally-run TensorFlow SavedModel in `.use_v4/` — to fill in missing recipe attributes in the PostgreSQL database via semantic similarity classification.

## What it classifies

Each recipe that has one or more NULL enrichable columns is processed. The tool writes back only the fields that were NULL; existing values are never overwritten.

| DB column | Domain type | Method | Description | Info |
|-----------|-------------|--------|-------------|------|
| `cuisine` | `Cuisine` | Single-label + threshold | Italian, Indian, Mexican, Thai, British, … | Nullable; only written when top similarity is at least `CUISINE_THRESHOLD = 0.60f` |
| `meal_type` | `MealType` | Single-label | Breakfast, Lunch, Dinner, Snack, Dessert, … | Nullable in schema; enrichment fills only when currently empty |
| `difficulty` | `DifficultyLevel` | Single-label | Easy, Medium, Hard | Nullable in schema; enrichment fills only when currently empty |
| `cooking_method` | `CookingMethod` | Single-label | Bake, Grill, Fry, Stir-Fry, Slow Cook, Steam, … | Nullable in schema; enrichment fills only when currently empty |
| `calorie_range` | `CalorieRange` | Rule-based | LOW < 300 kcal, MEDIUM 300–600, HIGH > 600 | Nullable when nutrition calories are missing |
| `dietary_preferences` | `Set<DietaryPreference>` | Multi-label + threshold | Vegan, Gluten-Free, Keto, Paleo, Halal, … | Can be empty; labels are added only above `DIETARY_THRESHOLD = 0.55f` |

`calorie_range` is derived purely from the `nutrition` table (no ML); the other five use USE embeddings + cosine similarity against pre-built class centroids.

## How classification works

1. A text snippet is built for each recipe: `title + description + aggregated ingredient names`.
2. All snippets in a batch are fed to the USE SavedModel, which returns 512-dimensional embeddings.
3. For each target attribute a **centroid** is pre-computed by encoding a set of seed phrases per class and averaging the resulting vectors (see `SeedExamples.kt`).
4. The recipe embedding is compared against every centroid via cosine similarity.
   - **Single-label**: the class with the highest similarity wins.
   - **Confidence-gated single-label**: cuisine is only written when the best score exceeds `CUISINE_THRESHOLD` (`0.60f`); otherwise it stays null.
   - **Multi-label** (dietary preferences): every class whose similarity exceeds `DIETARY_THRESHOLD` (`0.55f`) is included.

## Prerequisites

1. **Local USE v4 SavedModel** extracted to `.use_v4/` at the repository root (the same directory used by `./gradlew suggestIngredientMatches`; see [`INGREDIENTS.md`](INGREDIENTS.md#2-local-universal-sentence-encoder-v4-model-use_v4)).
2. PostgreSQL database running (connection configured via `PURECIPES_DB_URL`, `PURECIPES_DB_USER`, `PURECIPES_DB_PASSWORD`, defaulting to `jdbc:postgresql://localhost:5432/purecipes` and `postgres`).

## Run

```bash
./gradlew enrichRecipes
```

To override the model path explicitly, pass `-Penrich.modelPath=/path/to/use_v4` or set `USE_MODEL_PATH`.

The tool prints progress per batch and a final `Enrichment complete` line. It is safe to run multiple times; already-filled columns are skipped.

## Improving accuracy

### Tuning seed phrases (`SeedExamples.kt`)

Each enum value maps to a list of short natural-language phrases in `backend/src/tools/kotlin/app/purecipes/backend/tools/SeedExamples.kt`. The centroid is the average of their USE embeddings. To improve a mislabelled class:

- Add more diverse, representative phrases for that class.
- Remove phrases that are ambiguous or overlap with another class.
- Run `./gradlew enrichRecipes` again — changes take effect immediately (no retraining needed).

### Tuning thresholds (`EnrichRecipesMain.kt`)

- `CUISINE_THRESHOLD` (`0.60f`) controls whether cuisine is filled at all. Raise it to leave more uncertain cuisines null; lower it to classify cuisine more aggressively.
- `DIETARY_THRESHOLD` (`0.55f`) controls how confidently a recipe must resemble a dietary preference before it is assigned.
- `BATCH_SIZE` (`32`): larger batches are faster but use more memory.
