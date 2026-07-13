package app.purecipes.feature.auth.ui.registration

import app.purecipes.feature.analytics.domain.model.AnalyticsAuthMethod
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_UPPERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

	@Test
	fun `register with short password shows policy error`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("short1A")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_TOO_SHORT_MESSAGE
	}

	@Test
	fun `register with password missing uppercase shows policy error`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onPasswordChange("validpass12")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_UPPERCASE_MESSAGE
	}

	@Test
	fun `register with password missing lowercase shows policy error`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onPasswordChange("VALIDPASS12")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_LOWERCASE_MESSAGE
	}

	@Test
	fun `register with password missing number shows policy error`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onPasswordChange("ValidPasswd")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_NUMBER_MESSAGE
	}

	@Test
	fun `successful registration invokes callback with email`() = runViewModelTest {
		val viewModel = createViewModel()
		var registeredEmail: String? = null

		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("ValidPass12")
		viewModel.submitRegistration { registeredEmail = it }

		advanceUntilIdle()

		registeredEmail shouldBe "taylor@example.com"
	}

	@Test
	fun `successful registration tracks sign up completed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(analyticsRepository = analyticsRepository)

		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("ValidPass12")
		viewModel.submitRegistration { }

		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.SignUpCompleted(method = AnalyticsAuthMethod.EMAIL),
		)
	}

	private fun createViewModel(
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	): RegistrationViewModel {
		return RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(FakeAuthenticationRepository()),
			trackEvent = TrackEventUseCase(analyticsRepository),
		)
	}
}
