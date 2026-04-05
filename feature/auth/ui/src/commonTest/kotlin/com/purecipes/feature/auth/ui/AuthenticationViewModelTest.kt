package com.purecipes.feature.auth.ui

import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

		assertIs<AuthenticationState.SignedIn>(viewModel.authenticationState)
		assertFalse(viewModel.isEmailFormVisible)
		assertNull(viewModel.message)
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

		assertEquals("Apple sign-in was cancelled.", viewModel.message)
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
		assertEquals("Google sign-in was cancelled.", viewModel.message)
		assertTrue(viewModel.authenticationState is AuthenticationState.SignedOut)
		viewModelScope.cancel()
	}

}
