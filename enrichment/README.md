# Enrichment Module

The `:enrichment` module is a standalone command-line tool that uses the
[Universal Sentence Encoder (USE) v4](https://tfhub.dev/google/universal-sentence-encoder/4) —
a free, locally-run TensorFlow SavedModel — to fill in missing recipe attributes in the
PostgreSQL database via semantic similarity classification.

## What it classifies

Each recipe that has one or more NULL enrichable columns is processed. The tool writes back
only the fields that were NULL; existing values are never overwritten.

| DB column              | Domain type                | Method                  | Description                                      | Info |
|------------------------|----------------------------|-------------------------|--------------------------------------------------|------|
| `cuisine`              | `Cuisine`                  | Single-label + threshold | Italian, Indian, Mexican, Thai, British, …      | Nullable; only written when top similarity is at least `CUISINE_THRESHOLD = 0.60f` |
| `meal_type`            | `MealType`                 | Single-label            | Breakfast, Lunch, Dinner, Snack, Dessert, …      | Nullable in schema; enrichment fills only when currently empty |
| `difficulty`           | `DifficultyLevel`          | Single-label            | Easy, Medium, Hard                               | Nullable in schema; enrichment fills only when currently empty |
| `cooking_method`       | `CookingMethod`            | Single-label            | Bake, Grill, Fry, Stir-Fry, Slow Cook, Steam, … | Nullable in schema; enrichment fills only when currently empty |
| `calorie_range`        | `CalorieRange`             | Rule-based              | LOW < 300 kcal, MEDIUM 300–600, HIGH > 600       | Nullable when nutrition calories are missing |
| `dietary_preferences`  | `Set<DietaryPreference>`   | Multi-label + threshold | Vegan, Gluten-Free, Keto, Paleo, Halal, …       | Can be empty; labels are added only above `DIETARY_THRESHOLD = 0.55f` |

`calorie_range` is derived purely from the `nutrition` table (no ML); the other five use
USE embeddings + cosine similarity against pre-built class centroids.

## How classification works

1. A text snippet is built for each recipe: `title + description + aggregated ingredient names`.
2. All snippets in a batch are fed to the USE SavedModel, which returns 512-dimensional embeddings.
3. For each target attribute a **centroid** is pre-computed by encoding a set of seed phrases
   per class and averaging the resulting vectors (see `SeedExamples.kt`).
4. The recipe embedding is compared against every centroid via cosine similarity.
   - **Single-label**: the class with the highest similarity wins.
   - **Confidence-gated single-label**: cuisine is only written when the best score exceeds
     `CUISINE_THRESHOLD` (`0.60f`); otherwise it stays null.
   - **Multi-label** (dietary preferences): every class whose similarity exceeds
     `DIETARY_THRESHOLD` (`0.55f`) is included.

## Prerequisites

1. **Download USE v4** from TF Hub:
   ```bash
   # Downloads ~1 GB to ./use_v4/
   python -c "import tensorflow_hub as hub; hub.load('https://tfhub.dev/google/universal-sentence-encoder/4')"
   # or manually download the SavedModel archive from:
   # https://tfhub.dev/google/universal-sentence-encoder/4?tf-hub-format=compressed
   ```
2. Make sure the Purecipes PostgreSQL database is running and the schema has been initialised
   by starting the backend at least once (it runs `ALTER TABLE … ADD COLUMN IF NOT EXISTS`
   migrations automatically).

## Environment variables

| Variable               | Required | Example                                    |
|------------------------|----------|--------------------------------------------|
| `USE_MODEL_PATH`       | Yes      | `/path/to/use_v4`                          |
| `PURECIPES_DB_URL`     | Yes      | `jdbc:postgresql://localhost:5432/purecipes` |
| `PURECIPES_DB_USER`    | Yes      | `postgres`                                 |
| `PURECIPES_DB_PASSWORD`| Yes      | `postgres`                                 |

## Run

```bash
USE_MODEL_PATH=/path/to/use_v4 \
PURECIPES_DB_URL=jdbc:postgresql://localhost:5432/purecipes \
PURECIPES_DB_USER=postgres \
PURECIPES_DB_PASSWORD=postgres \
./gradlew :enrichment:run
```

The tool prints progress per batch and a final "Enrichment complete" line.
It is safe to run multiple times; already-filled columns are skipped.

## Improving accuracy

### Tuning seed phrases (`SeedExamples.kt`)

Each enum value maps to a list of short natural-language phrases. The centroid is the average
of their USE embeddings. To improve a mislabelled class:

- Add more diverse, representative phrases for that class.
- Remove phrases that are ambiguous or overlap with another class.
- Run the tool again — changes take effect immediately (no retraining needed).

### Tuning thresholds

`CUISINE_THRESHOLD` in `EnrichmentRunner.kt` controls whether cuisine is filled at all. The current default is `0.60f`.

- Raise it to leave more uncertain cuisines null.
- Lower it to classify cuisine more aggressively.

`DIETARY_THRESHOLD` in `EnrichmentRunner.kt` controls how confidently a recipe must resemble a
dietary preference before it is assigned. The current default is `0.55f`.

- Raise it (e.g. `0.65f`) to reduce false positives (fewer preferences assigned per recipe).
- Lower it (e.g. `0.45f`) to increase recall (more preferences assigned, but noisier).

### Batch size

`BATCH_SIZE = 32` in `EnrichmentRunner.kt`. Larger batches are faster but use more memory.
Reduce it if the process runs out of heap.

### Switching to a larger model

The USE v4 (`universal-sentence-encoder`) produces 512-dim embeddings. Alternatives that use
the same SavedModel API:

| Model                               | Dim  | Trade-off                          |
|-------------------------------------|------|------------------------------------|
| `universal-sentence-encoder`        | 512  | Fast, good general quality (current) |
| `universal-sentence-encoder-large`  | 512  | Slower Transformer, higher accuracy  |
| `universal-sentence-encoder-multilingual` | 512 | Multi-language support          |

Change only `USE_MODEL_PATH` to point to the new model — no code changes required as long as
the SavedModel exposes the same `serving_default_inputs` / `StatefulPartitionedCall` signature.
If the signature differs, update `UseTextClassifier.encodeTexts()` accordingly.
