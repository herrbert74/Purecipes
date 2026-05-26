package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookShareToken(
	val token: String,
)
