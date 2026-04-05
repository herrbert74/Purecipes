package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile

@Composable
internal actual fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	AppleSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(52.dp),
		onClick = { onResult(Result.failure(IllegalStateException("Apple sign-in is not supported on this platform."))) },
	)
}
