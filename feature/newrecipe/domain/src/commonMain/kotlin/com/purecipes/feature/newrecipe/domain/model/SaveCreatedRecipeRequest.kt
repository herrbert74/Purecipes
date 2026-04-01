package com.purecipes.feature.newrecipe.domain.model

data class SaveCreatedRecipeRequest(
	val recipeId: Int? = null,
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredients: List<String> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: String? = null,
)
