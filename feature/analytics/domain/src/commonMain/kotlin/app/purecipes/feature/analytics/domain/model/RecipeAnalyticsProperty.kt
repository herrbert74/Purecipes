package app.purecipes.feature.analytics.domain.model

object RecipeAnalyticsProperty {

	const val RECIPE_ID = "recipe_id"
	const val RECIPE_NAME = "recipe_name"
	const val IS_PRIVATE = "is_private"

	fun identity(
		recipeId: Int,
		recipeName: String?,
		isPrivate: Boolean? = null,
	): Map<String, AnalyticsValue> = buildMap {
		put(RECIPE_ID, recipeId.asAnalyticsValue())
		if (!recipeName.isNullOrBlank()) {
			put(RECIPE_NAME, AnalyticsValue.TextValue(recipeName))
		}
		if (isPrivate != null) {
			put(IS_PRIVATE, isPrivate.asAnalyticsValue())
		}
	}
}
