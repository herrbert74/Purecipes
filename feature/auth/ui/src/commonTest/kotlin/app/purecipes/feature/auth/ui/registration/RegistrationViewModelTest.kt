package app.purecipes.feature.auth.ui.registration

import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_UPPERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import io.kotest.matchers.shouldBe
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
class RegistrationViewModelTest {

	@Test
	fun `register with short password shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("short1A")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_TOO_SHORT_MESSAGE
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing uppercase shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onPasswordChange("validpass12")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_UPPERCASE_MESSAGE
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing lowercase shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onPasswordChange("VALIDPASS12")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_LOWERCASE_MESSAGE
		viewModelScope.cancel()
	}

	@Test
	fun `register with password missing number shows policy error`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(repository),
			coroutineScope = viewModelScope,
		)

		viewModel.onPasswordChange("ValidPasswd")
		viewModel.submitRegistration { }

		viewModel.passwordError shouldBe PASSWORD_MISSING_NUMBER_MESSAGE
		viewModelScope.cancel()
	}

	@Test
	fun `successful registration invokes callback with email`() = runTest {
		val repository = FakeAuthenticationRepository()
		val viewModelScope = CoroutineScope(SupervisorJob(Job()) + StandardTestDispatcher(testScheduler))
		val viewModel = RegistrationViewModel(
			registerWithEmail = RegisterWithEmailUseCase(repository),
			coroutineScope = viewModelScope,
		)
		var registeredEmail: String? = null

		viewModel.onDisplayNameChange("Taylor Baker")
		viewModel.onEmailChange("taylor@example.com")
		viewModel.onPasswordChange("ValidPass12")
		viewModel.submitRegistration { registeredEmail = it }

		advanceUntilIdle()

		registeredEmail shouldBe "taylor@example.com"
		viewModelScope.cancel()
	}
}
