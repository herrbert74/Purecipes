package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults

@Composable
internal actual fun AppleAuthenticationButton(
	onAppleSignInResult: (Result<AppleAuthenticationProfile?>) -> Unit,
) {
	OutlinedButton(
		onClick = {
			onAppleSignInResult(
				Result.failure(IllegalStateException("Apple sign-in is not supported on JVM.")),
			)
		},
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
	) {
		Text(text = "Continue with Apple")
	}
}
