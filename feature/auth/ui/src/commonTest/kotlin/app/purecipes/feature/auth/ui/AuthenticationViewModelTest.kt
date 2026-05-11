package app.purecipes.feature.auth.ui

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {

	@Test
	fun `register signs the user in and hides the form`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onFirstNameChange("Taylor")
		viewModel.onFamilyNameChange("Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("secret")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.authenticationState.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		viewModel.isEmailFormVisible shouldBe false
		viewModel.message shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `external provider cancellation exposes a message`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
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
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onGoogleSignInResult(idToken = null, email = null, displayName = "", profileImageUrl = null)
		viewModel.message shouldBe "Google sign-in was cancelled."
		(viewModel.authenticationState is AuthenticationState.SignedOut) shouldBe true
		viewModelScope.cancel()
	}

}
