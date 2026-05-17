package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.ui.authentication.button.AppleAuthenticationButton
import app.purecipes.feature.auth.ui.authentication.button.FacebookAuthenticationButton
import app.purecipes.feature.auth.ui.authentication.button.GoogleAuthenticationButton
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun AuthenticationProviderButtons(
	isGoogleConfigured: Boolean,
	onEmailProviderClick: () -> Unit,
	onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleUnavailableClick: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = onEmailProviderClick,
		) {
			Text(text = "Continue with email")
		}
		GoogleAuthenticationButton(
			isConfigured = isGoogleConfigured,
			onGoogleSignInResult = onGoogleSignInResult,
			onUnavailable = onGoogleUnavailableClick,
		)
		AppleAuthenticationButton(
			onResult = { result -> onExternalProviderSignInResult(AuthProvider.APPLE, result) },
		)
		FacebookAuthenticationButton(
			onResult = { result -> onExternalProviderSignInResult(AuthProvider.FACEBOOK, result) },
		)
	}
}
