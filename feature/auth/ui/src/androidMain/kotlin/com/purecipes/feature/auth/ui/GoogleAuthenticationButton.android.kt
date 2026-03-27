package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton

@Composable
internal actual fun InitializeGoogleAuthenticationProvider(googleWebClientId: String?) {
	remember(googleWebClientId) {
		googleWebClientId
			?.takeIf { it.isNotBlank() }
			?.let { GoogleAuthProvider.create(GoogleAuthCredentials(serverId = it)) }
	}
}

@Composable
internal actual fun GoogleAuthenticationButton(
	isConfigured: Boolean,
	onGoogleSignInResult: (email: String?, displayName: String, profileImageUrl: String?) -> Unit,
	onUnavailable: () -> Unit,
) {
	if (isConfigured) {
		GoogleButtonUiContainer(
			onGoogleSignInResult = { googleUser ->
				onGoogleSignInResult(
					googleUser?.email,
					googleUser?.displayName.orEmpty(),
					googleUser?.profilePicUrl,
				)
			},
		) {
			GoogleSignInButton(
				modifier = Modifier
					.fillMaxWidth()
					.height(52.dp),
			) { this.onClick() }
		}
	} else {
		GoogleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			onClick = onUnavailable,
		)
	}
}
