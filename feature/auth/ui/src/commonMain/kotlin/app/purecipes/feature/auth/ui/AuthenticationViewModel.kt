package app.purecipes.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import com.github.michaelbull.result.getError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal enum class EmailAuthenticationMode {
	SIGN_IN,
	REGISTER,
}

internal class AuthenticationViewModel(
	private val observeAuthenticationState: ObserveAuthenticationStateUseCase,
	private val signInWithEmail: SignInWithEmailUseCase,
	private val registerWithEmail: RegisterWithEmailUseCase,
	private val resendEmailVerification: ResendEmailVerificationUseCase,
	private val signInWithExternalProvider: SignInWithExternalProviderUseCase,
	private val signInWithGoogle: SignInWithGoogleUseCase,
	private val signOut: SignOutUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var authenticationState by mutableStateOf<AuthenticationState>(observeAuthenticationState().value)
		private set

	var emailAuthenticationMode by mutableStateOf(EmailAuthenticationMode.SIGN_IN)
		private set

	var isEmailFormVisible by mutableStateOf(false)
		private set

	var firstName by mutableStateOf("")
		private set

	var familyName by mutableStateOf("")
		private set

	var email by mutableStateOf("")
		private set

	var password by mutableStateOf("")
		private set

	var passwordError by mutableStateOf<String?>(null)
		private set

	var message by mutableStateOf<String?>(null)
		private set

	var infoMessage by mutableStateOf<String?>(null)
		private set

	var showResendVerificationEmail by mutableStateOf(false)
		private set

	var isBusy by mutableStateOf(false)
		private set

	init {
		scope.launch {
			observeAuthenticationState().collect { state ->
				authenticationState = state
				if (state is AuthenticationState.SignedIn) {
					isEmailFormVisible = false
					clearAuthMessages()
				}
			}
		}
	}

	fun onEmailProviderSelected() {
		isEmailFormVisible = true
		clearAuthMessages()
	}

	fun onEmailAuthenticationModeSelected(mode: EmailAuthenticationMode) {
		emailAuthenticationMode = mode
		clearAuthMessages()
	}

	fun onFirstNameChange(value: String) {
		firstName = value
		clearAuthMessages()
	}

	fun onFamilyNameChange(value: String) {
		familyName = value
		clearAuthMessages()
	}

	fun onEmailChange(value: String) {
		email = value
		clearAuthMessages()
	}

	fun onPasswordChange(value: String) {
		password = value
		clearAuthMessages()
	}

	fun onGoogleUnavailableSelected() {
		message = "Google sign-in needs a configured Web Client ID before it can be enabled."
		showResendVerificationEmail = false
	}

	fun onGoogleSignInResult(
		idToken: String?,
		email: String?,
		displayName: String,
		profileImageUrl: String?,
	) {
		if (idToken.isNullOrBlank()) {
			message = "Google sign-in was cancelled."
			showResendVerificationEmail = false
			return
		}
		scope.launch {
			isBusy = true
			val result = signInWithGoogle(
				GoogleAuthenticationProfile(
					idToken = idToken,
					email = email,
					displayName = displayName,
					profileImageUrl = profileImageUrl,
				),
			)
			setAuthMessage(result.getError()?.message)
			isBusy = false
		}
	}

	fun onExternalProviderSignInResult(provider: AuthProvider, result: Result<ExternalAuthenticationProfile?>) {
		val failure = result.exceptionOrNull()
		if (failure != null) {
			message = failure.message ?: "${provider.providerDisplayName()} sign-in failed."
			showResendVerificationEmail = false
			return
		}
		val profile = result.getOrNull()
		if (profile == null) {
			message = "${provider.providerDisplayName()} sign-in was cancelled."
			showResendVerificationEmail = false
			return
		}
		scope.launch {
			isBusy = true
			val signInResult = signInWithExternalProvider(profile)
			setAuthMessage(signInResult.getError()?.message)
			isBusy = false
		}
	}

	fun submitEmailAuthentication() {
		if (emailAuthenticationMode == EmailAuthenticationMode.REGISTER) {
			validatePasswordPolicy(password)?.let { validationError ->
				passwordError = validationError
				message = null
				return
			}
		}
		passwordError = null
		scope.launch {
			isBusy = true
			when (emailAuthenticationMode) {
				EmailAuthenticationMode.SIGN_IN -> {
					val result = signInWithEmail(email, password)
					setAuthMessage(result.getError()?.message)
				}
				EmailAuthenticationMode.REGISTER -> {
					val result = registerWithEmail(firstName, familyName, email, password)
					if (result.getError() == null) {
						infoMessage = "Registration successful. Please check your email to verify your account."
						emailAuthenticationMode = EmailAuthenticationMode.SIGN_IN
						message = null
						passwordError = null
						showResendVerificationEmail = true
					} else {
						setAuthMessage(result.getError()?.message)
					}
				}
			}
			isBusy = false
		}
	}

	fun resendVerificationEmail() {
		scope.launch {
			isBusy = true
			val result = resendEmailVerification(email, password)
			if (result.getError() == null) {
				infoMessage = "Verification email sent. Please check your inbox."
				message = null
				showResendVerificationEmail = true
			} else {
				setAuthMessage(result.getError()?.message)
			}
			isBusy = false
		}
	}

	fun signOut() {
		scope.launch {
			isBusy = true
			signOut.invoke()
			isBusy = false
		}
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}

	private fun clearAuthMessages() {
		message = null
		passwordError = null
		infoMessage = null
		showResendVerificationEmail = false
	}

	private fun setAuthMessage(errorMessage: String?) {
		message = errorMessage
		showResendVerificationEmail = errorMessage == EMAIL_NOT_VERIFIED_MESSAGE
	}
}

@Composable
internal fun authenticationViewModel(
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	resendEmailVerification: ResendEmailVerificationUseCase,
	signInWithExternalProvider: SignInWithExternalProviderUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	signOut: SignOutUseCase,
): AuthenticationViewModel {
	return viewModel(
		key = "AuthenticationViewModel:${observeAuthenticationState.hashCode()}",
		factory = viewModelFactory {
			initializer {
				AuthenticationViewModel(
					observeAuthenticationState = observeAuthenticationState,
					signInWithEmail = signInWithEmail,
					registerWithEmail = registerWithEmail,
					resendEmailVerification = resendEmailVerification,
					signInWithExternalProvider = signInWithExternalProvider,
					signInWithGoogle = signInWithGoogle,
					signOut = signOut,
				)
			}
		},
	)
}

private fun AuthProvider.providerDisplayName(): String {
	return when (this) {
		AuthProvider.EMAIL -> "Email"
		AuthProvider.GOOGLE -> "Google"
		AuthProvider.APPLE -> "Apple"
		AuthProvider.FACEBOOK -> "Facebook"
	}
}
