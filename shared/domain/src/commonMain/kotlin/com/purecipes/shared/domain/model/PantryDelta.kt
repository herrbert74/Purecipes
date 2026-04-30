package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PantryDelta(
	val add: Set<String> = emptySet(),
	val remove: Set<String> = emptySet(),
)
