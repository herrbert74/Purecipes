package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookSummary(
	val id: Int,
	val name: String,
	val recipeCount: Int,
	val updatedAtEpochMillis: Long,
)
