package app.purecipes.feature.analytics.domain.model

object CrashBreadcrumb {

	const val SEARCH_PERFORMED = "search_performed"
	const val RECIPE_SAVE_ATTEMPTED = "recipe_save_attempted"

	fun screen(screenName: String): String = "screen: $screenName"

	fun recipeOpened(recipeId: Int): String = "recipe_opened: $recipeId"

	fun cookingStarted(recipeId: Int): String = "cooking_started: $recipeId"

	fun cookingStepAdvanced(recipeId: Int, stepIndex: Int): String =
		"cooking_step_advanced: $recipeId:$stepIndex"

	fun signInAttempted(method: String): String = "sign_in_attempted: $method"
}
