package app.purecipes.feature.newrecipe.ui

internal enum class CreateRecipeSection(
	val label: String,
	val testTag: String,
) {

	About(
		label = "About",
		testTag = "createRecipeSectionAbout",
	),
	Ingredients(
		label = "Ingredients",
		testTag = "createRecipeSectionIngredients",
	),
	Steps(
		label = "Steps",
		testTag = "createRecipeSectionSteps",
	),
}

internal const val CREATE_RECIPE_DESCRIPTION_REQUIRED_MESSAGE = "Add a recipe description."
internal const val CREATE_RECIPE_INGREDIENT_NAME_REQUIRED_MESSAGE = "Add an ingredient name."
internal const val CREATE_RECIPE_STEP_REQUIRED_MESSAGE = "Add at least one cooking step."
internal const val CREATE_RECIPE_TITLE_REQUIRED_MESSAGE = "Add a recipe title."
internal const val CREATE_RECIPE_TOTAL_TIME_WHOLE_NUMBER_MESSAGE = "Total time must be a whole number."
