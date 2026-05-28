package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.PantryDelta
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
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
