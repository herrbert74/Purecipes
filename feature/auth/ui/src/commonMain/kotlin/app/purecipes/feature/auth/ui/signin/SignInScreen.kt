package app.purecipes.feature.auth.ui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun SignInScreen(
	initialEmail: String,
	showRegistrationSuccessMessage: Boolean,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SignInViewModel = assistedMetroViewModel<SignInViewModel, SignInViewModel.Factory> {
		create(
			initialEmail = initialEmail,
			showRegistrationSuccessMessage = showRegistrationSuccessMessage,
		)
	},
) {
	SignInScreenContent(
		email = viewModel.email,
		emailError = viewModel.emailError,
		password = viewModel.password,
		passwordError = viewModel.passwordError,
		isBusy = viewModel.isBusy,
		infoMessage = viewModel.infoMessage,
		showResendVerificationEmail = viewModel.showResendVerificationEmail,
		onEmailChange = viewModel::onEmailChange,
		onPasswordChange = viewModel::onPasswordChange,
		onSubmit = viewModel::submitSignIn,
		onResendVerificationEmail = viewModel::resendVerificationEmail,
		onForgotPassword = viewModel::sendPasswordResetEmail,
		onBack = onBack,
		modifier = modifier,
	)
}

@Composable
internal fun SignInScreenContent(
	email: String,
	emailError: String?,
	password: String,
	passwordError: String?,
	isBusy: Boolean,
	infoMessage: String?,
	showResendVerificationEmail: Boolean,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onSubmit: () -> Unit,
	onResendVerificationEmail: () -> Unit,
	onForgotPassword: () -> Unit,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Sign in",
						modifier = Modifier.testTag(SIGN_IN_SCREEN_TITLE_TAG),
					)
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
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
				infoMessage?.let { message ->
					Text(
						text = message,
						modifier = Modifier.testTag(SIGN_IN_INFO_MESSAGE_TAG),
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.primary,
					)
				}
				SignInForm(
					email = email,
					emailError = emailError,
					password = password,
					passwordError = passwordError,
					isBusy = isBusy,
					showResendVerificationEmail = showResendVerificationEmail,
					onEmailChange = onEmailChange,
					onPasswordChange = onPasswordChange,
					onSubmit = onSubmit,
					onResendVerificationEmail = onResendVerificationEmail,
					onForgotPassword = onForgotPassword,
				)
			}
		}
	}
}

@Preview(
	name = "Sign in screen light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SignInScreenLightPreview() {
	PurecipesTheme(darkTheme = false) {
		SignInScreenContent(
			email = "taylor@example.com",
			emailError = null,
			password = "",
			passwordError = null,
			isBusy = false,
			infoMessage = null,
			showResendVerificationEmail = false,
			onEmailChange = {},
			onPasswordChange = {},
			onSubmit = {},
			onResendVerificationEmail = {},
			onForgotPassword = {},
			onBack = {},
		)
	}
}

@Preview(
	name = "Sign in screen registration success",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SignInScreenRegistrationSuccessPreview() {
	PurecipesTheme(darkTheme = false) {
		SignInScreenContent(
			email = "taylor@example.com",
			emailError = null,
			password = "",
			passwordError = null,
			isBusy = false,
			infoMessage = "Registration successful. Please check your email to verify your account.",
			showResendVerificationEmail = true,
			onEmailChange = {},
			onPasswordChange = {},
			onSubmit = {},
			onResendVerificationEmail = {},
			onForgotPassword = {},
			onBack = {},
		)
	}
}

@Preview(
	name = "Sign in screen dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun SignInScreenDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		SignInScreenContent(
			email = "taylor@example.com",
			emailError = null,
			password = "secret",
			passwordError = null,
			isBusy = false,
			infoMessage = "Password reset email sent. Please check your inbox.",
			showResendVerificationEmail = false,
			onEmailChange = {},
			onPasswordChange = {},
			onSubmit = {},
			onResendVerificationEmail = {},
			onForgotPassword = {},
			onBack = {},
		)
	}
}
