package app.purecipes.shared.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import org.jetbrains.compose.resources.vectorResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.splash_pot_bg
import purecipes.shared.ui.generated.resources.splash_pot_fg
import purecipes.shared.ui.generated.resources.splash_pot_spoon
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val VIEWPORT_SIZE = SplashAnimatedIconSpec.CANVAS_VIEWPORT_SIZE
private const val FULL_TURN_RADIANS = (2 * PI).toFloat()
private const val HALF = 2f

private const val SPOON_PATH_ORIGIN_X = 205f
private const val SPOON_PATH_ORIGIN_Y = 108f
private const val SPOON_BOWL_X = SPOON_PATH_ORIGIN_X + 28f
private const val SPOON_BOWL_Y = SPOON_PATH_ORIGIN_Y + 65f
private const val SPOON_HANDLE_TIP_X = SPOON_PATH_ORIGIN_X + 103f
private const val SPOON_HANDLE_TIP_Y = SPOON_PATH_ORIGIN_Y
private const val HANDLE_GRIP_ALONG_SHAFT = 0.64f

private const val HANDLE_BASE_X =
	SPOON_BOWL_X + (SPOON_HANDLE_TIP_X - SPOON_BOWL_X) * HANDLE_GRIP_ALONG_SHAFT
private const val HANDLE_BASE_Y =
	SPOON_BOWL_Y + (SPOON_HANDLE_TIP_Y - SPOON_BOWL_Y) * HANDLE_GRIP_ALONG_SHAFT

private const val SHOULDER_X = 302f
private const val SHOULDER_Y = 212f

private const val ARM_CONTROL_X = 349f
private const val ARM_CONTROL_Y = 170f

private const val STIR_RADIUS_X = 18f
private const val STIR_RADIUS_Y = 15f

private const val ARM_STROKE_WIDTH = 12f
private const val HAND_RADIUS = 12f

private const val ARM_CONTROL_DX_FACTOR = 0.5f
private const val ARM_CONTROL_DY_FACTOR = 0.5f

private const val ARM_COLOR_ARGB = 0xFFDF205B
private val ArmColor = Color(ARM_COLOR_ARGB)

private const val SPLASH_PREVIEW_BACKGROUND_COLOR = 0xFFF8F7L

@Composable
fun StirringPot(modifier: Modifier = Modifier) {
	val bg = vectorResource(Res.drawable.splash_pot_bg)
	val spoon = vectorResource(Res.drawable.splash_pot_spoon)
	val fg = vectorResource(Res.drawable.splash_pot_fg)

	val bgPainter = rememberVectorPainter(bg)
	val spoonPainter = rememberVectorPainter(spoon)
	val fgPainter = rememberVectorPainter(fg)
	val layerDrawSize = Size(bg.viewportWidth, bg.viewportHeight)

	val transition = rememberInfiniteTransition(label = "stir")
	val phase by transition.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = SplashTimings.STIR_PERIOD_MILLIS, easing = LinearEasing),
			repeatMode = RepeatMode.Restart,
		),
		label = "stirPhase",
	)

	val angle = phase * FULL_TURN_RADIANS
	val dx = STIR_RADIUS_X * cos(angle)
	val dy = STIR_RADIUS_Y * sin(angle)

	Canvas(modifier = modifier) {
		val scale = min(size.width, size.height) / VIEWPORT_SIZE
		val centerX = size.width / HALF
		val centerY = size.height / HALF

		translate(centerX, centerY) {
			scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
				translate(-VIEWPORT_SIZE / HALF, -VIEWPORT_SIZE / HALF) {
					with(bgPainter) {
						draw(layerDrawSize)
					}

					translate(left = dx, top = dy) {
						with(spoonPainter) {
							draw(layerDrawSize)
						}
					}

					val hx = HANDLE_BASE_X + dx
					val hy = HANDLE_BASE_Y + dy
					val cx = ARM_CONTROL_X + dx * ARM_CONTROL_DX_FACTOR
					val cy = ARM_CONTROL_Y + dy * ARM_CONTROL_DY_FACTOR

					val armPath = Path().apply {
						moveTo(SHOULDER_X, SHOULDER_Y)
						quadraticTo(cx, cy, hx, hy)
					}

					drawPath(
						path = armPath,
						color = ArmColor,
						style = Stroke(width = ARM_STROKE_WIDTH, cap = StrokeCap.Round),
					)
					drawCircle(
						color = ArmColor,
						radius = HAND_RADIUS,
						center = Offset(hx, hy),
					)

					with(fgPainter) {
						draw(layerDrawSize)
					}
				}
			}
		}
	}
}

@Preview(
	name = "Stirring pot",
	showBackground = true,
	backgroundColor = SPLASH_PREVIEW_BACKGROUND_COLOR,
)
@Composable
private fun StirringPotPreview() {
	PurecipesPreviewScaffold {
		StirringPot(modifier = Modifier.size(SplashAnimatedIconSpec.VISIBLE_SIZE_DP.dp))
	}
}
