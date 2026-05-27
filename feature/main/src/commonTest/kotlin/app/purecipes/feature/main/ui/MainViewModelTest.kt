package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.settings.ui.navigation.AccountSettingsDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainViewModelTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	@Test
	fun `should exit only on search root`() {
		val viewModel = mainViewModelForTest()
		viewModel.shouldExit() shouldBe true
		viewModel.onRecipeSelected(42)
		viewModel.shouldExit() shouldBe false
	}

	@Test
	fun `requestLoginForPostLoginAction navigates to account and preserves origin until sign in`() {
		val viewModel = mainViewModelForTest()
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe PostLoginNavOrigin.RECIPE_SEARCH_FILTERS
	}

	@Test
	fun `post login resume opens search tab and enables open filters`() {
		val viewModel = mainViewModelForTest()
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
		val viewModel = mainViewModelForTest()

		viewModel.onTabSelected(mainTabs.first { it.destination == AccountDestination })
		viewModel.markPendingOpenSearchFiltersAfterLogin()
		viewModel.onTabSelected(mainTabs.first { it.destination == SearchDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe null
		viewModel.takePendingOpenSearchFilters() shouldBe true
	}

	@Test
	fun `tab selection resets stack to selected destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onTabSelected(mainTabs.first { it.destination == FavoritesDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination)
	}

	@Test
	fun `authentication succeeded pops email auth destinations`() {
		val viewModel = mainViewModelForTest()
		viewModel.onOpenEmailRegistration()
		viewModel.onOpenEmailSignIn(prefilledEmail = "taylor@example.com")
		viewModel.onAuthenticationSucceeded()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
	}

	@Test
	fun `authentication succeeded leaves account settings on stack`() {
		val viewModel = mainViewModelForTest()
		viewModel.onOpenSettings()
		viewModel.onAuthenticationSucceeded()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination, AccountSettingsDestination)
	}

	@Test
	fun `deep link to recipe opens recipe details on search tab`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.destination == FavoritesDestination })
		viewModel.onDeepLink(PurecipesLink.Recipe(99))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(99))
	}

	@Test
	fun `deep link to cookbook share switches to favorites and stores pending share token`() {
		val viewModel = mainViewModelForTest()
		viewModel.onDeepLink(PurecipesLink.CookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `stage cookbook share import stores token without changing tabs`() {
		val viewModel = mainViewModelForTest()
		viewModel.stageCookbookShareImport(sampleShareToken)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `back removes only the top destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onStartCooking(42)
		viewModel.onBack()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination, RecipeDetailsDestination(42))
	}
}
