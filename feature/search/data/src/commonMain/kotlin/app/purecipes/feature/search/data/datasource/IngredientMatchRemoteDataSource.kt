package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.IngredientMatchResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class IngredientMatchRemoteDataSource(
	private val api: PurecipesApi,
) : IngredientMatchDataSource.Remote {

	override suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse> =
		runCatchingApi {
			api.matchIngredient(name)
		}
}
