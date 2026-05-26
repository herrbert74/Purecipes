package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainViewModelTest {

	private val viewModel = MainViewModel()

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	@Test
	fun `should exit only on search root`() {
		viewModel.shouldExit(listOf(SearchDestination)) shouldBe true
		viewModel.shouldExit(listOf(SearchDestination, RecipeDetailsDestination(42))) shouldBe false
	}

	@Test
	fun `requestLoginForPostLoginAction navigates to account and preserves origin until sign in`() {
		val backStack = mutableListOf<NavKey>(SearchDestination)

		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS, backStack)

		backStack shouldBe listOf<NavKey>(AccountDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe PostLoginNavOrigin.RECIPE_SEARCH_FILTERS
	}

	@Test
	fun `selecting search tab clears pending post login origin without consuming open filters flag`() {
		val backStack = mutableListOf<NavKey>(AccountDestination)
		viewModel.markPendingOpenSearchFiltersAfterLogin()
		viewModel.onTabSelected(
			backStack,
			mainTabs.first { it.destination == SearchDestination },
		)

		backStack shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe null
		viewModel.takePendingOpenSearchFilters() shouldBe true
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
	fun `authentication succeeded pops email auth destinations`() {
		val backStack = mutableListOf<NavKey>(
			AccountDestination,
			EmailRegistrationDestination,
			EmailSignInDestination(prefilledEmail = "taylor@example.com"),
		)

		viewModel.onAuthenticationSucceeded(backStack)

		backStack shouldBe listOf<NavKey>(AccountDestination)
	}

	@Test
	fun `authentication succeeded leaves account settings on stack`() {
		val backStack = mutableListOf<NavKey>(
			AccountDestination,
			AccountSettingsDestination,
		)

		viewModel.onAuthenticationSucceeded(backStack)

		backStack shouldBe listOf<NavKey>(AccountDestination, AccountSettingsDestination)
	}

	@Test
	fun `deep link to recipe opens recipe details on search tab`() {
		val backStack = mutableListOf<NavKey>(FavoritesDestination)

		viewModel.onDeepLink(backStack, PurecipesLink.Recipe(99))

		backStack shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(99))
	}

	@Test
	fun `deep link to cookbook share switches to favorites and stores pending share token`() {
		val backStack = mutableListOf<NavKey>(SearchDestination)

		viewModel.onDeepLink(backStack, PurecipesLink.CookbookShare(sampleShareToken))

		backStack shouldBe listOf<NavKey>(FavoritesDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `stage cookbook share import stores token without changing tabs`() {
		val backStack = mutableListOf<NavKey>(SearchDestination)

		viewModel.stageCookbookShareImport(sampleShareToken)

		backStack shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
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
