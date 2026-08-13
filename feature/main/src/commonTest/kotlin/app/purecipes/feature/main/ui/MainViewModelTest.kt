package app.purecipes.feature.main.ui

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.domain.repository.AdsRepository
import app.purecipes.feature.analytics.domain.model.AnalyticsAdPlacement
import app.purecipes.feature.analytics.domain.model.AnalyticsDeepLinkType
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateEditorDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.settings.ui.navigation.AccountSettingsDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import app.purecipes.shared.ui.navigation.PostLoginAction
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainViewModelTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	@Test
	fun `isContentReady reflects search readiness coordinator`() {
		val readiness = SearchReadinessCoordinator()
		val viewModel = mainViewModelForTest(searchReadiness = readiness)

		viewModel.isContentReady.value shouldBe false
		readiness.reportReady()
		viewModel.isContentReady.value shouldBe true
	}

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
		viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenSearchFilters)

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
		viewModel.takePendingPostLoginAction() shouldBe PostLoginAction.OpenSearchFilters
	}

	@Test
	fun `selecting search tab clears pending post login origin and resets open filters destination`() {
		val viewModel = mainViewModelForTest()

		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Account })
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Search })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination())
		viewModel.takePendingPostLoginAction() shouldBe null
	}

	@Test
	fun `switching tabs preserves in tab depth`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination())
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Search })

		viewModel.peekBackStack() shouldBe listOf(SearchDestination(), RecipeDetailsDestination(42))
	}

	@Test
	fun `re tapping active tab pops to tab root`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Search })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination())
	}

	@Test
	fun `back at non search tab root switches to search`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })

		viewModel.onBack() shouldBe true

		viewModel.peekBackStack() shouldBe listOf<NavKey>(SearchDestination())
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

		viewModel.peekBackStack() shouldBe listOf(AccountDestination, AccountSettingsDestination)
	}

	@Test
	fun `deep link to recipe opens recipe details on search tab`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = mainViewModelForTest(analyticsRepository = analyticsRepository)
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })
		viewModel.onDeepLink(PurecipesLink.Recipe(99))

		viewModel.peekBackStack() shouldBe listOf(
			SearchDestination(),
			RecipeDetailsDestination(99, origin = AnalyticsOrigin.DEEP_LINK.value),
		)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.DeepLinkOpened>() shouldBe listOf(
			AnalyticsEvent.DeepLinkOpened(
				linkType = AnalyticsDeepLinkType.RECIPE,
				recipeId = 99,
			),
		)
	}

	@Test
	fun `deep link to recipe does not clobber other tab stacks`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })
		viewModel.onDeepLink(PurecipesLink.Recipe(99))

		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })

		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination())
	}

	@Test
	fun `deep link to cookbook share switches to favorites with share token on destination`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = mainViewModelForTest(analyticsRepository = analyticsRepository)
		viewModel.onDeepLink(PurecipesLink.CookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(
			FavoritesDestination(cookbookShareToken = sampleShareToken),
		)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.DeepLinkOpened>() shouldBe listOf(
			AnalyticsEvent.DeepLinkOpened(
				linkType = AnalyticsDeepLinkType.COOKBOOK,
				tokenPresent = true,
			),
		)
	}

	@Test
	fun `requestLoginForPostLoginAction preserves cookbook share token until sign in`() {
		val viewModel = mainViewModelForTest()
		viewModel.requestLoginForPostLoginAction(PostLoginAction.ImportCookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf<NavKey>(AccountDestination)
		viewModel.takePendingPostLoginAction() shouldBe
			PostLoginAction.ImportCookbookShare(sampleShareToken)
	}

	@Test
	fun `onBack at search root returns false`() {
		val viewModel = mainViewModelForTest()
		viewModel.onBack() shouldBe false
	}

	@Test
	fun `editing created recipe pushes editor onto the current tab`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })
		viewModel.createRecipeTabNavigator.openEditor(42)

		viewModel.selectedTab.stackId shouldBe MainTabStackId.Favorites
		viewModel.peekBackStack() shouldBe listOf(
			FavoritesDestination(),
			CreateEditorDestination(recipeId = 42),
		)
		viewModel.onBack() shouldBe true
		viewModel.peekBackStack() shouldBe listOf<NavKey>(FavoritesDestination())
	}

	@Test
	fun `selecting another recipe replaces current recipe details destination`() {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onRecipeSelected(99)

		viewModel.peekBackStack() shouldBe listOf(SearchDestination(), RecipeDetailsDestination(99))
	}

	@Test
	fun `back removes only the top destination`() = runUnconfinedViewModelTest {
		val viewModel = mainViewModelForTest()
		viewModel.onRecipeSelected(42)
		viewModel.onStartCooking(42)
		viewModel.onBack()

		viewModel.peekBackStack() shouldBe listOf(SearchDestination(), RecipeDetailsDestination(42))
	}

	@Test
	fun `pre cook interstitial tracks ad impression and click callbacks`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = mainViewModelForTest(
			analyticsRepository = analyticsRepository,
			adsRepository = object : AdsRepository {
				override fun initialize() = Unit

				override fun showInterstitial(
					onDismissed: () -> Unit,
					onImpression: (() -> Unit)?,
					onClicked: (() -> Unit)?,
				) {
					onImpression?.invoke()
					onClicked?.invoke()
					onDismissed()
				}
			},
			preCookInterstitialChance = { true },
		)

		viewModel.onRecipeSelected(42)
		viewModel.onStartCooking(42)

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.AdImpression>() shouldBe listOf(
			AnalyticsEvent.AdImpression(placement = AnalyticsAdPlacement.INTERSTITIAL),
		)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.AdClicked>() shouldBe listOf(
			AnalyticsEvent.AdClicked(placement = AnalyticsAdPlacement.INTERSTITIAL),
		)
	}
}
