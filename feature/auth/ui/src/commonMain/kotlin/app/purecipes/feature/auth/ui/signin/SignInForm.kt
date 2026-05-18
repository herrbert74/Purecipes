package app.purecipes.feature.auth.ui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun SignInForm(
	email: String,
	emailError: String?,
	password: String,
	passwordError: String?,
	isBusy: Boolean,
	showResendVerificationEmail: Boolean,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onSubmit: () -> Unit,
	onResendVerificationEmail: () -> Unit,
	onForgotPassword: () -> Unit,
) {
	var isPasswordVisible by remember { mutableStateOf(false) }
	val emailFocusRequester = remember { FocusRequester() }
	val passwordFocusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	Surface(
		modifier = Modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			OutlinedTextField(
				value = email,
				onValueChange = onEmailChange,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(emailFocusRequester)
					.testTag(SIGN_IN_EMAIL_FIELD_TAG),
				label = { Text("Email") },
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
				keyboardActions = KeyboardActions(
					onNext = { passwordFocusRequester.requestFocus() },
				),
				isError = emailError != null,
				supportingText = emailError?.let { error ->
					{
						Text(
							text = error,
							modifier = Modifier.testTag(SIGN_IN_EMAIL_ERROR_TAG),
						)
					}
				},
			)
			OutlinedTextField(
				value = password,
				onValueChange = onPasswordChange,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(passwordFocusRequester)
					.testTag(SIGN_IN_PASSWORD_FIELD_TAG),
				label = { Text("Password") },
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(
					onDone = { focusManager.clearFocus() },
				),
				isError = passwordError != null,
				supportingText = passwordError?.let { error ->
					{
						Text(
							text = error,
							modifier = Modifier.testTag(SIGN_IN_PASSWORD_ERROR_TAG),
						)
					}
				},
				visualTransformation = if (isPasswordVisible) {
					VisualTransformation.None
				} else {
					PasswordVisualTransformation()
				},
				trailingIcon = {
					IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
						Icon(
							imageVector = if (isPasswordVisible) {
								Icons.Filled.VisibilityOff
							} else {
								Icons.Filled.Visibility
							},
							contentDescription = if (isPasswordVisible) {
								"Hide password"
							} else {
								"Show password"
							},
						)
					}
				},
			)
			TextButton(
				modifier = Modifier.testTag(SIGN_IN_FORGOT_PASSWORD_TAG),
				onClick = onForgotPassword,
				enabled = !isBusy,
			) {
				Text(text = "Forgot password?")
			}
			Button(
				modifier = Modifier
					.fillMaxWidth()
					.height(PurecipesTheme.space.xxl),
				onClick = onSubmit,
				enabled = !isBusy,
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(PurecipesTheme.space.m),
						strokeWidth = PurecipesTheme.space.quark,
					)
				} else {
					Text(text = "Sign in")
				}
			}
			if (showResendVerificationEmail) {
				OutlinedButton(
					modifier = Modifier
						.fillMaxWidth()
						.testTag(SIGN_IN_RESEND_VERIFICATION_TAG),
					onClick = onResendVerificationEmail,
					enabled = !isBusy,
				) {
					Text(text = "Resend verification email")
				}
			}
		}
	}
}
