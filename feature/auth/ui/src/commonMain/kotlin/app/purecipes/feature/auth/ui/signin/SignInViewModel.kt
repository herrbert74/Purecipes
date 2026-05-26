package app.purecipes.feature.auth.ui.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AssistedInject
class SignInViewModel(
	private val signInWithEmail: SignInWithEmailUseCase,
	private val resendEmailVerification: ResendEmailVerificationUseCase,
	private val sendPasswordResetEmail: SendPasswordResetEmailUseCase,
	@Assisted initialEmail: String,
	@Assisted showRegistrationSuccessMessage: Boolean,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var email by mutableStateOf(initialEmail)
		private set

	var password by mutableStateOf("")
		private set

	var emailError by mutableStateOf<String?>(null)
		private set

	var passwordError by mutableStateOf<String?>(null)
		private set

	var infoMessage by mutableStateOf(
		if (showRegistrationSuccessMessage) {
			"Registration successful. Please check your email to verify your account."
		} else {
			null
		},
	)
		private set

	var showResendVerificationEmail by mutableStateOf(showRegistrationSuccessMessage)
		private set

	var isBusy by mutableStateOf(false)
		private set

	fun onEmailChange(value: String) {
		email = value
		clearErrors()
	}

	fun onPasswordChange(value: String) {
		password = value
		clearErrors()
	}

	fun submitSignIn() {
		emailError = null
		passwordError = null
		scope.launch {
			isBusy = true
			val result = signInWithEmail(email, password)
			setSignInError(result.getError()?.message)
			isBusy = false
		}
	}

	fun resendVerificationEmail() {
		scope.launch {
			isBusy = true
			val result = resendEmailVerification(email, password)
			if (result.getError() == null) {
				infoMessage = "Verification email sent. Please check your inbox."
				emailError = null
				passwordError = null
				showResendVerificationEmail = true
			} else {
				setSignInError(result.getError()?.message)
			}
			isBusy = false
		}
	}

	fun sendPasswordResetEmail() {
		scope.launch {
			isBusy = true
			val result = sendPasswordResetEmail(email)
			if (result.getError() == null) {
				infoMessage = "Password reset email sent. Please check your inbox."
				emailError = null
				passwordError = null
			} else {
				setSignInError(result.getError()?.message)
			}
			isBusy = false
		}
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}

	private fun clearErrors() {
		emailError = null
		passwordError = null
		infoMessage = null
	}

	private fun setSignInError(errorMessage: String?) {
		emailError = errorMessage?.takeIf { it.isEmailFieldMessage() }
		passwordError = errorMessage?.takeIf { !it.isEmailFieldMessage() }
		showResendVerificationEmail = errorMessage == EMAIL_NOT_VERIFIED_MESSAGE
	}

	private fun String.isEmailFieldMessage(): Boolean {
		return this == EMAIL_REQUIRED_MESSAGE ||
			this == INVALID_EMAIL_MESSAGE ||
			this == EMAIL_NOT_VERIFIED_MESSAGE
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey(Factory::class)
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(initialEmail: String, showRegistrationSuccessMessage: Boolean): SignInViewModel
	}
}
