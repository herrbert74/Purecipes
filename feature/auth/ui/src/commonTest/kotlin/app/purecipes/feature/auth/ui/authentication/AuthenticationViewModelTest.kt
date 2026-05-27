package app.purecipes.feature.auth.ui.authentication

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {

	@Test
	fun `external provider cancellation exposes a message`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val consentRepository = FakeConsentRepository(ConsentState.OBTAINED)
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			deleteAccount = DeleteAccountUseCase(repository),
			signOut = SignOutUseCase(repository),
			observeConsentState = ObserveConsentStateUseCase(consentRepository),
			showConsentForm = ShowConsentFormUseCase(consentRepository),
			coroutineScope = viewModelScope,
		)

		viewModel.onExternalProviderSignInResult(AuthProvider.APPLE, Result.success(null))

		viewModel.message shouldBe "Apple sign-in was cancelled."
		viewModelScope.cancel()
	}

	@Test
	fun `blank google result shows cancellation message`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val consentRepository = FakeConsentRepository(ConsentState.OBTAINED)
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			deleteAccount = DeleteAccountUseCase(repository),
			signOut = SignOutUseCase(repository),
			observeConsentState = ObserveConsentStateUseCase(consentRepository),
			showConsentForm = ShowConsentFormUseCase(consentRepository),
			coroutineScope = viewModelScope,
		)

		viewModel.onGoogleSignInResult(idToken = null, email = null, displayName = "", profileImageUrl = null)
		viewModel.message shouldBe "Google sign-in was cancelled."
		(viewModel.authenticationState is AuthenticationState.SignedOut) shouldBe true
		viewModelScope.cancel()
	}
}
