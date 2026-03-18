package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientGroup(
	val name: String? = null,
	val ingredients: List<String> = emptyList(),
)

@Serializable
data class RecipeDetails(
	val id: Int,
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredientGroups: List<IngredientGroup> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: String? = null,
)
