package app.purecipes.feature.auth.ui.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsAuthMethod
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.FacebookAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithFacebookUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AuthenticationViewModel(
	private val observeAuthenticationState: ObserveAuthenticationStateUseCase,
	private val signInWithExternalProvider: SignInWithExternalProviderUseCase,
	private val signInWithFacebook: SignInWithFacebookUseCase,
	private val signInWithGoogle: SignInWithGoogleUseCase,
	private val deleteAccount: DeleteAccountUseCase,
	private val signOut: SignOutUseCase,
	observeConsentState: ObserveConsentStateUseCase,
	private val showConsentForm: ShowConsentFormUseCase,
	private val trackEvent: TrackEventUseCase,
) : ViewModel() {

	val consentState: StateFlow<ConsentState> = observeConsentState()

	var authenticationState by mutableStateOf<AuthenticationState>(observeAuthenticationState().value)
		private set

	var message by mutableStateOf<String?>(null)
		private set

	var isBusy by mutableStateOf(false)
		private set

	init {
		viewModelScope.launch {
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
		viewModelScope.launch {
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
			if (result.getError() == null) {
				trackEvent(AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.GOOGLE))
			}
			isBusy = false
		}
	}

	fun onFacebookSignInResult(
		idToken: String?,
		email: String?,
		displayName: String,
		profileImageUrl: String?,
	) {
		if (idToken.isNullOrBlank()) {
			message = "Facebook sign-in was cancelled."
			return
		}
		viewModelScope.launch {
			isBusy = true
			val result = signInWithFacebook(
				FacebookAuthenticationProfile(
					idToken = idToken,
					email = email,
					displayName = displayName,
					profileImageUrl = profileImageUrl,
				),
			)
			message = result.getError()?.message
			if (result.getError() == null) {
				trackEvent(AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.FACEBOOK))
			}
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
		viewModelScope.launch {
			isBusy = true
			val signInResult = signInWithExternalProvider(profile)
			message = signInResult.getError()?.message
			if (signInResult.getError() == null) {
				trackEvent(AnalyticsEvent.SignInCompleted(method = provider.toAnalyticsAuthMethod()))
			}
			isBusy = false
		}
	}

	fun signOut() {
		viewModelScope.launch {
			isBusy = true
			signOut.invoke()
			trackEvent(AnalyticsEvent.SignOut)
			isBusy = false
		}
	}

	fun deleteAccount() {
		viewModelScope.launch {
			isBusy = true
			message = deleteAccount.invoke().getError()?.message
			isBusy = false
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

private fun AuthProvider.toAnalyticsAuthMethod(): String {
	return when (this) {
		AuthProvider.EMAIL -> AnalyticsAuthMethod.EMAIL
		AuthProvider.GOOGLE -> AnalyticsAuthMethod.GOOGLE
		AuthProvider.APPLE -> AnalyticsAuthMethod.APPLE
		AuthProvider.FACEBOOK -> AnalyticsAuthMethod.FACEBOOK
	}
}
