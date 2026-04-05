package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal actual fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?) = Unit

@Composable
internal actual fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
) {
	OutlinedButton(
		onClick = onUnavailable,
		modifier = Modifier
			.fillMaxWidth()
			.height(52.dp),
	) {
		Text(text = if (isConfigured) "Continue with Google" else "Google sign-in unavailable")
	}
}
