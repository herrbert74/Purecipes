package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsActiveTab
import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsUserState
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelGlobalPropertiesTest {

	private val sampleUser = fakeAuthUser(displayName = "Taylor")

	@Test
	fun `start sets active tab and anonymous user state`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		mainViewModelForTest(analyticsRepository = analyticsRepository).start()

		analyticsRepository.globalProperties[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe
			AnalyticsValue.TextValue(AnalyticsActiveTab.SEARCH)
		analyticsRepository.globalProperties[AnalyticsGlobalProperty.USER_STATE] shouldBe
			AnalyticsValue.TextValue(AnalyticsUserState.ANONYMOUS)
	}

	@Test
	fun `tab selection and sign in update global properties`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			analyticsRepository = analyticsRepository,
		)
		viewModel.start()

		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })

		analyticsRepository.globalProperties[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe
			AnalyticsValue.TextValue(AnalyticsActiveTab.FAVORITES)

		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		analyticsRepository.globalProperties[AnalyticsGlobalProperty.USER_STATE] shouldBe
			AnalyticsValue.TextValue(AnalyticsUserState.LOGGED_IN)
	}

	@Test
	fun `recipe selection from favorites stamps favorites origin`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Favorites })
		viewModel.onRecipeSelected(42)

		viewModel.peekBackStack() shouldBe listOf(
			FavoritesDestination(),
			RecipeDetailsDestination(42, origin = AnalyticsOrigin.FAVORITES.value),
		)
	}
}
