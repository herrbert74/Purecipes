package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsActiveTab
import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumStatus
import app.purecipes.feature.analytics.domain.model.AnalyticsUserState
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.library.ui.navigation.LibraryDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelGlobalPropertiesTest {

	private val sampleUser = fakeAuthUser(displayName = "Taylor")

	@Test
	fun `start sets active tab anonymous user state and free premium status`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		mainViewModelForTest(
			analyticsRepository = analyticsRepository,
			crashRepository = crashRepository,
		).start()

		analyticsRepository.globalProperties[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe
			AnalyticsValue.TextValue(AnalyticsActiveTab.SEARCH)
		analyticsRepository.globalProperties[AnalyticsGlobalProperty.USER_STATE] shouldBe
			AnalyticsValue.TextValue(AnalyticsUserState.ANONYMOUS)
		analyticsRepository.globalProperties[AnalyticsGlobalProperty.PREMIUM_STATUS] shouldBe
			AnalyticsValue.TextValue(AnalyticsPremiumStatus.FREE)
		crashRepository.customValues[AnalyticsGlobalProperty.ENVIRONMENT] shouldBe "debug"
		crashRepository.customValues[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe AnalyticsActiveTab.SEARCH
		crashRepository.customValues[AnalyticsGlobalProperty.USER_STATE] shouldBe AnalyticsUserState.ANONYMOUS
		crashRepository.lastUserId shouldBe null
	}

	@Test
	fun `tab selection and sign in update global properties`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			analyticsRepository = analyticsRepository,
			crashRepository = crashRepository,
		)
		viewModel.start()

		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Library })

		analyticsRepository.globalProperties[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe
			AnalyticsValue.TextValue(AnalyticsActiveTab.FAVORITES)
		crashRepository.customValues[AnalyticsGlobalProperty.ACTIVE_TAB] shouldBe AnalyticsActiveTab.FAVORITES

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
		crashRepository.customValues[AnalyticsGlobalProperty.USER_STATE] shouldBe AnalyticsUserState.LOGGED_IN
		crashRepository.lastUserId shouldBe sampleUser.id
	}

	@Test
	fun `recipe selection from favorites stamps favorites origin`() {
		val viewModel = mainViewModelForTest()
		viewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Library })
		viewModel.onRecipeSelected(42)

		viewModel.peekBackStack() shouldBe listOf(
			LibraryDestination(),
			RecipeDetailsDestination(42, origin = AnalyticsOrigin.FAVORITES.value),
		)
	}
}
