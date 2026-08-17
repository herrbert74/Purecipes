package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.apple.rememberAppleAuthState
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton

@Composable
internal actual fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	if (LocalInspectionMode.current) {
		OutlinedButton(
			onClick = {},
			enabled = false,
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) {
			Text(text = "Continue with Apple")
		}
		return
	}
	val appleAuth = rememberAppleAuthState(
		linkAccount = false,
		onResult = { result ->
			val mapped: Result<ExternalAuthenticationProfile?> = result.map { user ->
				user.toExternalAuthenticationProfile(AuthProvider.APPLE)
			}
			onResult(mapped)
		},
	)
	AppleSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
		onClick = { appleAuth.launch() },
	)
}
