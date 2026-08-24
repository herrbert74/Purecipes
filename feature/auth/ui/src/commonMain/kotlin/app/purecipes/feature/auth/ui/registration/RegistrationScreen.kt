package app.purecipes.feature.auth.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.component.BrandMomentHeader
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun RegistrationScreen(
	onBack: () -> Unit,
	onRegistrationSuccess: (email: String) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: RegistrationViewModel = metroViewModel(),
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Create account",
						modifier = Modifier.testTag(REGISTRATION_SCREEN_TITLE_TAG),
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
				BrandMomentHeader(
					icon = Icons.Filled.Person,
					iconContentDescription = "Create account",
					title = "Join Purecipes",
					description = "Register with your email. Your display name is shown on your profile.",
				)
				RegistrationForm(
					displayName = viewModel.displayName,
					email = viewModel.email,
					emailError = viewModel.emailError,
					password = viewModel.password,
					passwordError = viewModel.passwordError,
					isBusy = viewModel.isBusy,
					onDisplayNameChange = viewModel::onDisplayNameChange,
					onEmailChange = viewModel::onEmailChange,
					onPasswordChange = viewModel::onPasswordChange,
					onSubmit = { viewModel.submitRegistration(onRegistrationSuccess) },
				)
			}
		}
	}
}
