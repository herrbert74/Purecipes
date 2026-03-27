package com.purecipes.feature.auth.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?)

@Composable
internal expect fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
)
