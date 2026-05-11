package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookCreateRequest(
	val name: String,
)
