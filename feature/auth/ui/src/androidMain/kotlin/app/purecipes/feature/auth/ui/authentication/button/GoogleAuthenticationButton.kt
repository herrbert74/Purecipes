package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
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
	onGoogleSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
) {
	if (LocalInspectionMode.current) {
		OutlinedButton(
			onClick = {},
			enabled = false,
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) {
			Text(text = "Continue with Google")
		}
		return
	}
	val coroutineScope = rememberCoroutineScope()
	if (isConfigured) {
		GoogleButtonUiContainerFirebase(
			onResult = { result ->
				val failure = result.exceptionOrNull()
				if (failure != null) {
					onGoogleSignInResult(null, null, "", null)
					return@GoogleButtonUiContainerFirebase
				}
				val firebaseUser = result.getOrNull()
				if (firebaseUser == null) {
					onGoogleSignInResult(null, null, "", null)
					return@GoogleButtonUiContainerFirebase
				}
				coroutineScope.launch {
					val profile = firebaseUser.toGoogleAuthenticationProfile()
					onGoogleSignInResult(
						profile?.idToken,
						profile?.email,
						profile?.displayName.orEmpty(),
						profile?.profileImageUrl,
					)
				}
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
