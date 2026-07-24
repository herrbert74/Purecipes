package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.runtime.Composable
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile

@Composable
internal expect fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?)

@Composable
internal expect fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (Result<GoogleAuthenticationProfile?>) -> Unit,
	onUnavailable: () -> Unit,
)
