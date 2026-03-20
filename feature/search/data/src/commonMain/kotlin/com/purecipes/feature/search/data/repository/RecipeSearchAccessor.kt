package com.purecipes.feature.search.data.repository

import com.purecipes.feature.search.data.datasource.RecipeSearchDataSource
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.RecipeSummary

class RecipeSearchAccessor(
	private val remoteDataSource: RecipeSearchDataSource.Remote,
) : RecipeSearchRepository {

	override suspend fun search(query: String): SearchOutcome<List<RecipeSummary>> = remoteDataSource.search(query)
}
