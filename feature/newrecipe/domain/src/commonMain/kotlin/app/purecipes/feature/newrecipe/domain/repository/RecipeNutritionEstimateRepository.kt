package app.purecipes.feature.newrecipe.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.NutritionSummary

interface RecipeNutritionEstimateRepository {

	suspend fun estimateRecipeNutrition(ingredients: List<String>): Outcome<NutritionSummary?>
}
