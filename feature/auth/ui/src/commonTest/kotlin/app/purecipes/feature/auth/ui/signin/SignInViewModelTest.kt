package app.purecipes.feature.auth.ui.signin

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

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
		val viewModel = createViewModel(repository, viewModelScope)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		ObserveAuthenticationStateUseCase(repository)().first()
			.shouldBeInstanceOf<AuthenticationState.SignedIn>()
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
		val viewModel = createViewModel(repository, viewModelScope)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.emailError shouldBe EMAIL_NOT_VERIFIED_MESSAGE
		viewModel.passwordError shouldBe null
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
		val viewModel = createViewModel(repository, viewModelScope)

		viewModel.onPasswordChange("wrong-password")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.passwordError shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		viewModel.emailError shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `sign in with invalid email shows email field error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "not-an-email",
			showRegistrationSuccessMessage = false,
			coroutineScope = viewModelScope,
		)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.emailError shouldBe INVALID_EMAIL_MESSAGE
		viewModel.passwordError shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `registration success shows info message`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "taylor@example.com",
			showRegistrationSuccessMessage = true,
			coroutineScope = viewModelScope,
		)

		viewModel.infoMessage shouldBe "Registration successful. Please check your email to verify your account."
		viewModel.showResendVerificationEmail shouldBe true
		viewModelScope.cancel()
	}

	@Test
	fun `forgot password with valid email shows success message`() = runTest {
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { Ok(Unit) },
		)
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = createViewModel(repository, viewModelScope)

		viewModel.sendPasswordResetEmail()

		advanceUntilIdle()

		viewModel.infoMessage shouldBe "Password reset email sent. Please check your inbox."
		viewModel.emailError shouldBe null
		viewModel.passwordError shouldBe null
		viewModelScope.cancel()
	}

	@Test
	fun `forgot password with blank email shows email field error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "",
			showRegistrationSuccessMessage = false,
			coroutineScope = viewModelScope,
		)

		viewModel.sendPasswordResetEmail()

		advanceUntilIdle()

		viewModel.emailError shouldBe EMAIL_REQUIRED_MESSAGE
		viewModel.infoMessage shouldBe null
		viewModelScope.cancel()
	}

	private fun createViewModel(
		repository: FakeAuthenticationRepository,
		viewModelScope: CoroutineScope,
	): SignInViewModel {
		return SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "taylor@example.com",
			showRegistrationSuccessMessage = false,
			coroutineScope = viewModelScope,
		)
	}
}
