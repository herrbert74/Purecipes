package app.purecipes.feature.auth.ui.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
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
) : ViewModel() {

	var displayName by mutableStateOf("")
		private set

	var email by mutableStateOf("")
		private set

	var password by mutableStateOf("")
		private set

	var emailError by mutableStateOf<String?>(null)
		private set

	var passwordError by mutableStateOf<String?>(null)
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

	fun submitRegistration(onSuccess: (email: String) -> Unit) {
		validatePasswordPolicy(password)?.let { validationError ->
			emailError = null
			passwordError = validationError
			return
		}
		emailError = null
		passwordError = null
		viewModelScope.launch {
			isBusy = true
			val result = registerWithEmail(displayName, email, password)
			if (result.getError() == null) {
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
	}

	private fun setRegistrationError(errorMessage: String?) {
		emailError = errorMessage?.takeIf { it.isEmailFieldMessage() }
		passwordError = errorMessage?.takeIf { !it.isEmailFieldMessage() }
	}

	private fun String.isEmailFieldMessage(): Boolean {
		return this == EMAIL_REQUIRED_MESSAGE || this == INVALID_EMAIL_MESSAGE
	}
}
