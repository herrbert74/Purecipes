package app.purecipes.feature.analytics.domain.model

object RecipeAnalyticsProperty {

	const val RECIPE_ID = "recipe_id"
	const val RECIPE_NAME = "recipe_name"

	fun identity(
		recipeId: Int,
		recipeName: String?,
	): Map<String, AnalyticsValue> = buildMap {
		put(RECIPE_ID, recipeId.asAnalyticsValue())
		if (!recipeName.isNullOrBlank()) {
			put(RECIPE_NAME, AnalyticsValue.TextValue(recipeName))
		}
	}
}
