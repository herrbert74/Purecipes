package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSummary(
	val id: Int,
	val title: String,
	val cuisine: Cuisine?,
	val imageUrl: String?,
	val totalTime: Int?,
	val measurementSystem: MeasurementSystem? = null,
	val isFavorite: Boolean = false,
	val isPrivate: Boolean = false,
)
