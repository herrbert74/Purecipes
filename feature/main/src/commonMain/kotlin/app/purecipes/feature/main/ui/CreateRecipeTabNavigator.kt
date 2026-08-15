package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.library.ui.navigation.LibraryDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateEditorDestination

internal class CreateRecipeTabNavigator(
	private val stackFor: (MainTabStackId) -> NavBackStack<NavKey>,
	private val selectTab: (MainTabStackId) -> Unit,
	private val replaceTabRoot: (NavKey) -> Unit,
	private val push: (NavKey) -> Unit,
) {

	fun openNewRecipe() {
		val stack = stackFor(MainTabStackId.Create)
		stack.clear()
		stack += CreateDestination
		selectTab(MainTabStackId.Create)
	}

	fun openEditor(recipeId: Int) {
		push(CreateEditorDestination(recipeId = recipeId))
	}

	fun onRecipeSaveSuccess(message: String) {
		val createStack = stackFor(MainTabStackId.Create)
		createStack.clear()
		createStack += CreateDestination
		replaceTabRoot(
			LibraryDestination(
				openMyRecipes = true,
				recipeSaveMessage = message,
			),
		)
	}
}
