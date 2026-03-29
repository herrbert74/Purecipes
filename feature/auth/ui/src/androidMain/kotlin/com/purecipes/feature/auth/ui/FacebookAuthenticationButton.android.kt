package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.facebook.FacebookButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile

@Composable
internal actual fun FacebookAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	FacebookButtonUiContainerFirebase(
		linkAccount = false,
		onResult = { result ->
			onResult(result.map { it?.toExternalAuthenticationProfile(AuthProvider.FACEBOOK) })
		},
	) {
		FacebookSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
		) { this.onClick() }
	}
}
