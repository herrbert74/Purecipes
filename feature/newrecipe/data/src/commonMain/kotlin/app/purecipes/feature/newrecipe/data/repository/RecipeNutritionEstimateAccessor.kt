package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.data.datasource.RecipeNutritionEstimateDataSource
import app.purecipes.feature.newrecipe.domain.repository.RecipeNutritionEstimateRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeNutritionEstimateAccessor(
	private val remoteDataSource: RecipeNutritionEstimateDataSource.Remote,
) : RecipeNutritionEstimateRepository {

	override suspend fun estimateRecipeNutrition(ingredients: List<String>) =
		remoteDataSource.estimateRecipeNutrition(ingredients)
}
