package app.purecipes.feature.auth.ui.signin

import app.purecipes.base.kotlin.result.Failure
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
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

	@Test
	fun `sign in does not enforce password policy`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Ok(fakeAuthUser())
			},
		)
		val viewModel = createViewModel(repository)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		ObserveAuthenticationStateUseCase(repository)().first()
			.shouldBeInstanceOf<AuthenticationState.SignedIn>()
	}

	@Test
	fun `unverified email sign in shows resend button`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Err(Failure.ServerError(EMAIL_NOT_VERIFIED_MESSAGE))
			},
		)
		val viewModel = createViewModel(repository)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.emailError shouldBe EMAIL_NOT_VERIFIED_MESSAGE
		viewModel.passwordError shouldBe null
		viewModel.showResendVerificationEmail shouldBe true
	}

	@Test
	fun `sign in with incorrect password shows password field error`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Err(Failure.ServerError(INCORRECT_EMAIL_OR_PASSWORD_MESSAGE))
			},
		)
		val viewModel = createViewModel(repository)

		viewModel.onPasswordChange("wrong-password")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.passwordError shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		viewModel.emailError shouldBe null
	}

	@Test
	fun `sign in with invalid email shows email field error`() = runViewModelTest {
		val repository = FakeAuthenticationRepository()
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "not-an-email",
			showRegistrationSuccessMessage = false,
		)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.emailError shouldBe INVALID_EMAIL_MESSAGE
		viewModel.passwordError shouldBe null
	}

	@Test
	fun `registration success shows info message`() = runViewModelTest {
		val repository = FakeAuthenticationRepository()
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "taylor@example.com",
			showRegistrationSuccessMessage = true,
		)

		viewModel.infoMessage shouldBe "Registration successful. Please check your email to verify your account."
		viewModel.showResendVerificationEmail shouldBe true
	}

	@Test
	fun `forgot password with valid email shows success message`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { Ok(Unit) },
		)
		val viewModel = createViewModel(repository)

		viewModel.sendPasswordResetEmail()

		advanceUntilIdle()

		viewModel.infoMessage shouldBe "Password reset email sent. Please check your inbox."
		viewModel.emailError shouldBe null
		viewModel.passwordError shouldBe null
	}

	@Test
	fun `forgot password with blank email shows email field error`() = runViewModelTest {
		val repository = FakeAuthenticationRepository()
		val viewModel = SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "",
			showRegistrationSuccessMessage = false,
		)

		viewModel.sendPasswordResetEmail()

		advanceUntilIdle()

		viewModel.emailError shouldBe EMAIL_REQUIRED_MESSAGE
		viewModel.infoMessage shouldBe null
	}

	private fun createViewModel(repository: FakeAuthenticationRepository): SignInViewModel {
		return SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			initialEmail = "taylor@example.com",
			showRegistrationSuccessMessage = false,
		)
	}
}
