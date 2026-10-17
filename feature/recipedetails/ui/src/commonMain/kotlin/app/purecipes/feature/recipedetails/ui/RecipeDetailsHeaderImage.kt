package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

const val RECIPE_DETAILS_HEADER_IMAGE_TAG = "recipeDetailsHeaderImage"
val RECIPE_DETAILS_HEADER_HEIGHT: Dp = 280.dp
private val RECIPE_DETAILS_EXTRA_PARALLAX_HEIGHT: Dp = 120.dp
private const val HALF_FACTOR = 0.5f
private const val PARALLAX_RATIO = 0.5f

@Composable
internal fun RecipeDetailsHeaderImage(
	imageUrl: String?,
	title: String,
	modifier: Modifier = Modifier,
	height: Dp = RECIPE_DETAILS_HEADER_HEIGHT,
	scrollOffset: () -> Float = { 0f },
) {
	val density = LocalDensity.current
	val extraHeightPx = with(density) { RECIPE_DETAILS_EXTRA_PARALLAX_HEIGHT.toPx() }
	val heightPx = with(density) { height.toPx() }
	val maxTranslation = extraHeightPx * HALF_FACTOR

	Box(
		modifier = modifier
			.fillMaxWidth()
			.height(height)
			.clipToBounds()
			.background(PurecipesTheme.colorScheme.surfaceContainerLow)
			.testTag(RECIPE_DETAILS_HEADER_IMAGE_TAG),
	) {
		AsyncImage(
			model = imageUrl?.trim()?.takeIf { it.isNotEmpty() },
			contentDescription = title,
			modifier = Modifier
				.fillMaxWidth()
				.height(height + RECIPE_DETAILS_EXTRA_PARALLAX_HEIGHT)
				.graphicsLayer {
					val offset = scrollOffset()
					if (offset < 0f) {
						val scale = 1f + (-offset / heightPx)
						scaleX = scale
						scaleY = scale
						transformOrigin = TransformOrigin(HALF_FACTOR, 0f)
						translationY = -maxTranslation
					} else {
						translationY = -maxTranslation + (offset * PARALLAX_RATIO).coerceAtMost(maxTranslation)
					}
				},
			contentScale = ContentScale.Crop,
		)
	}
}
