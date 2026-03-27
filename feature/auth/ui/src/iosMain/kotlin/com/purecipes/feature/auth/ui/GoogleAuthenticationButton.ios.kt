package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@Composable
internal actual fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?) = Unit

@Composable
internal actual fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
) {
	GoogleSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(52.dp),
		onClick = onUnavailable,
	)
}
