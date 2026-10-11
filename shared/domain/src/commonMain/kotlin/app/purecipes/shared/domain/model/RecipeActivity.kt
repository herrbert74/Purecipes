package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RecipeActivityEvent {

	OPEN,
	COOK_COMPLETE,
}

@Serializable
data class RecipeActivityRequest(
	val event: RecipeActivityEvent,
)
