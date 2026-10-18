package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.SearchFilters
import kotlinx.coroutines.flow.Flow

interface RecipeSearchFilterRepository {

	suspend fun getFilters(): SearchFilters

	suspend fun saveFilters(filters: SearchFilters)

	fun observeFilterChanges(): Flow<Unit>
}
