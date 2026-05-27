package app.purecipes.shared.ui.navigation

sealed interface PostLoginNavigationTarget {
	data object OpenSearchWithFilters : PostLoginNavigationTarget

	data class OpenFavoritesWithCookbookShare(val token: String) : PostLoginNavigationTarget
}

fun resolvePostLoginNavigationTarget(action: PostLoginAction): PostLoginNavigationTarget =
	when (action) {
		PostLoginAction.OpenSearchFilters -> PostLoginNavigationTarget.OpenSearchWithFilters
		is PostLoginAction.ImportCookbookShare ->
			PostLoginNavigationTarget.OpenFavoritesWithCookbookShare(action.token)
	}
