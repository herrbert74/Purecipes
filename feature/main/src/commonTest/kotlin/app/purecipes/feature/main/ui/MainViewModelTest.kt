package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

private fun mainViewModelForTest(): MainViewModel = MainViewModel(
	observeAuthenticationState = ObserveAuthenticationStateUseCase(
		FakeAuthenticationRepository(AuthenticationState.SignedOut),
	),
	refreshConsent = RefreshConsentUseCase(FakeConsentRepository(ConsentState.NOT_REQUIRED)),
	setAnalyticsUserId = SetAnalyticsUserIdUseCase(FakeAnalyticsRepository()),
	observeIncomingLinks = ObserveIncomingLinksUseCase(
		object : IncomingLinkRepository {
			override fun observeLinks() = emptyFlow<PurecipesLink>()

			override fun deliver(url: String) = Unit
		},
	),
	publishWebLaunchLink = PublishWebLaunchLinkUseCase(
		object : WebLaunchLinkRepository {
			override fun readLaunchUrl(): String? = null
		},
		object : IncomingLinkRepository {
			override fun observeLinks() = emptyFlow<PurecipesLink>()

			override fun deliver(url: String) = Unit
		},
	),
	purecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG
	},
	onDeliverPendingIncomingLink = {},
	coroutineScope = CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher()),
)
