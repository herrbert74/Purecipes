package app.purecipes.feature.library.domain.model

sealed interface CookbookMembershipEvent {

	val recipeId: Int
	val cookbookId: Int

	data class Added(
		override val recipeId: Int,
		override val cookbookId: Int,
	) : CookbookMembershipEvent

	data class Removed(
		override val recipeId: Int,
		override val cookbookId: Int,
	) : CookbookMembershipEvent
}
