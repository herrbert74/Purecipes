package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExcludedIngredientsDelta(
	val add: Set<String> = emptySet(),
	val remove: Set<String> = emptySet(),
)
