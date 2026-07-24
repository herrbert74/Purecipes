package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.ui.profile.PrivacySettingsContent
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun SignedOutContent(
	consentState: ConsentState,
	isGoogleConfigured: Boolean,
	onEmailRegistrationClick: () -> Unit,
	onSignInClick: () -> Unit,
	onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
	onFacebookSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleSignInResult: (Result<GoogleAuthenticationProfile?>) -> Unit,
	onManagePrivacySettings: () -> Unit,
	onGoogleUnavailableClick: () -> Unit,
	authenticationProviderButtons: @Composable (
		isGoogleConfigured: Boolean,
		onEmailProviderClick: () -> Unit,
		onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
		onFacebookSignInResult: (String?, String?, String, String?) -> Unit,
		onGoogleSignInResult: (Result<GoogleAuthenticationProfile?>) -> Unit,
		onGoogleUnavailableClick: () -> Unit,
	) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		Text(
			text = "Choose how you want to sign in. Email registration asks for a display name shown on your profile.",
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		authenticationProviderButtons(
			isGoogleConfigured,
			onEmailRegistrationClick,
			onExternalProviderSignInResult,
			onFacebookSignInResult,
			onGoogleSignInResult,
			onGoogleUnavailableClick,
		)
		TextButton(onClick = onSignInClick) {
			Text(text = "Or, sign in")
		}
		HorizontalDivider()
		PrivacySettingsContent(
			consentState = consentState,
			onManagePrivacySettings = onManagePrivacySettings,
		)
	}
}

@Preview(
	name = "Signed out content light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SignedOutContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(title = { Text(text = "Account") })
			},
		) { innerPadding ->
			Column(
				modifier = Modifier
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
			) {
				SignedOutContent(
					consentState = ConsentState.OBTAINED,
					isGoogleConfigured = true,
					onEmailRegistrationClick = {},
					onSignInClick = {},
					onExternalProviderSignInResult = { _, _ -> },
					onFacebookSignInResult = { _, _, _, _ -> },
					onGoogleSignInResult = {},
					onManagePrivacySettings = {},
					onGoogleUnavailableClick = {},
					authenticationProviderButtons = {
							configured,
							onEmail,
							onExternal,
							onFacebook,
							onGoogle,
							onUnavailable,
						->
						AuthenticationProviderButtons(
							isGoogleConfigured = configured,
							onEmailProviderClick = onEmail,
							onExternalProviderSignInResult = onExternal,
							onFacebookSignInResult = onFacebook,
							onGoogleSignInResult = onGoogle,
							onGoogleUnavailableClick = onUnavailable,
						)
					},
				)
			}
		}
	}
}
