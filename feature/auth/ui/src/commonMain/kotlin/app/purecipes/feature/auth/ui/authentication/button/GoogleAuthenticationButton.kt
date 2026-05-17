package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.runtime.Composable

@Composable
internal expect fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?)

@Composable
internal expect fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
)
