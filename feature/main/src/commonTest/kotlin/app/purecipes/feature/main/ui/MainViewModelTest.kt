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
	fun `selecting search tab clears pending post login origin and resets open filters destination`() {
		val viewModel = mainViewModelForTest()

		viewModel.onTabSelected(mainTabs.first { it.destination is AccountDestination })
		viewModel.onTabSelected(mainTabs.first { it.destination is SearchDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination())
		viewModel.takePostLoginOriginAfterSignIn() shouldBe null
	}

	@Test
	fun `tab selection resets stack to selected destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onTabSelected(mainTabs.first { it.destination is FavoritesDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination())
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
		viewModel.onTabSelected(mainTabs.first { it.destination is FavoritesDestination })
		viewModel.onDeepLink(PurecipesLink.Recipe(99))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination(), RecipeDetailsDestination(99))
	}

	@Test
	fun `deep link to cookbook share switches to favorites with share token on destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onDeepLink(PurecipesLink.CookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(
			FavoritesDestination(cookbookShareToken = sampleShareToken),
		)
	}

	@Test
	fun `stage cookbook share import stores token until favorites tab is selected`() {
		val viewModel = mainViewModelForTest()
		viewModel.stageCookbookShareImport(sampleShareToken)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination())
		viewModel.onTabSelected(mainTabs.first { it.destination is FavoritesDestination })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(
			FavoritesDestination(cookbookShareToken = sampleShareToken),
		)
	}

	@Test
	fun `back removes only the top destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onStartCooking(42)
		viewModel.onBack()

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination(), RecipeDetailsDestination(42))
	}
}
