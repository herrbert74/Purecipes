package com.purecipes.feature.search.data.datasource

import com.github.michaelbull.result.Ok
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.RecipeSummary

class RecipeSearchRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeSearchDataSource.Remote {

	override suspend fun search(query: String): SearchOutcome<List<RecipeSummary>> {
		val trimmedQuery = query.trim()
		if (trimmedQuery.isBlank()) return Ok(emptyList())

		return runCatchingApi {
			api.search(trimmedQuery)
		}
	}
}
