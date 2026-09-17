package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton

@Composable
internal actual fun AppleAuthenticationButton(
	onAppleSignInResult: (Result<AppleAuthenticationProfile?>) -> Unit,
) {
	AppleSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
		onClick = {
			onAppleSignInResult(
				Result.failure(IllegalStateException("Apple sign-in is not supported on this platform.")),
			)
		},
	)
}
