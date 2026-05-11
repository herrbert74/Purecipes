package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainViewModelTest {

	private val viewModel = MainViewModel()

	@Test
	fun `should exit only on search root`() {
		viewModel.shouldExit(listOf(SearchDestination)) shouldBe true
		viewModel.shouldExit(listOf(SearchDestination, RecipeDetailsDestination(42))) shouldBe false
	}

	@Test
	fun `tab selection resets stack to selected destination`() {
		val backStack: MutableList<NavKey> = mutableListOf(SearchDestination, RecipeDetailsDestination(42))

		viewModel.onTabSelected(
			backStack = backStack,
			tab = mainTabs.first { it.destination == FavoritesDestination },
		)

		backStack shouldBe listOf<NavKey>(FavoritesDestination)
	}

	@Test
	fun `back removes only the top destination`() {
		val backStack: MutableList<NavKey> = mutableListOf(
			SearchDestination,
			RecipeDetailsDestination(42),
			RecipeCookingDestination(42),
		)

		viewModel.onBack(backStack)

		backStack shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(42))
	}
}
