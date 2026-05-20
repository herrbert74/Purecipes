package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeNutrition(
	val recipeTotals: NutritionSummary,
	val perServing: NutritionSummary? = null,
	val per100Grams: NutritionSummary? = null,
	val servingCount: Double? = null,
	val servingDescription: String? = null,
	val totalWeightGrams: Double? = null,
	val ingredients: List<IngredientNutritionLine> = emptyList(),
) {
	fun hasDisplayableData(): Boolean =
		recipeTotals.hasMacroNutrients() ||
			ingredients.any(IngredientNutritionLine::hasDisplayableData)
}

fun NutritionSummary.hasMacroNutrients(): Boolean =
	listOf(calories, protein, carbohydrates, fat, fiber, sugar, sodium).any { value -> value != null }

fun IngredientNutritionLine.hasDisplayableData(): Boolean =
	contribution.hasMacroNutrients() || per100Grams?.hasMacroNutrients() == true || isMatched
