package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

const val RECIPE_DETAILS_HEADER_IMAGE_TAG = "recipeDetailsHeaderImage"
val RECIPE_DETAILS_HEADER_HEIGHT: Dp = 280.dp

@Composable
internal fun RecipeDetailsHeaderImage(
	imageUrl: String?,
	title: String,
	modifier: Modifier = Modifier,
	height: Dp = RECIPE_DETAILS_HEADER_HEIGHT,
) {
	AsyncImage(
		model = imageUrl?.trim()?.takeIf { it.isNotEmpty() },
		contentDescription = title,
		modifier = modifier
			.fillMaxWidth()
			.height(height)
			.background(PurecipesTheme.colorScheme.surfaceContainerLow)
			.testTag(RECIPE_DETAILS_HEADER_IMAGE_TAG),
		contentScale = ContentScale.Crop,
	)
}
