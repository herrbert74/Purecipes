package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeImageUploadResponse(
	val imageUrl: String,
)
