package app.purecipes.feature.favorites.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
internal fun FavoritesSignedOutContent(modifier: Modifier = Modifier) {
	EmptyStateContent(
		icon = Icons.Filled.Favorite,
		iconContentDescription = "Favorites",
		title = "Sign in to view favorites",
		description = "Favorites are tied to your session, so each account keeps its own saved recipes.",
		modifier = modifier,
	)
}

@Preview(
	name = "Favorites signed out light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FavoritesSignedOutContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Favorites") },
				)
			},
		) { innerPadding ->
			FavoritesSignedOutContent(
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}
