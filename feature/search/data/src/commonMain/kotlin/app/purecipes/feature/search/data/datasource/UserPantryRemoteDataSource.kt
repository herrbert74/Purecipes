package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.PantryDelta

class UserPantryRemoteDataSource(
	private val api: PurecipesApi,
) : UserPantryDataSource.Remote {

	override suspend fun getPantry(): SearchOutcome<Set<String>> = runCatchingApi {
		api.getUserPantry()
	}

	override suspend fun updatePantry(delta: PantryDelta): SearchOutcome<Set<String>> = runCatchingApi {
		api.updateUserPantry(delta)
	}
}
