package app.purecipes.feature.auth.ui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import app.purecipes.shared.ui.component.atom.PurecipesOutlinedButton
import app.purecipes.shared.ui.component.atom.PurecipesTextButton
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.hide_password
import purecipes.shared.ui.generated.resources.show_password

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
					.semantics {
						if (emailError != null) {
							error(emailError)
						}
					}
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
					.semantics {
						if (passwordError != null) {
							error(passwordError)
						}
					}
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
								AppIcons.VisibilityOff
							} else {
								AppIcons.Visibility
							},
							contentDescription = stringResource(
								if (isPasswordVisible) {
									Res.string.hide_password
								} else {
									Res.string.show_password
								},
							),
						)
					}
				},
			)
			PurecipesTextButton(
				text = "Forgot password?",
				onClick = onForgotPassword,
				enabled = !isBusy,
				modifier = Modifier.testTag(SIGN_IN_FORGOT_PASSWORD_TAG),
			)
			Button(
				modifier = Modifier
					.fillMaxWidth()
					.defaultMinSize(minHeight = PurecipesTheme.space.xxl),
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
				PurecipesOutlinedButton(
					text = "Resend verification email",
					onClick = onResendVerificationEmail,
					enabled = !isBusy,
					modifier = Modifier.testTag(SIGN_IN_RESEND_VERIFICATION_TAG),
				)
			}
		}
	}
}
