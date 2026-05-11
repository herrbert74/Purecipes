package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookRef(
	val id: Int,
	val name: String,
)
