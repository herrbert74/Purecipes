package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.repository.RecipeNutritionEstimateRepository
import app.purecipes.shared.domain.model.NutritionSummary
import dev.zacsweers.metro.Inject

@Inject
class EstimateRecipeNutritionUseCase(
	private val repository: RecipeNutritionEstimateRepository,
) {

	suspend operator fun invoke(ingredients: List<String>): Outcome<NutritionSummary?> {
		return repository.estimateRecipeNutrition(ingredients)
	}
}
