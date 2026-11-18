package app.purecipes.feature.auth.ui.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.component.atom.headingSemantics
import app.purecipes.shared.ui.component.atom.includeInParentAccessibilityContainer
import app.purecipes.shared.ui.component.organism.BrandMomentHeader
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.back

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
				modifier = Modifier.includeInParentAccessibilityContainer(),
				title = {
					Text(
						text = "Create account",
						modifier = Modifier.testTag(REGISTRATION_SCREEN_TITLE_TAG).headingSemantics(),
					)
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = AppIcons.ArrowBack,
							contentDescription = stringResource(Res.string.back),
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
					.includeInParentAccessibilityContainer()
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				BrandMomentHeader(
					icon = AppIcons.Person,
					iconContentDescription = "Create account",
					title = "Join Purecipes",
					description = "Register with your email. Your display name is shown on your profile.",
					compact = true,
				)
				RegistrationForm(
					displayName = viewModel.displayName,
					email = viewModel.email,
					emailError = viewModel.emailError,
					password = viewModel.password,
					passwordError = viewModel.passwordError,
					confirmPassword = viewModel.confirmPassword,
					confirmPasswordError = viewModel.confirmPasswordError,
					isBusy = viewModel.isBusy,
					onDisplayNameChange = viewModel::onDisplayNameChange,
					onEmailChange = viewModel::onEmailChange,
					onPasswordChange = viewModel::onPasswordChange,
					onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
					onSubmit = { viewModel.submitRegistration(onRegistrationSuccess) },
				)
			}
		}
	}
}
