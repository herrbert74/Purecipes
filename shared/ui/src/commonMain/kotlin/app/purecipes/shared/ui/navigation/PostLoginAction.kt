package app.purecipes.shared.ui.navigation

sealed interface PostLoginAction {
	data object OpenSearchFilters : PostLoginAction

	data class ImportCookbookShare(val token: String) : PostLoginAction
}
