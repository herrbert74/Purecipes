package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun UploadSignedOutContent(
	onRequestLogIn: () -> Unit,
	modifier: Modifier = Modifier,
) {
	EmptyStateContent(
		icon = Icons.Filled.Add,
		iconContentDescription = "Create recipe",
		title = "Sign in to upload recipes",
		description = "Recipe upload is tied to your account so you can edit your uploaded recipes later.",
		modifier = modifier,
		action = {
			Button(onClick = onRequestLogIn) {
				Text(text = "Go to Account")
			}
		},
	)
}

@Preview(
	name = "Create recipe signed out light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun UploadSignedOutContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Create recipe") },
				)
			},
		) { innerPadding ->
			UploadSignedOutContent(
				onRequestLogIn = {},
				modifier = Modifier.padding(innerPadding).padding(PurecipesTheme.space.m),
			)
		}
	}
}
