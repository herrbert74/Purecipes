package com.purecipes.feature.search.model

data class RecipeSummary(
	val id: Int,
	val title: String,
	val cuisine: String?,
	val imageUrl: String?,
	val totalTime: Int?,
)
