package app.purecipes.feature.auth.ui.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsAuthMethod
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_CONFIRMATION_MISMATCH_MESSAGE
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class RegistrationViewModel(
	private val registerWithEmail: RegisterWithEmailUseCase,
	private val trackEvent: TrackEventUseCase,
) : ViewModel() {

	var displayName by mutableStateOf("")
		private set

	var email by mutableStateOf("")
		private set

	var password by mutableStateOf("")
		private set

	var confirmPassword by mutableStateOf("")
		private set

	var emailError by mutableStateOf<String?>(null)
		private set

	var passwordError by mutableStateOf<String?>(null)
		private set

	var confirmPasswordError by mutableStateOf<String?>(null)
		private set

	var isBusy by mutableStateOf(false)
		private set

	fun onDisplayNameChange(value: String) {
		displayName = value
		clearErrors()
	}

	fun onEmailChange(value: String) {
		email = value
		clearErrors()
	}

	fun onPasswordChange(value: String) {
		password = value
		clearErrors()
	}

	fun onConfirmPasswordChange(value: String) {
		confirmPassword = value
		clearErrors()
	}

	fun submitRegistration(onSuccess: (email: String) -> Unit) {
		validatePasswordPolicy(password)?.let { validationError ->
			emailError = null
			passwordError = validationError
			confirmPasswordError = null
			return
		}
		if (password != confirmPassword) {
			emailError = null
			passwordError = null
			confirmPasswordError = PASSWORD_CONFIRMATION_MISMATCH_MESSAGE
			return
		}
		emailError = null
		passwordError = null
		confirmPasswordError = null
		viewModelScope.launch {
			isBusy = true
			val result = registerWithEmail(displayName, email, password)
			if (result.getError() == null) {
				trackEvent(AnalyticsEvent.SignUpCompleted(method = AnalyticsAuthMethod.EMAIL))
				onSuccess(email)
			} else {
				setRegistrationError(result.getError()?.message)
			}
			isBusy = false
		}
	}

	private fun clearErrors() {
		emailError = null
		passwordError = null
		confirmPasswordError = null
	}

	private fun setRegistrationError(errorMessage: String?) {
		emailError = errorMessage?.takeIf { it.isEmailFieldMessage() }
		passwordError = errorMessage?.takeIf { !it.isEmailFieldMessage() }
		confirmPasswordError = null
	}

	private fun String.isEmailFieldMessage(): Boolean {
		return this == EMAIL_REQUIRED_MESSAGE || this == INVALID_EMAIL_MESSAGE
	}
}
