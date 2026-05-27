package app.purecipes.feature.favorites.domain.model

sealed interface FavoriteEvent {

	val recipeId: Int

	data class Added(override val recipeId: Int) : FavoriteEvent

	data class Removed(override val recipeId: Int) : FavoriteEvent
}
