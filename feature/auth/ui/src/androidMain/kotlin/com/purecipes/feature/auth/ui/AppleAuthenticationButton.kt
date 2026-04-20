package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.shared.ui.component.PurecipesButtonDefaults

@Composable
internal actual fun AppleAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	AppleButtonUiContainer(
		linkAccount = false,
		onResult = { result ->
			onResult(result.map { it?.toExternalAuthenticationProfile(AuthProvider.APPLE) })
		},
	) {
		AppleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) { this.onClick() }
	}
}
