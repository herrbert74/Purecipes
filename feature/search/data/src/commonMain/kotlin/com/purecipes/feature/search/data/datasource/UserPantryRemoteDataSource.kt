package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.PantryDelta

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
