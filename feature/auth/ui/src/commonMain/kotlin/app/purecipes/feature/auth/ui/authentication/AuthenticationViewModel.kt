package app.purecipes.feature.auth.ui.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AuthenticationViewModel(
	private val observeAuthenticationState: ObserveAuthenticationStateUseCase,
	private val signInWithExternalProvider: SignInWithExternalProviderUseCase,
	private val signInWithGoogle: SignInWithGoogleUseCase,
	private val deleteAccount: DeleteAccountUseCase,
	private val signOut: SignOutUseCase,
	observeConsentState: ObserveConsentStateUseCase,
	private val showConsentForm: ShowConsentFormUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	val consentState: StateFlow<ConsentState> = observeConsentState()

	var authenticationState by mutableStateOf<AuthenticationState>(observeAuthenticationState().value)
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
					message = null
				}
			}
		}
	}

	fun onGoogleUnavailableSelected() {
		message = "Google sign-in needs a configured Web Client ID before it can be enabled."
	}

	fun onManagePrivacySettingsClick() {
		showConsentForm()
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

	fun signOut() {
		scope.launch {
			isBusy = true
			signOut.invoke()
			isBusy = false
		}
	}

	fun deleteAccount() {
		scope.launch {
			isBusy = true
			message = deleteAccount.invoke().getError()?.message
			isBusy = false
		}
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}
}

private fun AuthProvider.providerDisplayName(): String {
	return when (this) {
		AuthProvider.EMAIL -> "Email"
		AuthProvider.GOOGLE -> "Google"
		AuthProvider.APPLE -> "Apple"
		AuthProvider.FACEBOOK -> "Facebook"
	}
}
