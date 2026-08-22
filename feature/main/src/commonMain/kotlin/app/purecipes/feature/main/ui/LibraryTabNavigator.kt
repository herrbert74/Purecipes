package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.library.ui.navigation.CookbookDetailDestination
import app.purecipes.feature.library.ui.navigation.LibraryCookbooksDestination
import app.purecipes.shared.domain.model.CookbookSummary

internal class LibraryTabNavigator(
	private val stackFor: (MainTabStackId) -> NavBackStack<NavKey>,
) {

	fun openCookbookDetail(cookbookId: Int, name: String) {
		val stack = stackFor(MainTabStackId.Library)
		if (stack.none { it is LibraryCookbooksDestination }) {
			stack += LibraryCookbooksDestination
		}
		while (stack.lastOrNull() is CookbookDetailDestination) {
			stack.removeAt(stack.lastIndex)
		}
		stack += CookbookDetailDestination(
			cookbookId = cookbookId,
			name = name,
		)
	}

	fun openCookbook(cookbook: CookbookSummary) {
		openCookbookDetail(
			cookbookId = cookbook.id,
			name = cookbook.name,
		)
	}
}
