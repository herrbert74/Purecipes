package com.purecipes.feature.search.network

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchDto(
	val id: Int,
	val title: String,
	val cuisine: String? = null,
	val imageUrl: String? = null,
	val totalTime: Int? = null,
)
