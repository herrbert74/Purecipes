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
	;

	companion object {

		fun forValidationMessage(message: String): CreateRecipeSection {
			return when (message) {
				CREATE_RECIPE_INGREDIENT_NAME_REQUIRED_MESSAGE -> Ingredients
				CREATE_RECIPE_STEP_REQUIRED_MESSAGE -> Steps
				else -> About
			}
		}
	}
}

internal const val CREATE_RECIPE_INGREDIENT_NAME_REQUIRED_MESSAGE = "Add an ingredient name."
internal const val CREATE_RECIPE_STEP_REQUIRED_MESSAGE = "Add at least one cooking step."
