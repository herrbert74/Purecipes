package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.RecipeSummary

internal const val CATALOG_IMAGE_SCORE = 8

internal const val CATALOG_INSTRUCTION_SCORE = 4

internal const val CATALOG_INGREDIENT_SCORE = 2

internal const val CATALOG_METADATA_SCORE = 1

private const val RANDOM_HASH_MODULO = 104729L
private const val RANDOM_HASH_BASE_MULTIPLIER = 2654435761L
private const val RANDOM_MULTIPLIER_SPAN = 1_000_000L
private const val RANDOM_SEED_LCG_MULTIPLIER = 6364136223846793005L
private const val RANDOM_SEED_LCG_INCREMENT = 1442695040888963407L

internal enum class SearchRecipeOrder {
	CATALOG,
	CATALOG_RANDOM,
	TITLE,
	TITLE_RANDOM,
	NEWEST,
}

internal data class SearchRecipeQuery(
	val whereClause: String,
	val params: List<Any>,
	val order: SearchRecipeOrder,
	val limit: Int? = null,
	val offset: Int? = null,
	val randomSeed: Long? = null,
)

internal data class SearchRecipeCandidate(
	val summary: RecipeSummary,
	val completenessScore: Int,
	val createdAtMillis: Long,
)

internal fun recipeCompletenessScoreSql(recipeAlias: String): String {
	return """
		(
			CASE WHEN NULLIF(BTRIM($recipeAlias.image_url), '') IS NOT NULL
				THEN $CATALOG_IMAGE_SCORE ELSE 0 END
			+ CASE WHEN EXISTS (
				SELECT 1
				FROM instruction_steps catalog_steps
				WHERE catalog_steps.recipe_id = $recipeAlias.id
			) THEN $CATALOG_INSTRUCTION_SCORE ELSE 0 END
			+ CASE WHEN EXISTS (
				SELECT 1
				FROM ingredient_groups catalog_groups
				INNER JOIN ingredients catalog_ingredients
					ON catalog_ingredients.ingredient_group_id = catalog_groups.id
				WHERE catalog_groups.recipe_id = $recipeAlias.id
			) THEN $CATALOG_INGREDIENT_SCORE ELSE 0 END
			+ CASE WHEN
				NULLIF(BTRIM($recipeAlias.description), '') IS NOT NULL
				AND NULLIF(BTRIM($recipeAlias.cuisine), '') IS NOT NULL
				AND NULLIF(BTRIM($recipeAlias.meal_type), '') IS NOT NULL
				AND $recipeAlias.total_time IS NOT NULL
				THEN $CATALOG_METADATA_SCORE ELSE 0 END
		)
	""".trimIndent()
}

internal fun searchRecipeOrderBySql(order: SearchRecipeOrder, randomSeed: Long? = null): String {
	val newestTieBreak = "r.created_at DESC, r.id DESC"
	val randomTieBreak = randomOrderExpression(randomSeed)
	return when (order) {
		SearchRecipeOrder.CATALOG ->
			"ORDER BY r.catalog_rank ASC NULLS LAST, completeness_score DESC, $newestTieBreak"

		SearchRecipeOrder.CATALOG_RANDOM ->
			"ORDER BY r.catalog_rank ASC NULLS LAST, completeness_score DESC, $randomTieBreak"

		SearchRecipeOrder.TITLE ->
			"ORDER BY completeness_score DESC, $newestTieBreak"

		SearchRecipeOrder.TITLE_RANDOM ->
			"ORDER BY completeness_score DESC, $randomTieBreak"

		SearchRecipeOrder.NEWEST ->
			"ORDER BY $newestTieBreak"
	}
}

private fun randomOrderExpression(randomSeed: Long?): String {
	val seed = randomSeed ?: 0L
	val mixed = (seed * RANDOM_SEED_LCG_MULTIPLIER + RANDOM_SEED_LCG_INCREMENT) ushr 1
	val multiplier = RANDOM_HASH_BASE_MULTIPLIER + (mixed % RANDOM_MULTIPLIER_SPAN) * 2L
	val offset = mixed % RANDOM_HASH_MODULO
	return "MOD(ABS(CAST(r.id AS BIGINT) * $multiplier + $offset), $RANDOM_HASH_MODULO), r.id DESC"
}
