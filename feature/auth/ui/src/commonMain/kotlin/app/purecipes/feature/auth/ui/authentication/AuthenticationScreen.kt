package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.ui.authentication.button.InitializeGoogleAuthenticationProvider
import app.purecipes.feature.auth.ui.profile.SignedInContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun AuthenticationScreen(
	onOpenSettings: () -> Unit,
	onNavigateToEmailRegistration: () -> Unit,
	onNavigateToSignIn: () -> Unit,
	googleWebClientId: String?,
	modifier: Modifier = Modifier,
	viewModel: AuthenticationViewModel = metroViewModel(),
	initializeGoogleAuthenticationProvider: @Composable (String?) -> Unit =
		{ InitializeGoogleAuthenticationProvider(it) },
	authenticationProviderButtons: @Composable (
		isGoogleConfigured: Boolean,
		onEmailProviderClick: () -> Unit,
		onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
		onFacebookSignInResult: (String?, String?, String, String?) -> Unit,
		onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
		onGoogleUnavailableClick: () -> Unit,
	) -> Unit = {
			isGoogleConfigured,
			onEmailProviderClick,
			onExternalProviderSignInResult,
			onFacebookSignInResult,
			onGoogleSignInResult,
			onGoogleUnavailableClick,
		->
		AuthenticationProviderButtons(
			isGoogleConfigured = isGoogleConfigured,
			onEmailProviderClick = onEmailProviderClick,
			onExternalProviderSignInResult = onExternalProviderSignInResult,
			onFacebookSignInResult = onFacebookSignInResult,
			onGoogleSignInResult = onGoogleSignInResult,
			onGoogleUnavailableClick = onGoogleUnavailableClick,
		)
	},
) {
	initializeGoogleAuthenticationProvider(googleWebClientId)
	val consentState by viewModel.consentState.collectAsState()

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Account", modifier = Modifier.testTag(AUTH_SCREEN_TITLE_TAG)) },
				actions = {
					IconButton(onClick = onOpenSettings) {
						Icon(
							imageVector = Icons.Filled.Settings,
							contentDescription = "Open settings",
						)
					}
				},
			)
		},
	) { innerPadding ->
		Surface(modifier = Modifier.fillMaxSize()) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				viewModel.message?.let { message ->
					ErrorText(
						text = message,
						modifier = Modifier.testTag(AUTH_ERROR_MESSAGE_TAG),
					)
				}
				when (val state = viewModel.authenticationState) {
					AuthenticationState.SignedOut -> SignedOutContent(
						consentState = consentState,
						isGoogleConfigured = !googleWebClientId.isNullOrBlank(),
						onEmailRegistrationClick = onNavigateToEmailRegistration,
						onSignInClick = onNavigateToSignIn,
						onExternalProviderSignInResult = viewModel::onExternalProviderSignInResult,
						onFacebookSignInResult = viewModel::onFacebookSignInResult,
						onGoogleSignInResult = viewModel::onGoogleSignInResult,
						onManagePrivacySettings = viewModel::onManagePrivacySettingsClick,
						onGoogleUnavailableClick = viewModel::onGoogleUnavailableSelected,
						authenticationProviderButtons = authenticationProviderButtons,
					)

					is AuthenticationState.SignedIn -> SignedInContent(
						consentState = consentState,
						user = state.user,
						isBusy = viewModel.isBusy,
						onManagePrivacySettings = viewModel::onManagePrivacySettingsClick,
						onSignOut = viewModel::signOut,
						onDeleteAccount = viewModel::deleteAccount,
					)
				}
			}
		}
	}
}

@Preview(
	name = "Account screen error light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun AuthenticationScreenErrorPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(title = { Text(text = "Account") })
			},
		) { innerPadding ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				ErrorText(text = "Google sign-in is not configured on this build.")
				SignedOutContent(
					consentState = ConsentState.OBTAINED,
					isGoogleConfigured = false,
					onEmailRegistrationClick = {},
					onSignInClick = {},
					onExternalProviderSignInResult = { _, _ -> },
					onFacebookSignInResult = { _, _, _, _ -> },
					onGoogleSignInResult = { _, _, _, _ -> },
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
