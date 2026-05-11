package app.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import app.purecipes.shared.ui.component.PurecipesButtonDefaults

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
	onGoogleSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
) {
	if (isConfigured) {
		GoogleButtonUiContainer(
			onGoogleSignInResult = { googleUser ->
				onGoogleSignInResult(
					googleUser?.idToken,
					googleUser?.email,
					googleUser?.displayName.orEmpty(),
					googleUser?.profilePicUrl,
				)
			},
		) {
			GoogleSignInButton(
				modifier = Modifier
					.fillMaxWidth()
					.height(PurecipesButtonDefaults.providerButtonHeight),
			) { this.onClick() }
		}
	} else {
		GoogleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = onUnavailable,
		)
	}
}
