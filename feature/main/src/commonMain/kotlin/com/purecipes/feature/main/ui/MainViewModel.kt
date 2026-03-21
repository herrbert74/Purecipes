package com.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavKey

internal class MainViewModel : ViewModel() {

	fun shouldExit(currentDestination: NavKey?): Boolean = currentDestination == SearchDestination

	fun onTabSelected(
		backStack: MutableList<NavKey>,
		currentDestination: NavKey?,
		tab: MainTab,
	) {
		if (!tab.isSelected(currentDestination) || backStack.lastOrNull() != tab.destination) {
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
