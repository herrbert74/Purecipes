package app.purecipes.feature.auth.ui.authentication

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_UPPERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
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
	fun `register shows verification message and switches to sign in`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("ValidPass12")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.authenticationState.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		viewModel.emailAuthenticationMode shouldBe EmailAuthenticationMode.SIGN_IN
		viewModel.infoMessage shouldBe "Registration successful. Please check your email to verify your account."
		viewModel.message shouldBe null
		viewModel.showResendVerificationEmail shouldBe true
		viewModelScope.cancel()
	}

	@Test
	fun `register with short password shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("short1A")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.passwordError shouldBe PASSWORD_TOO_SHORT_MESSAGE
		viewModel.message shouldBe null
		viewModel.infoMessage shouldBe null
		viewModel.emailAuthenticationMode shouldBe EmailAuthenticationMode.REGISTER
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing uppercase shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onPasswordChange("validpass12")
		viewModel.submitEmailAuthentication()

		viewModel.passwordError shouldBe PASSWORD_MISSING_UPPERCASE_MESSAGE
		viewModel.message shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing lowercase shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onPasswordChange("VALIDPASS12")
		viewModel.submitEmailAuthentication()

		viewModel.passwordError shouldBe PASSWORD_MISSING_LOWERCASE_MESSAGE
		viewModel.message shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing number shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailAuthenticationModeSelected(EmailAuthenticationMode.REGISTER)
		viewModel.onPasswordChange("ValidPasswd")
		viewModel.submitEmailAuthentication()

		viewModel.passwordError shouldBe PASSWORD_MISSING_NUMBER_MESSAGE
		viewModel.message shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `sign in does not enforce password policy`() = runTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Ok(
					AuthUser(
						id = "user-1",
						email = "taylor@example.com",
						displayName = "Taylor Baker",
						firstName = null,
						familyName = null,
						profileImageUrl = null,
						provider = AuthProvider.EMAIL,
					),
				)
			},
		)
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("secret")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.message shouldBe null
		viewModel.authenticationState.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		viewModelScope.cancel()
	}

	@Test
	fun `unverified email sign in shows resend button`() = runTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Err(Failure.ServerError(EMAIL_NOT_VERIFIED_MESSAGE))
			},
		)
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("secret")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.emailError shouldBe EMAIL_NOT_VERIFIED_MESSAGE
		viewModel.passwordError shouldBe null
		viewModel.message shouldBe null
		viewModel.showResendVerificationEmail shouldBe true
		viewModelScope.cancel()
	}

	@Test
	fun `sign in with incorrect password shows password field error`() = runTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Err(Failure.ServerError(INCORRECT_EMAIL_OR_PASSWORD_MESSAGE))
			},
		)
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("wrong-password")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.passwordError shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		viewModel.emailError shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `sign in with invalid email shows email field error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = AuthenticationViewModel(
			observeAuthenticationState = ObserveAuthenticationStateUseCase(repository),
			signInWithEmail = SignInWithEmailUseCase(repository),
			registerWithEmail = RegisterWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			signInWithExternalProvider = SignInWithExternalProviderUseCase(repository),
			signInWithGoogle = SignInWithGoogleUseCase(repository),
			signOut = SignOutUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onEmailProviderSelected()
		viewModel.onEmailChange("not-an-email")
		viewModel.onPasswordChange("secret")
		viewModel.submitEmailAuthentication()

		advanceUntilIdle()

		viewModel.emailError shouldBe INVALID_EMAIL_MESSAGE
		viewModel.passwordError shouldBe null
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
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
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
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
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
