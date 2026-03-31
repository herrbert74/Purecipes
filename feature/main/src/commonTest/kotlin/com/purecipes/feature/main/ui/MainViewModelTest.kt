package com.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainViewModelTest {

	private val viewModel = MainViewModel()

	@Test
	fun `should exit only on search root`() {
		assertTrue(viewModel.shouldExit(listOf(SearchDestination)))
		assertFalse(viewModel.shouldExit(listOf(SearchDestination, RecipeDetailsDestination(42))))
	}

	@Test
	fun `tab selection resets stack to selected destination`() {
		val backStack: MutableList<NavKey> = mutableListOf(SearchDestination, RecipeDetailsDestination(42))

		viewModel.onTabSelected(
			backStack = backStack,
			tab = mainTabs.first { it.destination == FavoritesDestination },
		)

		assertEquals(listOf<NavKey>(FavoritesDestination), backStack)
	}

	@Test
	fun `back removes only the top destination`() {
		val backStack: MutableList<NavKey> = mutableListOf(
			SearchDestination,
			RecipeDetailsDestination(42),
			RecipeCookingDestination(42),
		)

		viewModel.onBack(backStack)

		assertEquals(listOf<NavKey>(SearchDestination, RecipeDetailsDestination(42)), backStack)
	}
}
