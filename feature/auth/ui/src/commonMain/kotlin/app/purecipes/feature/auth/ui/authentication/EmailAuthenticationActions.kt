package app.purecipes.feature.auth.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun EmailAuthenticationActions(
	onSignInClick: () -> Unit,
	onCreateAccountClick: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			HorizontalDivider(modifier = Modifier.weight(1f))
			Text(
				text = "or use email",
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
			HorizontalDivider(modifier = Modifier.weight(1f))
		}
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight)
				.testTag(AUTH_SIGN_IN_WITH_EMAIL_TAG),
			onClick = onSignInClick,
		) {
			Text(text = "Sign in with email")
		}
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight)
				.testTag(AUTH_CREATE_ACCOUNT_WITH_EMAIL_TAG),
			onClick = onCreateAccountClick,
		) {
			Text(text = "Create account with email")
		}
	}
}

@Preview(
	name = "Email authentication actions light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun EmailAuthenticationActionsLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Column(modifier = Modifier.padding(PurecipesTheme.space.l)) {
			EmailAuthenticationActions(
				onSignInClick = {},
				onCreateAccountClick = {},
			)
		}
	}
}
