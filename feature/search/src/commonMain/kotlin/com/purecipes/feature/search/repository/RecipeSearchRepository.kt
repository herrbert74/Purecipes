package com.purecipes.feature.search.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.feature.search.model.RecipeSummary
import com.purecipes.feature.search.network.RecipeSearchApi
import kotlinx.io.IOException

class RecipeSearchRepository(
	private val api: RecipeSearchApi,
) {
	suspend fun search(query: String): SearchOutcome<List<RecipeSummary>> {
		val trimmedQuery = query.trim()
		if (trimmedQuery.isBlank()) return Ok(emptyList())

		return try {
			val results = api.search(trimmedQuery).map {
				RecipeSummary(
					id = it.id,
					title = it.title,
					cuisine = it.cuisine,
					imageUrl = it.imageUrl,
					totalTime = it.totalTime,
				)
			}
			Ok(results)
		} catch (error: IOException) {
			Err(SearchFailure.IoFailure)
		} catch (error: Throwable) {
			Err(SearchFailure.ServerError(error.message ?: "Search failed"))
		}
	}
}
