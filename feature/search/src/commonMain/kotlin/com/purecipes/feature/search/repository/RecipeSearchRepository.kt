package com.purecipes.feature.search.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.feature.search.model.RecipeSummary
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.Inject
import kotlinx.io.IOException

@Inject
class RecipeSearchRepository(private val api: PurecipesApi) {

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
		} catch (_: IOException) {
			Err(SearchFailure.IoFailure)
		} catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
			Err(SearchFailure.ServerError(error.message ?: "Search failed"))
		}
	}
}
