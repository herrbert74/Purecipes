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
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.feature.auth.ui.authentication.button.InitializeGoogleAuthenticationProvider
import app.purecipes.feature.auth.ui.profile.SignedInContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun AuthenticationScreen(
	observeConsentState: ObserveConsentStateUseCase,
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	resendEmailVerification: ResendEmailVerificationUseCase,
	signInWithExternalProvider: SignInWithExternalProviderUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	showConsentForm: ShowConsentFormUseCase,
	signOut: SignOutUseCase,
	onOpenSettings: () -> Unit,
	googleWebClientId: String?,
	modifier: Modifier = Modifier,
	initializeGoogleAuthenticationProvider: @Composable (String?) -> Unit =
		{ InitializeGoogleAuthenticationProvider(it) },
	authenticationProviderButtons: @Composable (
		isGoogleConfigured: Boolean,
		onEmailProviderClick: () -> Unit,
		onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
		onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
		onGoogleUnavailableClick: () -> Unit,
	) -> Unit = {
		isGoogleConfigured,
		onEmailProviderClick,
		onExternalProviderSignInResult,
		onGoogleSignInResult,
		onGoogleUnavailableClick,
		->
		AuthenticationProviderButtons(
			isGoogleConfigured = isGoogleConfigured,
			onEmailProviderClick = onEmailProviderClick,
			onExternalProviderSignInResult = onExternalProviderSignInResult,
			onGoogleSignInResult = onGoogleSignInResult,
			onGoogleUnavailableClick = onGoogleUnavailableClick,
		)
	},
) {
	initializeGoogleAuthenticationProvider(googleWebClientId)
	val consentState by observeConsentState().collectAsState()
	val viewModel = authenticationViewModel(
		observeAuthenticationState = observeAuthenticationState,
		signInWithEmail = signInWithEmail,
		registerWithEmail = registerWithEmail,
		resendEmailVerification = resendEmailVerification,
		signInWithExternalProvider = signInWithExternalProvider,
		signInWithGoogle = signInWithGoogle,
		signOut = signOut,
	)

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
				when (val state = viewModel.authenticationState) {
					AuthenticationState.SignedOut -> SignedOutContent(
						consentState = consentState,
						emailAuthenticationMode = viewModel.emailAuthenticationMode,
						isEmailFormVisible = viewModel.isEmailFormVisible,
						displayName = viewModel.displayName,
						email = viewModel.email,
						emailError = viewModel.emailError,
						password = viewModel.password,
						passwordError = viewModel.passwordError,
						isBusy = viewModel.isBusy,
						showResendVerificationEmail = viewModel.showResendVerificationEmail,
						isGoogleConfigured = !googleWebClientId.isNullOrBlank(),
						onEmailProviderClick = viewModel::onEmailProviderSelected,
						onEmailAuthenticationModeChange = viewModel::onEmailAuthenticationModeSelected,
						onDisplayNameChange = viewModel::onDisplayNameChange,
						onEmailChange = viewModel::onEmailChange,
						onPasswordChange = viewModel::onPasswordChange,
						onEmailAuthenticationSubmit = viewModel::submitEmailAuthentication,
						onResendVerificationEmail = viewModel::resendVerificationEmail,
						onExternalProviderSignInResult = viewModel::onExternalProviderSignInResult,
						onGoogleSignInResult = viewModel::onGoogleSignInResult,
						onManagePrivacySettings = { showConsentForm() },
						onGoogleUnavailableClick = viewModel::onGoogleUnavailableSelected,
						authenticationProviderButtons = authenticationProviderButtons,
					)

					is AuthenticationState.SignedIn -> SignedInContent(
						consentState = consentState,
						user = state.user,
						isBusy = viewModel.isBusy,
						onManagePrivacySettings = { showConsentForm() },
						onSignOut = viewModel::signOut,
					)
				}

				viewModel.message?.let { message ->
					ErrorText(
						text = message,
						modifier = Modifier.testTag(AUTH_ERROR_MESSAGE_TAG),
					)
				}
				viewModel.infoMessage?.let { infoMessage ->
					Text(
						text = infoMessage,
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}
