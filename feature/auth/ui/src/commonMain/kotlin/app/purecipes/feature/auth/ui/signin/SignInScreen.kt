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
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun SignInScreen(
	signInWithEmail: SignInWithEmailUseCase,
	resendEmailVerification: ResendEmailVerificationUseCase,
	initialEmail: String,
	showRegistrationSuccessMessage: Boolean,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val viewModel = signInViewModel(
		signInWithEmail = signInWithEmail,
		resendEmailVerification = resendEmailVerification,
		initialEmail = initialEmail,
		showRegistrationSuccessMessage = showRegistrationSuccessMessage,
	)

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
				viewModel.infoMessage?.let { infoMessage ->
					Text(
						text = infoMessage,
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.primary,
					)
				}
				SignInForm(
					email = viewModel.email,
					emailError = viewModel.emailError,
					password = viewModel.password,
					passwordError = viewModel.passwordError,
					isBusy = viewModel.isBusy,
					showResendVerificationEmail = viewModel.showResendVerificationEmail,
					onEmailChange = viewModel::onEmailChange,
					onPasswordChange = viewModel::onPasswordChange,
					onSubmit = viewModel::submitSignIn,
					onResendVerificationEmail = viewModel::resendVerificationEmail,
				)
			}
		}
	}
}
