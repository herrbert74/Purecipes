package app.purecipes.feature.auth.ui.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import com.github.michaelbull.result.getError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class SignInViewModel(
	private val signInWithEmail: SignInWithEmailUseCase,
	private val resendEmailVerification: ResendEmailVerificationUseCase,
	initialEmail: String,
	showRegistrationSuccessMessage: Boolean,
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
}

@Composable
internal fun signInViewModel(
	signInWithEmail: SignInWithEmailUseCase,
	resendEmailVerification: ResendEmailVerificationUseCase,
	initialEmail: String,
	showRegistrationSuccessMessage: Boolean,
): SignInViewModel {
	return viewModel(
		key = "SignInViewModel:$initialEmail:$showRegistrationSuccessMessage:${signInWithEmail.hashCode()}",
		factory = viewModelFactory {
			initializer {
				SignInViewModel(
					signInWithEmail = signInWithEmail,
					resendEmailVerification = resendEmailVerification,
					initialEmail = initialEmail,
					showRegistrationSuccessMessage = showRegistrationSuccessMessage,
				)
			}
		},
	)
}
