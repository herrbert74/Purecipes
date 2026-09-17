package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.apple.rememberAppleAuthState
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import kotlinx.coroutines.launch

@Composable
internal actual fun AppleAuthenticationButton(
	onAppleSignInResult: (Result<AppleAuthenticationProfile?>) -> Unit,
) {
	val coroutineScope = rememberCoroutineScope()
	val appleAuth = rememberAppleAuthState(
		linkAccount = false,
		onResult = { result ->
			coroutineScope.launch {
				onAppleSignInResult(result.toAppleAuthenticationProfileResult())
			}
		},
	)
	AppleSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
		onClick = { appleAuth.launch() },
	)
}
