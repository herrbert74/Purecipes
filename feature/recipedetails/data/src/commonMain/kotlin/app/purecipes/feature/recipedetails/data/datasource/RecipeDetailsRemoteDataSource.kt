package app.purecipes.feature.recipedetails.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.RecipeDetails
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeDetailsRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeDetailsDataSource.Remote {

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = runCatchingApi {
		api.getRecipeDetails(recipeId)
	}
}
