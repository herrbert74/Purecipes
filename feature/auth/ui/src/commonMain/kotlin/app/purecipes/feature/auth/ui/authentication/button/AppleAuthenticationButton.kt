package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.runtime.Composable
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile

@Composable
internal expect fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
)
