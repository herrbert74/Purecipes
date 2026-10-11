package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.RecipeSummary

internal const val CATALOG_IMAGE_SCORE = 8

internal const val CATALOG_INSTRUCTION_SCORE = 4

internal const val CATALOG_INGREDIENT_SCORE = 2

internal const val CATALOG_METADATA_SCORE = 1

internal enum class SearchRecipeOrder {
	CATALOG,
	TITLE,
	NEWEST,
}

internal data class SearchRecipeQuery(
	val whereClause: String,
	val params: List<Any>,
	val order: SearchRecipeOrder,
	val limit: Int? = null,
	val offset: Int? = null,
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

internal fun searchRecipeOrderBySql(order: SearchRecipeOrder): String {
	val newestTieBreak = "r.created_at DESC, r.id DESC"
	return when (order) {
		SearchRecipeOrder.CATALOG ->
			"ORDER BY r.catalog_rank ASC NULLS LAST, completeness_score DESC, $newestTieBreak"

		SearchRecipeOrder.TITLE ->
			"ORDER BY completeness_score DESC, $newestTieBreak"

		SearchRecipeOrder.NEWEST ->
			"ORDER BY $newestTieBreak"
	}
}
