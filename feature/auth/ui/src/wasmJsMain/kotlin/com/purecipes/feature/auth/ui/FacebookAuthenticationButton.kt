package com.purecipes.feature.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.shared.ui.component.PurecipesButtonDefaults

@Composable
internal actual fun FacebookAuthenticationButton(
	onResult: (Result<ExternalAuthenticationProfile?>) -> Unit,
) {
	FacebookSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
		onClick = {
			onResult(Result.failure(IllegalStateException("Facebook sign-in is not supported on this platform.")))
		},
	)
}
