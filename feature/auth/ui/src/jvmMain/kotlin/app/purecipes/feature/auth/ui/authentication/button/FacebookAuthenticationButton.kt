package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.component.PurecipesButtonDefaults

@Composable
internal actual fun FacebookAuthenticationButton(
	onFacebookSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
) {
	OutlinedButton(
		onClick = { onFacebookSignInResult(null, null, "", null) },
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
	) {
		Text(text = "Continue with Facebook")
	}
}
