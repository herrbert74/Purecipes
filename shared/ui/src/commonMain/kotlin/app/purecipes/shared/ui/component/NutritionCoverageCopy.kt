package app.purecipes.shared.ui.component

import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionSummary

const val USDA_NUTRITION_ATTRIBUTION =
	"Nutrition estimates for some ingredients use data from USDA FoodData Central."

internal fun NutritionSummary.coverageCopy(): String? {
	val matched = matchedIngredientCount
	val totalCount = totalIngredientCount
	if (matched == null || totalCount == null || totalCount <= 0) {
		return if (calculationSource == NutritionCalculationSource.SCRAPED) {
			"Imported nutrition values"
		} else {
			null
		}
	}

	return when {
		matched == 0 -> "No ingredients matched yet"
		isComplete -> "Complete estimate from all $totalCount ingredients"
		else -> "Partial estimate from $matched of $totalCount ingredients"
	}
}

internal fun NutritionSummary.shouldShowUsdaAttribution(): Boolean =
	calculationSource != NutritionCalculationSource.SCRAPED
