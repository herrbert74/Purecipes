package app.purecipes.shared.ui.navigation

sealed interface PostLoginNavigationTarget {
	data object OpenSearchWithFilters : PostLoginNavigationTarget

	data object OpenCreate : PostLoginNavigationTarget

	data object OpenFavoritesMyRecipes : PostLoginNavigationTarget

	data object OpenFeatureRequests : PostLoginNavigationTarget

	data class OpenFavoritesWithCookbookShare(val token: String) : PostLoginNavigationTarget
}

fun resolvePostLoginNavigationTarget(action: PostLoginAction): PostLoginNavigationTarget =
	when (action) {
		PostLoginAction.OpenSearchFilters -> PostLoginNavigationTarget.OpenSearchWithFilters
		PostLoginAction.OpenCreate -> PostLoginNavigationTarget.OpenCreate
		PostLoginAction.OpenFavoritesMyRecipes -> PostLoginNavigationTarget.OpenFavoritesMyRecipes
		PostLoginAction.OpenFeatureRequests -> PostLoginNavigationTarget.OpenFeatureRequests
		is PostLoginAction.ImportCookbookShare ->
			PostLoginNavigationTarget.OpenFavoritesWithCookbookShare(action.token)
	}
