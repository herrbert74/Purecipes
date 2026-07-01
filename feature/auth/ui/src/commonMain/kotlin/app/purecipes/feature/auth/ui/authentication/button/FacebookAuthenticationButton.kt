package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.runtime.Composable

@Composable
internal expect fun FacebookAuthenticationButton(
	onFacebookSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
)
