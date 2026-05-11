package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.SearchFilters

interface RecipeSearchFilterRepository {

	suspend fun getFilters(): SearchFilters

	suspend fun saveFilters(filters: SearchFilters)
}
