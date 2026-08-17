package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.cooking.ui.navigation.RecipeCookingDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination

internal class CookingFlowNavigator(
	private val activeStack: () -> NavBackStack<NavKey>,
	private val stackFor: (MainTabStackId) -> NavBackStack<NavKey>,
	private val selectTab: (MainTabStackId) -> Unit,
) {

	fun findMoreRecipes() {
		val currentStack = activeStack()
		while (
			currentStack.lastOrNull() is RecipeCookingDestination ||
			currentStack.lastOrNull() is RecipeDetailsDestination
		) {
			currentStack.removeAt(currentStack.lastIndex)
		}
		selectTab(MainTabStackId.Search)
		val searchStack = stackFor(MainTabStackId.Search)
		while (searchStack.size > 1) {
			searchStack.removeAt(searchStack.lastIndex)
		}
	}
}
