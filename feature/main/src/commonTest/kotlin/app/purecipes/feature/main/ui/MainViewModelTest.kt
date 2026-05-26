package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainViewModelTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	@Test
	fun `should exit only on search root`() {
		val viewModel = MainViewModel()
		viewModel.shouldExit() shouldBe true
		viewModel.onRecipeSelected(42)
		viewModel.shouldExit() shouldBe false
	}

	@Test
	fun `requestLoginForPostLoginAction navigates to account and preserves origin until sign in`() {
		val viewModel = MainViewModel()
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe PostLoginNavOrigin.RECIPE_SEARCH_FILTERS
	}

	@Test
	fun `post login resume opens search tab and enables open filters`() {
		val viewModel = MainViewModel()
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS)

		viewModel.onOpenEmailSignIn(prefilledEmail = "taylor@example.com")
		viewModel.onAuthenticationSucceeded()

		viewModel.takePostLoginOriginAfterSignIn() shouldBe PostLoginNavOrigin.RECIPE_SEARCH_FILTERS
		viewModel.markPendingOpenSearchFiltersAfterLogin()

		viewModel.onTabSelected(mainTabs.first { it.destination == SearchDestination })
		viewModel.takePendingOpenSearchFilters() shouldBe true
		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination)
	}

	@Test
	fun `selecting search tab clears pending post login origin without consuming open filters flag`() {
		val viewModel = MainViewModel()

		viewModel.onTabSelected(mainTabs.first { it.destination == AccountDestination })
		viewModel.markPendingOpenSearchFiltersAfterLogin()
		viewModel.onTabSelected(mainTabs.first { it.destination == SearchDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe null
		viewModel.takePendingOpenSearchFilters() shouldBe true
	}

	@Test
	fun `tab selection resets stack to selected destination`() {
		val viewModel = MainViewModel()
		viewModel.onRecipeSelected(42)
		viewModel.onTabSelected(mainTabs.first { it.destination == FavoritesDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination)
	}

	@Test
	fun `authentication succeeded pops email auth destinations`() {
		val viewModel = MainViewModel()
		viewModel.onOpenEmailRegistration()
		viewModel.onOpenEmailSignIn(prefilledEmail = "taylor@example.com")
		viewModel.onAuthenticationSucceeded()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
	}

	@Test
	fun `authentication succeeded leaves account settings on stack`() {
		val viewModel = MainViewModel()
		viewModel.onOpenSettings()
		viewModel.onAuthenticationSucceeded()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination, AccountSettingsDestination)
	}

	@Test
	fun `deep link to recipe opens recipe details on search tab`() {
		val viewModel = MainViewModel()
		viewModel.onTabSelected(mainTabs.first { it.destination == FavoritesDestination })
		viewModel.onDeepLink(PurecipesLink.Recipe(99))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(99))
	}

	@Test
	fun `deep link to cookbook share switches to favorites and stores pending share token`() {
		val viewModel = MainViewModel()
		viewModel.onDeepLink(PurecipesLink.CookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `stage cookbook share import stores token without changing tabs`() {
		val viewModel = MainViewModel()
		viewModel.stageCookbookShareImport(sampleShareToken)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `back removes only the top destination`() {
		val viewModel = MainViewModel()
		viewModel.onRecipeSelected(42)
		viewModel.onStartCooking(42)
		viewModel.onBack()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(42))
	}
}
