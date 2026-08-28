package app.purecipes.feature.onboarding.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.abs
import kotlin.math.sin

private const val SPARKLE_INNER_RATIO = 0.18f
private const val SPARKLE_DIAGONAL = 0.7071f
private const val SPARKLE_BASE_RADIUS_FRACTION = 0.11f
private const val SPARKLE_CORE_RADIUS_FRACTION = 0.16f
private const val SPARKLE_PHASE_SPREAD = 20f
private const val SPARKLE_FLICKER_SHARPNESS = 8
private const val SPARKLE_VISIBILITY_THRESHOLD = 0.01f

/**
 * Pure Compose stand-in for the AGSL star shader in the reference design, so the sparkle also runs
 * on iOS and Wasm where `RuntimeShader` is unavailable.
 *
 * Each sparkle is a four point diamond whose alpha follows `|sin(phase)|` raised to a high power,
 * which keeps it dark for most of the cycle and produces a short, sharp glint.
 */
internal fun Modifier.sparkleOverlay(
	color: Color,
	time: () -> Float,
): Modifier = this.drawWithCache {
	val path = Path()

	onDrawWithContent {
		drawContent()
		val baseRadius = size.minDimension * SPARKLE_BASE_RADIUS_FRACTION
		val currentTime = time()
		sparkles.forEach { sparkle ->
			val flicker = sparkle.flickerAt(currentTime)
			if (flicker <= SPARKLE_VISIBILITY_THRESHOLD) {
				return@forEach
			}
			val center = sparkle.centerIn(size)
			val radius = baseRadius * sparkle.scale
			path.reset()
			path.addFourPointStar(center = center, radius = radius)
			drawPath(path = path, color = color, alpha = flicker * sparkle.intensity)
			drawCircle(
				color = color,
				radius = radius * SPARKLE_CORE_RADIUS_FRACTION,
				center = center,
				alpha = flicker * sparkle.intensity,
			)
		}
	}
}

@Immutable
private data class Sparkle(
	val x: Float,
	val y: Float,
	val scale: Float,
	val intensity: Float,
	val speed: Float,
) {

	fun centerIn(size: Size): Offset = Offset(x = size.width * x, y = size.height * y)

	fun flickerAt(time: Float): Float {
		val phase = time * speed + x * SPARKLE_PHASE_SPREAD
		var flicker = abs(sin(phase))
		repeat(SPARKLE_FLICKER_SHARPNESS - 1) {
			flicker *= abs(sin(phase))
		}
		return flicker
	}
}

private val sparkles = listOf(
	Sparkle(x = 0.25f, y = 0.30f, scale = 1.0f, intensity = 0.8f, speed = 3.0f),
	Sparkle(x = 0.70f, y = 0.20f, scale = 0.8f, intensity = 0.9f, speed = 4.2f),
	Sparkle(x = 0.45f, y = 0.80f, scale = 1.2f, intensity = 0.7f, speed = 2.5f),
	Sparkle(x = 0.80f, y = 0.60f, scale = 0.6f, intensity = 1.0f, speed = 5.0f),
	Sparkle(x = 0.30f, y = 0.70f, scale = 1.0f, intensity = 0.6f, speed = 3.8f),
	Sparkle(x = 0.60f, y = 0.50f, scale = 0.8f, intensity = 0.8f, speed = 4.5f),
	Sparkle(x = 0.85f, y = 0.35f, scale = 1.0f, intensity = 0.9f, speed = 2.0f),
	Sparkle(x = 0.15f, y = 0.55f, scale = 0.6f, intensity = 0.7f, speed = 6.0f),
)

private fun Path.addFourPointStar(center: Offset, radius: Float) {
	val inner = radius * SPARKLE_INNER_RATIO
	val innerDiagonal = inner * SPARKLE_DIAGONAL
	moveTo(center.x, center.y - radius)
	lineTo(center.x + innerDiagonal, center.y - innerDiagonal)
	lineTo(center.x + radius, center.y)
	lineTo(center.x + innerDiagonal, center.y + innerDiagonal)
	lineTo(center.x, center.y + radius)
	lineTo(center.x - innerDiagonal, center.y + innerDiagonal)
	lineTo(center.x - radius, center.y)
	lineTo(center.x - innerDiagonal, center.y - innerDiagonal)
	close()
}
