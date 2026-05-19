package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.repository.RecipeNutritionEstimateRepository
import app.purecipes.shared.domain.model.NutritionSummary
import com.github.michaelbull.result.Ok

class FakeRecipeNutritionEstimateRepository(
	private val estimateResult: Outcome<NutritionSummary?> = Ok(null),
) : RecipeNutritionEstimateRepository {

	var lastIngredients: List<String> = emptyList()
		private set

	override suspend fun estimateRecipeNutrition(ingredients: List<String>): Outcome<NutritionSummary?> {
		lastIngredients = ingredients
		return estimateResult
	}
}
