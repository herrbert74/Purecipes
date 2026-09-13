package app.purecipes.feature.auth.ui.signin

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun ForgotPasswordConfirmDialog(
	email: String,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	AlertDialog(
		modifier = Modifier.testTag(SIGN_IN_FORGOT_PASSWORD_DIALOG_TAG),
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = onConfirm,
				modifier = Modifier.testTag(SIGN_IN_FORGOT_PASSWORD_DIALOG_CONFIRM_TAG),
			) {
				Text(text = "Send email")
			}
		},
		dismissButton = {
			TextButton(
				onClick = onDismiss,
				modifier = Modifier.testTag(SIGN_IN_FORGOT_PASSWORD_DIALOG_DISMISS_TAG),
			) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "Reset password?") },
		text = { Text(text = "Send a password reset email to $email?") },
	)
}

@Preview(
	name = "Forgot password confirm dialog light",
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun ForgotPasswordConfirmDialogLightPreview() {
	PurecipesTheme(darkTheme = false) {
		ForgotPasswordConfirmDialog(
			email = "taylor@example.com",
			onDismiss = {},
			onConfirm = {},
		)
	}
}

@Preview(
	name = "Forgot password confirm dialog dark",
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun ForgotPasswordConfirmDialogDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		ForgotPasswordConfirmDialog(
			email = "taylor@example.com",
			onDismiss = {},
			onConfirm = {},
		)
	}
}
