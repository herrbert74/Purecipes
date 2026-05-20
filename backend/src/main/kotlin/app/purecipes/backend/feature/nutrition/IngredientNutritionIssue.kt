package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary

internal enum class IngredientNutritionIssueKind {
	NOT_MEASURABLE,
	NO_FOOD_MATCH,
	UNRESOLVED_GRAMS,
}

internal data class IngredientNutritionIssue(
	val ingredientId: Int,
	val rawText: String,
	val parsedName: String,
	val kind: IngredientNutritionIssueKind,
)

internal fun RecipeNutritionCalculationResult.collectIngredientIssues(): List<IngredientNutritionIssue> =
	ingredientResults.mapNotNull { result ->
		if (IngredientVocabulary.isIgnorableIngredientLine(result.parsed.rawText)) {
			return@mapNotNull null
		}

		val parsed = result.parsed
		when {
			!parsed.isMeasurable -> IngredientNutritionIssue(
				ingredientId = result.ingredientId,
				rawText = parsed.rawText,
				parsedName = parsed.parsedName,
				kind = IngredientNutritionIssueKind.NOT_MEASURABLE,
			)
			result.foodMatch == null -> IngredientNutritionIssue(
				ingredientId = result.ingredientId,
				rawText = parsed.rawText,
				parsedName = parsed.parsedName,
				kind = IngredientNutritionIssueKind.NO_FOOD_MATCH,
			)
			result.grams == null -> IngredientNutritionIssue(
				ingredientId = result.ingredientId,
				rawText = parsed.rawText,
				parsedName = parsed.parsedName,
				kind = IngredientNutritionIssueKind.UNRESOLVED_GRAMS,
			)
			else -> null
		}
	}
