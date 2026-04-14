package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
	val query: String,
	val filters: SearchFilters = SearchFilters(),
	val limit: Int = 25,
)
