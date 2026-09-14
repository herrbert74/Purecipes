package app.purecipes.feature.auth.ui.signin

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsAuthMethod
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_RESET_EMAIL_SENT_MESSAGE
import app.purecipes.shared.domain.model.REGISTRATION_SUCCESS_MESSAGE
import app.purecipes.shared.domain.model.VERIFICATION_EMAIL_SENT_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
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
	fun `successful email sign in tracks sign in completed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Ok(fakeAuthUser())
			},
		)
		val viewModel = createViewModel(repository, analyticsRepository, crashRepository)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.EMAIL),
		)
		crashRepository.breadcrumbs shouldBe listOf(
			CrashBreadcrumb.signInAttempted(AnalyticsAuthMethod.EMAIL),
		)
	}

	@Test
	fun `failed email sign in reports handled exception`() = runViewModelTest {
		val crashRepository = FakeCrashRepository()
		val repository = FakeAuthenticationRepository(
			signInWithEmailHandler = { _, _ ->
				Err(Failure.ServerError(INCORRECT_EMAIL_OR_PASSWORD_MESSAGE))
			},
		)
		val viewModel = createViewModel(repository, crashRepository = crashRepository)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		crashRepository.breadcrumbs shouldBe listOf(
			CrashBreadcrumb.signInAttempted(AnalyticsAuthMethod.EMAIL),
		)
		crashRepository.handledExceptions.map { it.message } shouldBe listOf(AnalyticsErrorKind.SERVER_ERROR)
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
		val viewModel = createViewModel(
			repository = repository,
			initialEmail = "not-an-email",
		)

		viewModel.onPasswordChange("secret")
		viewModel.submitSignIn()

		advanceUntilIdle()

		viewModel.emailError shouldBe INVALID_EMAIL_MESSAGE
		viewModel.passwordError shouldBe null
	}

	@Test
	fun `registration success shows info message`() = runViewModelTest {
		val viewModel = createViewModel(
			repository = FakeAuthenticationRepository(),
			showRegistrationSuccessMessage = true,
		)

		viewModel.infoMessage shouldBe REGISTRATION_SUCCESS_MESSAGE
		viewModel.showResendVerificationEmail shouldBe true
	}

	@Test
	fun `resend verification email shows success message`() = runViewModelTest {
		val viewModel = createViewModel(FakeAuthenticationRepository())

		viewModel.onPasswordChange("secret")
		viewModel.resendVerificationEmail()

		advanceUntilIdle()

		viewModel.infoMessage shouldBe VERIFICATION_EMAIL_SENT_MESSAGE
	}

	@Test
	fun `forgot password with valid email shows confirmation dialog`() = runViewModelTest {
		var sendCount = 0
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = {
				sendCount++
				Ok(Unit)
			},
		)
		val viewModel = createViewModel(repository)

		viewModel.requestPasswordReset()

		viewModel.showForgotPasswordConfirmDialog shouldBe true
		viewModel.emailError shouldBe null
		sendCount shouldBe 0
	}

	@Test
	fun `confirming forgot password sends email and shows success message`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { Ok(Unit) },
		)
		val viewModel = createViewModel(repository)

		viewModel.requestPasswordReset()
		viewModel.confirmPasswordReset()

		advanceUntilIdle()

		viewModel.infoMessage shouldBe PASSWORD_RESET_EMAIL_SENT_MESSAGE
		viewModel.emailError shouldBe null
		viewModel.passwordError shouldBe null
		viewModel.showForgotPasswordConfirmDialog shouldBe false
	}

	@Test
	fun `dismissing forgot password confirmation does not send email`() = runViewModelTest {
		var sendCount = 0
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = {
				sendCount++
				Ok(Unit)
			},
		)
		val viewModel = createViewModel(repository)

		viewModel.requestPasswordReset()
		viewModel.dismissPasswordResetConfirmation()

		advanceUntilIdle()

		sendCount shouldBe 0
		viewModel.showForgotPasswordConfirmDialog shouldBe false
		viewModel.infoMessage shouldBe null
	}

	@Test
	fun `forgot password with blank email shows email field error`() = runViewModelTest {
		val viewModel = createViewModel(
			repository = FakeAuthenticationRepository(),
			initialEmail = "",
		)

		viewModel.requestPasswordReset()

		viewModel.emailError shouldBe EMAIL_REQUIRED_MESSAGE
		viewModel.infoMessage shouldBe null
		viewModel.showForgotPasswordConfirmDialog shouldBe false
	}

	@Test
	fun `forgot password with invalid email shows email field error`() = runViewModelTest {
		val viewModel = createViewModel(
			repository = FakeAuthenticationRepository(),
			initialEmail = "not-an-email",
		)

		viewModel.requestPasswordReset()

		viewModel.emailError shouldBe INVALID_EMAIL_MESSAGE
		viewModel.showForgotPasswordConfirmDialog shouldBe false
	}

	@Test
	fun `password change does not clear password reset success message`() = runViewModelTest {
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { Ok(Unit) },
		)
		val viewModel = createViewModel(repository)

		viewModel.requestPasswordReset()
		viewModel.confirmPasswordReset()
		advanceUntilIdle()
		viewModel.onPasswordChange("secret")

		viewModel.infoMessage shouldBe PASSWORD_RESET_EMAIL_SENT_MESSAGE
	}

	private fun createViewModel(
		repository: FakeAuthenticationRepository,
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		crashRepository: FakeCrashRepository = FakeCrashRepository(),
		initialEmail: String = "taylor@example.com",
		showRegistrationSuccessMessage: Boolean = false,
	): SignInViewModel {
		return SignInViewModel(
			signInWithEmail = SignInWithEmailUseCase(repository),
			resendEmailVerification = ResendEmailVerificationUseCase(repository),
			sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
			sendHandledException = SendHandledExceptionUseCase(crashRepository),
			initialEmail = initialEmail,
			showRegistrationSuccessMessage = showRegistrationSuccessMessage,
		)
	}
}
