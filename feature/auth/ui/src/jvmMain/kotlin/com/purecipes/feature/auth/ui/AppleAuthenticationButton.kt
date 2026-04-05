package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile

@Composable
internal actual fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	OutlinedButton(
		onClick = { onResult(Result.failure(IllegalStateException("Apple sign-in is not supported on JVM."))) },
		modifier = Modifier
			.fillMaxWidth()
			.height(52.dp),
	) {
		Text(text = "Continue with Apple")
	}
}
