package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUESTS_SIGN_IN_BUTTON_TAG = "featureRequestsSignInButton"

@Composable
internal fun FeatureRequestsSignedOutContent(
	onRequestLogIn: () -> Unit,
	modifier: Modifier = Modifier,
) {
	EmptyStateContent(
		icon = Icons.Filled.Lightbulb,
		iconContentDescription = "Feature requests",
		title = "Sign in to request features",
		description = "Requests, votes and comments are tied to your account, so sign in to join the board.",
		modifier = modifier,
		action = {
			PurecipesButton(
				text = "Go to Account",
				onClick = onRequestLogIn,
				modifier = Modifier.testTag(FEATURE_REQUESTS_SIGN_IN_BUTTON_TAG),
			)
		},
	)
}

@Preview(
	name = "Feature requests signed out light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FeatureRequestsSignedOutContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Feature requests") },
				)
			},
		) { innerPadding ->
			FeatureRequestsSignedOutContent(
				onRequestLogIn = {},
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}
