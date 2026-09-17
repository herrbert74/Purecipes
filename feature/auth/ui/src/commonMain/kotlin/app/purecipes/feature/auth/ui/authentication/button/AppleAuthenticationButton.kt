package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.runtime.Composable
import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile

@Composable
internal expect fun AppleAuthenticationButton(
	onAppleSignInResult: (Result<AppleAuthenticationProfile?>) -> Unit,
)
