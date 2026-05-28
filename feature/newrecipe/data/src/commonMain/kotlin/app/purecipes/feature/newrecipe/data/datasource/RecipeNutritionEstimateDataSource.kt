package app.purecipes.feature.newrecipe.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeNutritionEstimateRequest
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

interface RecipeNutritionEstimateDataSource {

	interface Remote {

		suspend fun estimateRecipeNutrition(ingredients: List<String>): Outcome<NutritionSummary?>
	}
}

@Inject
@ContributesBinding(AppScope::class)
class RecipeNutritionEstimateRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeNutritionEstimateDataSource.Remote {

	override suspend fun estimateRecipeNutrition(ingredients: List<String>): Outcome<NutritionSummary?> =
		runCatchingApi {
			api.estimateRecipeNutrition(RecipeNutritionEstimateRequest(ingredients = ingredients)).nutrition
		}
}
