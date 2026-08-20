package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Immutable

@Immutable
data class CreateRecipeFieldErrors(
	val title: String? = null,
	val description: String? = null,
	val totalTime: String? = null,
	val unnamedIngredientIndexes: List<Int> = emptyList(),
	val steps: String? = null,
) {

	val hasErrors: Boolean
		get() = title != null ||
			description != null ||
			totalTime != null ||
			unnamedIngredientIndexes.isNotEmpty() ||
			steps != null

	internal val firstSection: CreateRecipeSection
		get() = when {
			title != null || description != null || totalTime != null -> CreateRecipeSection.About
			unnamedIngredientIndexes.isNotEmpty() -> CreateRecipeSection.Ingredients
			steps != null -> CreateRecipeSection.Steps
			else -> CreateRecipeSection.About
		}

	fun ingredientNameError(index: Int): String? =
		CREATE_RECIPE_INGREDIENT_NAME_REQUIRED_MESSAGE.takeIf { index in unnamedIngredientIndexes }
}
