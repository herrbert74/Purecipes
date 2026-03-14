package com.purecipes.feature.search.data.repository

import com.github.michaelbull.result.Ok
import com.purecipes.feature.search.domain.model.RecipeSummary
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi

class RecipeSearchAccessor(private val api: PurecipesApi) : RecipeSearchRepository {

	override suspend fun search(query: String): SearchOutcome<List<RecipeSummary>> {
		val trimmedQuery = query.trim()
		if (trimmedQuery.isBlank()) return Ok(emptyList())

		return runCatchingApi {
			api.search(trimmedQuery).map {
				RecipeSummary(
					id = it.id,
					title = it.title,
					cuisine = it.cuisine,
					imageUrl = it.imageUrl,
					totalTime = it.totalTime,
				)
			}
		}
	}
}
