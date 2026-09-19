package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.ui.authentication.button.AppleAuthenticationButton
import app.purecipes.feature.auth.ui.authentication.button.FacebookAuthenticationButton
import app.purecipes.feature.auth.ui.authentication.button.GoogleAuthenticationButton
import app.purecipes.feature.auth.ui.authentication.button.isAppleSignInAvailable
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun AuthenticationProviderButtons(
	isGoogleConfigured: Boolean,
	onAppleSignInResult: (Result<AppleAuthenticationProfile?>) -> Unit,
	onFacebookSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleSignInResult: (Result<GoogleAuthenticationProfile?>) -> Unit,
	onGoogleUnavailableClick: () -> Unit,
) {
	if (LocalInspectionMode.current) {
		PreviewAuthenticationProviderButtons()
		return
	}
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		GoogleAuthenticationButton(
			isConfigured = isGoogleConfigured,
			onGoogleSignInResult = onGoogleSignInResult,
			onUnavailable = onGoogleUnavailableClick,
		)
		if (isAppleSignInAvailable) {
			AppleAuthenticationButton(
				onAppleSignInResult = onAppleSignInResult,
			)
		}
		FacebookAuthenticationButton(
			onFacebookSignInResult = onFacebookSignInResult,
		)
	}
}
