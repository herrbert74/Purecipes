package com.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavKey

internal class MainViewModel : ViewModel() {

	fun shouldExit(backStack: List<NavKey>): Boolean =
		backStack.size == 1 && backStack.firstOrNull() == SearchDestination

	fun onTabSelected(backStack: MutableList<NavKey>, tab: MainTab) {
		if (backStack.size != 1 || backStack.firstOrNull() != tab.destination) {
			backStack.clear()
			backStack += tab.destination
		}
	}

	fun onRecipeSelected(backStack: MutableList<NavKey>, recipeId: Int) {
		backStack += RecipeDetailsDestination(recipeId)
	}

	fun onStartCooking(backStack: MutableList<NavKey>, recipeId: Int) {
		backStack += RecipeCookingDestination(recipeId)
	}

	fun onOpenSettings(backStack: MutableList<NavKey>) {
		if (backStack.firstOrNull() != AccountDestination) {
			backStack.clear()
			backStack += AccountDestination
		}
		if (backStack.lastOrNull() != AccountSettingsDestination) {
			backStack += AccountSettingsDestination
		}
	}

	fun onBack(backStack: MutableList<NavKey>) {
		if (backStack.size > 1) {
			backStack.removeAt(backStack.lastIndex)
		}
	}
}

@Composable
internal fun mainViewModel(): MainViewModel = viewModel(
	factory = viewModelFactory {
		initializer { MainViewModel() }
	},
)
