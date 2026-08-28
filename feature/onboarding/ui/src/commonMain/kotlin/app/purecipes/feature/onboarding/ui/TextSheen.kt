package app.purecipes.feature.onboarding.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

private const val SHEEN_BAND_FRACTION = 0.35f

/**
 * Sweeps a soft highlight band diagonally across whatever the modified node painted, replacing the
 * AGSL shine shader from the reference design with a multiplatform equivalent.
 *
 * [BlendMode.SrcAtop] keeps the band inside already painted pixels, so on text it lights up the
 * glyphs only and leaves the background untouched. That requires an offscreen layer, otherwise the
 * blend would apply against the page background as well.
 */
internal fun Modifier.textSheen(
	color: Color,
	sweep: () -> Float,
): Modifier = this
	.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
	.drawWithContent {
		drawContent()
		val bandWidth = size.width * SHEEN_BAND_FRACTION
		val travel = size.width + bandWidth * 2f
		val bandCenter = -bandWidth + sweep() * travel
		drawRect(
			brush = Brush.linearGradient(
				colors = listOf(Color.Transparent, color, Color.Transparent),
				start = Offset(bandCenter - bandWidth, 0f),
				end = Offset(bandCenter + bandWidth, size.height),
			),
			blendMode = BlendMode.SrcAtop,
		)
	}
