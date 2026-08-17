package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.rememberGoogleSignInState
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

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
	if (isConfigured) {
		val googleSignIn = rememberGoogleSignInState(
			onResult = { result ->
				onGoogleSignInResult(
					result.map { googleUser ->
						GoogleAuthenticationProfile(
							idToken = googleUser.idToken,
							email = googleUser.email,
							displayName = googleUser.displayName.orEmpty(),
							profileImageUrl = googleUser.profilePicUrl,
						)
					},
				)
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
