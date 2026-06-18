package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class IngredientRequirement {
	REQUIRED,
	OPTIONAL,
}
