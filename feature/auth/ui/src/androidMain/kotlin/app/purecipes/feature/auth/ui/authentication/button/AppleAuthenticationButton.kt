package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton

@Composable
internal actual fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	AppleButtonUiContainer(
		linkAccount = false,
		onResult = { result ->
			onResult(result.map { it?.toExternalAuthenticationProfile(AuthProvider.APPLE) })
		},
	) {
		AppleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) { this.onClick() }
	}
}
