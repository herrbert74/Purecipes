package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.rememberGoogleAuthState
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.auth.FirebaseUser
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
	val coroutineScope = rememberCoroutineScope()
	if (isConfigured) {
		val googleAuth = rememberGoogleAuthState(
			linkAccount = false,
			filterByAuthorizedAccounts = false,
			onResult = { result ->
				val firebaseUser = result.getOrNull()?.raw as? FirebaseUser
				if (firebaseUser == null) {
					onGoogleSignInResult(null, null, "", null)
				} else {
					coroutineScope.launch {
						val profile = firebaseUser.toGoogleAuthenticationProfile()
						onGoogleSignInResult(
							profile?.idToken,
							profile?.email,
							profile?.displayName.orEmpty(),
							profile?.profileImageUrl,
						)
					}
				}
			},
		)
		GoogleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = { googleAuth.launch() },
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
