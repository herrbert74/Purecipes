package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.firebase.facebook.FacebookButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton

@Composable
internal actual fun FacebookAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	FacebookButtonUiContainerFirebase(
		linkAccount = false,
		onResult = { result ->
			onResult(result.map { it?.toExternalAuthenticationProfile(AuthProvider.FACEBOOK) })
		},
	) {
		FacebookSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) { this.onClick() }
	}
}
