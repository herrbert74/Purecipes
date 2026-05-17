package app.purecipes.feature.auth.ui.registration

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import app.purecipes.shared.domain.model.PASSWORD_POLICY_SUPPORTING_TEXT
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RegistrationForm(
	displayName: String,
	email: String,
	emailError: String?,
	password: String,
	passwordError: String?,
	isBusy: Boolean,
	onDisplayNameChange: (String) -> Unit,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onSubmit: () -> Unit,
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
				value = displayName,
				onValueChange = onDisplayNameChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Display name") },
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
				keyboardActions = KeyboardActions(
					onNext = { emailFocusRequester.requestFocus() },
				),
			)
			OutlinedTextField(
				value = email,
				onValueChange = onEmailChange,
				modifier = Modifier
					.fillMaxWidth()
					.focusRequester(emailFocusRequester)
					.testTag(REGISTRATION_EMAIL_FIELD_TAG),
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
							modifier = Modifier.testTag(REGISTRATION_EMAIL_ERROR_TAG),
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
					.testTag(REGISTRATION_PASSWORD_FIELD_TAG),
				label = { Text("Password") },
				singleLine = true,
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
				keyboardActions = KeyboardActions(
					onDone = { focusManager.clearFocus() },
				),
				isError = passwordError != null,
				supportingText = when {
					passwordError != null -> {
						{
							Text(
								text = passwordError,
								modifier = Modifier.testTag(REGISTRATION_PASSWORD_ERROR_TAG),
							)
						}
					}

					else -> {
						{
							Text(
								text = PASSWORD_POLICY_SUPPORTING_TEXT,
								modifier = Modifier.testTag(REGISTRATION_PASSWORD_POLICY_SUPPORTING_TEXT_TAG),
							)
						}
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
			Button(
				modifier = Modifier
					.fillMaxWidth()
					.height(PurecipesTheme.space.xxl)
					.testTag(REGISTRATION_SUBMIT_TAG),
				onClick = onSubmit,
				enabled = !isBusy,
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(PurecipesTheme.space.m),
						strokeWidth = PurecipesTheme.space.quark,
					)
				} else {
					Text(text = "Create account")
				}
			}
		}
	}
}
