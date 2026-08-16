package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun PreviewAuthenticationProviderButtons() {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = {},
			enabled = false,
		) {
			Text(text = "Continue with Google")
		}
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = {},
			enabled = false,
		) {
			Text(text = "Continue with Apple")
		}
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = {},
			enabled = false,
		) {
			Text(text = "Continue with Facebook")
		}
	}
}
