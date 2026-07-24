package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.firebase.google.rememberFirebaseGoogleSignInState
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import kotlinx.coroutines.launch

@Composable
internal actual fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?) {
	remember(googleWebClientId) {
		googleWebClientId
			?.takeIf { it.isNotBlank() }
			?.let { GoogleAuthProvider.create(GoogleAuthCredentials(serverId = it)) }
	}
}

@Composable
internal actual fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (Result<GoogleAuthenticationProfile?>) -> Unit,
	onUnavailable: () -> Unit,
) {
	val coroutineScope = rememberCoroutineScope()
	if (isConfigured) {
		val googleSignIn = rememberFirebaseGoogleSignInState(
			linkAccount = false,
			filterByAuthorizedAccounts = false,
			onResult = { result ->
				coroutineScope.launch {
					onGoogleSignInResult(
						result.fold(
							onSuccess = { firebaseUser ->
								Result.success(firebaseUser?.toGoogleAuthenticationProfile())
							},
							onFailure = { error -> Result.failure(error) },
						),
					)
				}
			},
		)
		GoogleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = { googleSignIn.launch() },
		)
	} else {
		GoogleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = onUnavailable,
		)
	}
}
