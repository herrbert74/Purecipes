package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.ui.profile.PrivacySettingsContent
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun SignedOutContent(
	consentState: ConsentState,
	emailAuthenticationMode: EmailAuthenticationMode,
	isEmailFormVisible: Boolean,
	firstName: String,
	familyName: String,
	email: String,
	emailError: String?,
	password: String,
	passwordError: String?,
	isBusy: Boolean,
	showResendVerificationEmail: Boolean,
	isGoogleConfigured: Boolean,
	onEmailProviderClick: () -> Unit,
	onEmailAuthenticationModeChange: (EmailAuthenticationMode) -> Unit,
	onFirstNameChange: (String) -> Unit,
	onFamilyNameChange: (String) -> Unit,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onEmailAuthenticationSubmit: () -> Unit,
	onResendVerificationEmail: () -> Unit,
	onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onManagePrivacySettings: () -> Unit,
	onGoogleUnavailableClick: () -> Unit,
	authenticationProviderButtons: @Composable (
		isGoogleConfigured: Boolean,
		onEmailProviderClick: () -> Unit,
		onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
		onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
		onGoogleUnavailableClick: () -> Unit,
	) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		Text(
			text = """Choose how you want to sign in.
				| Email registration uses your first and family name as the display name.
			""".trimIndent(),
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		authenticationProviderButtons(
			isGoogleConfigured,
			onEmailProviderClick,
			onExternalProviderSignInResult,
			onGoogleSignInResult,
			onGoogleUnavailableClick,
		)
		if (isEmailFormVisible) {
			EmailAuthenticationForm(
				emailAuthenticationMode = emailAuthenticationMode,
				firstName = firstName,
				familyName = familyName,
				email = email,
				emailError = emailError,
				password = password,
				passwordError = passwordError,
				isBusy = isBusy,
				showResendVerificationEmail = showResendVerificationEmail,
				onEmailAuthenticationModeChange = onEmailAuthenticationModeChange,
				onFirstNameChange = onFirstNameChange,
				onFamilyNameChange = onFamilyNameChange,
				onEmailChange = onEmailChange,
				onPasswordChange = onPasswordChange,
				onEmailAuthenticationSubmit = onEmailAuthenticationSubmit,
				onResendVerificationEmail = onResendVerificationEmail,
			)
		}
		HorizontalDivider()
		PrivacySettingsContent(
			consentState = consentState,
			onManagePrivacySettings = onManagePrivacySettings,
		)
	}
}
