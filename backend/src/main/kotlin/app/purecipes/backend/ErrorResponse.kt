package app.purecipes.backend

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
	val message: String,
	val detail: String? = null,
)
