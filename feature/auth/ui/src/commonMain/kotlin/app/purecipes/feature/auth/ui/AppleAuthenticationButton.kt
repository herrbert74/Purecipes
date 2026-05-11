package app.purecipes.feature.auth.ui

import androidx.compose.runtime.Composable
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile

@Composable
internal expect fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
)
