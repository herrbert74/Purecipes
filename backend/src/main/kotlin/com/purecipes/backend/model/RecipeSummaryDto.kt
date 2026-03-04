package com.purecipes.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSummaryDto(
	val id: Int,
	val title: String,
	val cuisine: String? = null,
	val imageUrl: String? = null,
	val totalTime: Int? = null,
)
