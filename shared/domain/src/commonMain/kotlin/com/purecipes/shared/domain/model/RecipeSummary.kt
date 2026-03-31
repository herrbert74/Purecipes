package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSummary(
	val id: Int,
	val title: String,
	val cuisine: String?,
	val imageUrl: String?,
	val totalTime: Int?,
	val isFavorite: Boolean = false,
)
