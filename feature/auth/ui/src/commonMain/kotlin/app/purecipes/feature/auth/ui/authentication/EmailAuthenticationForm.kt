package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import app.purecipes.shared.domain.model.PASSWORD_POLICY_SUPPORTING_TEXT
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun EmailAuthenticationForm(
	emailAuthenticationMode: EmailAuthenticationMode,
	firstName: String,
	familyName: String,
	email: String,
	emailError: String?,
	password: String,
	passwordError: String?,
	isBusy: Boolean,
	showResendVerificationEmail: Boolean,
	onEmailAuthenticationModeChange: (EmailAuthenticationMode) -> Unit,
	onFirstNameChange: (String) -> Unit,
	onFamilyNameChange: (String) -> Unit,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onEmailAuthenticationSubmit: () -> Unit,
	onResendVerificationEmail: () -> Unit,
) {
	Surface(
		modifier = Modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				OutlinedButton(
					modifier = Modifier.weight(1f),
					onClick = { onEmailAuthenticationModeChange(EmailAuthenticationMode.SIGN_IN) },
				) {
					Text(text = "Sign in")
				}
				Button(
					modifier = Modifier.weight(1f),
					onClick = { onEmailAuthenticationModeChange(EmailAuthenticationMode.REGISTER) },
					enabled = emailAuthenticationMode != EmailAuthenticationMode.REGISTER,
				) {
					Text(text = "Register")
				}
			}
			if (emailAuthenticationMode == EmailAuthenticationMode.REGISTER) {
				OutlinedTextField(
					value = firstName,
					onValueChange = onFirstNameChange,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("First name") },
					singleLine = true,
				)
				OutlinedTextField(
					value = familyName,
					onValueChange = onFamilyNameChange,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("Family name") },
					singleLine = true,
				)
			}
			OutlinedTextField(
				value = email,
				onValueChange = onEmailChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(AUTH_EMAIL_FIELD_TAG),
				label = { Text("Email") },
				singleLine = true,
				isError = emailError != null,
				supportingText = emailError?.let { error ->
					{
						Text(
							text = error,
							modifier = Modifier.testTag(AUTH_EMAIL_ERROR_TAG),
						)
					}
				},
			)
			OutlinedTextField(
				value = password,
				onValueChange = onPasswordChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(AUTH_PASSWORD_FIELD_TAG),
				label = { Text("Password") },
				singleLine = true,
				isError = passwordError != null,
				supportingText = when {
					passwordError != null -> {
						{
							Text(
								text = passwordError,
								modifier = Modifier.testTag(AUTH_PASSWORD_ERROR_TAG),
							)
						}
					}
					emailAuthenticationMode == EmailAuthenticationMode.REGISTER -> {
						{
							Text(
								text = PASSWORD_POLICY_SUPPORTING_TEXT,
								modifier = Modifier.testTag(AUTH_PASSWORD_POLICY_SUPPORTING_TEXT_TAG),
							)
						}
					}
					else -> null
				},
				visualTransformation = PasswordVisualTransformation(),
			)
			Button(
				modifier = Modifier
					.fillMaxWidth()
					.height(PurecipesTheme.space.xxl),
				onClick = onEmailAuthenticationSubmit,
				enabled = !isBusy,
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(PurecipesTheme.space.m),
						strokeWidth = PurecipesTheme.space.quark,
					)
				} else {
					Text(
						text = if (emailAuthenticationMode == EmailAuthenticationMode.REGISTER) {
							"Create account"
						} else {
							"Sign in"
						},
					)
				}
			}
			if (showResendVerificationEmail && emailAuthenticationMode == EmailAuthenticationMode.SIGN_IN) {
				OutlinedButton(
					modifier = Modifier
						.fillMaxWidth()
						.testTag(AUTH_RESEND_VERIFICATION_TAG),
					onClick = onResendVerificationEmail,
					enabled = !isBusy,
				) {
					Text(text = "Resend verification email")
				}
			}
		}
	}
}
