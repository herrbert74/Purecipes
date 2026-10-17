package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.recipeImageSharedElement
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import kotlin.math.roundToInt

const val RECIPE_DETAILS_HEADER_IMAGE_TAG = "recipeDetailsHeaderImage"
val RECIPE_DETAILS_HEADER_HEIGHT: Dp = 360.dp
val RECIPE_DETAILS_MIN_HEADER_HEIGHT: Dp = 260.dp
private val HEADER_SHRINK_RANGE: Dp = 180.dp
private val PARALLAX_TRANSLATION: Dp = 36.dp
private const val HALF_FACTOR = 0.5f
private const val IMAGE_ORIGIN_Y = 0.15f
private const val INITIAL_IMAGE_SCALE = 1.2f
private const val FINAL_IMAGE_SCALE = 1.0f

@Composable
internal fun RecipeDetailsHeaderImage(
	imageUrl: String?,
	title: String,
	modifier: Modifier = Modifier,
	recipeId: Int = 0,
	height: Dp = RECIPE_DETAILS_HEADER_HEIGHT,
	scrollOffset: () -> Float = { 0f },
) {
	val density = LocalDensity.current
	val initialHeightPx = with(density) { height.toPx() }
	val minHeightPx = with(density) { RECIPE_DETAILS_MIN_HEADER_HEIGHT.toPx() }
	val shrinkRangePx = with(density) { HEADER_SHRINK_RANGE.toPx() }
	val parallaxOffsetPx = with(density) { PARALLAX_TRANSLATION.toPx() }

	Box(
		modifier = modifier
			.fillMaxWidth()
			.layout { measurable, constraints ->
				val offset = scrollOffset()
				val currentHeightPx = if (offset < 0f) {
					(initialHeightPx - offset).roundToInt()
				} else {
					val progress = (offset / shrinkRangePx).coerceIn(0f, 1f)
					(initialHeightPx - (initialHeightPx - minHeightPx) * progress).roundToInt()
				}
				val placeable = measurable.measure(
					constraints.copy(
						minHeight = currentHeightPx,
						maxHeight = currentHeightPx,
					),
				)
				layout(placeable.width, placeable.height) {
					placeable.placeRelative(0, 0)
				}
			}
			.clipToBounds()
			.background(PurecipesTheme.colorScheme.surfaceContainerLow)
			.testTag(RECIPE_DETAILS_HEADER_IMAGE_TAG),
	) {
		AsyncImage(
			model = imageUrl?.trim()?.takeIf { it.isNotEmpty() },
			contentDescription = title,
			modifier = Modifier
				.fillMaxSize()
				.recipeImageSharedElement(recipeId)
				.graphicsLayer {
					val offset = scrollOffset()
					if (offset < 0f) {
						val scale = INITIAL_IMAGE_SCALE + (-offset / initialHeightPx)
						scaleX = scale
						scaleY = scale
						transformOrigin = TransformOrigin(HALF_FACTOR, 0f)
						translationY = 0f
					} else {
						val progress = (offset / shrinkRangePx).coerceIn(0f, 1f)
						val scale = INITIAL_IMAGE_SCALE - (INITIAL_IMAGE_SCALE - FINAL_IMAGE_SCALE) * progress
						scaleX = scale
						scaleY = scale
						transformOrigin = TransformOrigin(HALF_FACTOR, IMAGE_ORIGIN_Y)
						translationY = progress * parallaxOffsetPx
					}
				},
			contentScale = ContentScale.Crop,
		)
	}
}
