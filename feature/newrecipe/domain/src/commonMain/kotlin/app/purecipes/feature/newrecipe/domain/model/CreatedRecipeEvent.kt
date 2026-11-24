package app.purecipes.feature.newrecipe.domain.model

sealed interface CreatedRecipeEvent {

	val recipeId: Int

	data class Saved(override val recipeId: Int) : CreatedRecipeEvent

	data class Deleted(override val recipeId: Int) : CreatedRecipeEvent
}
