package com.purecipes.feature.search.domain.repository

import com.purecipes.shared.domain.model.SearchFilters

interface RecipeSearchFilterRepository {

	suspend fun getFilters(): SearchFilters

	suspend fun saveFilters(filters: SearchFilters)
}
