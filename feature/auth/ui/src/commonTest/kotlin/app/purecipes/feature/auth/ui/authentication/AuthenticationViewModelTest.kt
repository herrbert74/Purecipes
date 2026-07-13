package app.purecipes.feature.auth.ui.authentication

import app.purecipes.feature.analytics.domain.model.AnalyticsAuthMethod
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithFacebookUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {

	@Test
	fun `external provider cancellation exposes a message`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onExternalProviderSignInResult(AuthProvider.APPLE, Result.success(null))

		viewModel.message shouldBe "Apple sign-in was cancelled."
	}

	@Test
	fun `blank google result shows cancellation message`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onGoogleSignInResult(idToken = null, email = null, displayName = "", profileImageUrl = null)
		viewModel.message shouldBe "Google sign-in was cancelled."
		(viewModel.authenticationState is AuthenticationState.SignedOut) shouldBe true
	}

	@Test
	fun `blank facebook result shows cancellation message`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onFacebookSignInResult(idToken = null, email = null, displayName = "", profileImageUrl = null)
		viewModel.message shouldBe "Facebook sign-in was cancelled."
		(viewModel.authenticationState is AuthenticationState.SignedOut) shouldBe true
	}

	@Test
	fun `successful google sign in tracks sign in completed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(analyticsRepository = analyticsRepository)

		viewModel.onGoogleSignInResult(
			idToken = "token",
			email = "user@example.com",
			displayName = "User",
			profileImageUrl = null,
		)
		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.GOOGLE),
		)
	}

	@Test
	fun `successful apple sign in tracks sign in completed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(analyticsRepository = analyticsRepository)

		viewModel.onExternalProviderSignInResult(
			AuthProvider.APPLE,
			Result.success(
				ExternalAuthenticationProfile(
					provider = AuthProvider.APPLE,
					id = "apple-id",
					email = "user@example.com",
					displayName = "User",
					profileImageUrl = null,
				),
			),
		)
		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.APPLE),
		)
	}

	@Test
	fun `sign out tracks sign out event`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			repository = FakeAuthenticationRepository(
				AuthenticationState.SignedIn(fakeAuthUser()),
			),
			analyticsRepository = analyticsRepository,
		)

		viewModel.signOut()
		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(AnalyticsEvent.SignOut)
	}

	private fun createViewModel(
		repository: FakeAuthenticationRepository = FakeAuthenticationRepository(),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		crashRepository: FakeCrashRepository = FakeCrashRepository(),
	): AuthenticationViewModel {
		val consentRepository = FakeConsentRepository(ConsentState.OBTAINED)
		return AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithFacebook = SignInWithFacebookUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			deleteAccount = DeleteAccountUseCase(repository),
			signOut = SignOutUseCase(repository),
			observeConsentState = ObserveConsentStateUseCase(consentRepository),
			showConsentForm = ShowConsentFormUseCase(consentRepository),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
			sendHandledException = SendHandledExceptionUseCase(crashRepository),
		)
	}
}
