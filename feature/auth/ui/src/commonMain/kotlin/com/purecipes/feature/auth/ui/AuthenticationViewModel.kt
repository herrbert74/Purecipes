package com.purecipes.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.getError
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
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

	var message by mutableStateOf<String?>(null)
		private set

	var isBusy by mutableStateOf(false)
		private set

	init {
		scope.launch {
			observeAuthenticationState().collect { state ->
				authenticationState = state
				if (state is AuthenticationState.SignedIn) {
					isEmailFormVisible = false
					message = null
				}
			}
		}
	}

	fun onEmailProviderSelected() {
		isEmailFormVisible = true
		message = null
	}

	fun onEmailAuthenticationModeSelected(mode: EmailAuthenticationMode) {
		emailAuthenticationMode = mode
		message = null
	}

	fun onFirstNameChange(value: String) {
		firstName = value
		message = null
	}

	fun onFamilyNameChange(value: String) {
		familyName = value
		message = null
	}

	fun onEmailChange(value: String) {
		email = value
		message = null
	}

	fun onPasswordChange(value: String) {
		password = value
		message = null
	}

	fun onGoogleUnavailableSelected() {
		message = "Google sign-in needs a configured Web Client ID before it can be enabled."
	}

	fun onGoogleSignInResult(
		idToken: String?,
		email: String?,
		displayName: String,
		profileImageUrl: String?,
	) {
		if (idToken.isNullOrBlank()) {
			message = "Google sign-in was cancelled."
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
			message = result.getError()?.message
			isBusy = false
		}
	}

	fun onExternalProviderSignInResult(provider: AuthProvider, result: Result<ExternalAuthenticationProfile?>) {
		val failure = result.exceptionOrNull()
		if (failure != null) {
			message = failure.message ?: "${provider.providerDisplayName()} sign-in failed."
			return
		}
		val profile = result.getOrNull()
		if (profile == null) {
			message = "${provider.providerDisplayName()} sign-in was cancelled."
			return
		}
		scope.launch {
			isBusy = true
			val signInResult = signInWithExternalProvider(profile)
			message = signInResult.getError()?.message
			isBusy = false
		}
	}

	fun submitEmailAuthentication() {
		scope.launch {
			isBusy = true
			val result = when (emailAuthenticationMode) {
				EmailAuthenticationMode.SIGN_IN -> signInWithEmail(email, password)
				EmailAuthenticationMode.REGISTER -> registerWithEmail(firstName, familyName, email, password)
			}
			message = result.getError()?.message
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
}

@Composable
internal fun authenticationViewModel(
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
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
