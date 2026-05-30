package app.purecipes.shared.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.imageResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.splash_pot_bg
import purecipes.shared.ui.generated.resources.splash_pot_fg
import purecipes.shared.ui.generated.resources.splash_pot_spoon
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val STIR_PERIOD_MILLIS = 1500
private const val IMAGE_SIZE = 512f
private const val FULL_TURN_RADIANS = (2 * PI).toFloat()
private const val HALF = 2f

private const val SHOULDER_X = 417.9f
private const val SHOULDER_Y = 230.6f
private const val HANDLE_BASE_X = 486.6f
private const val HANDLE_BASE_Y = 46.6f

private const val STIR_RADIUS_X = 15f
private const val STIR_RADIUS_Y = 8f

private const val ARM_STROKE_WIDTH = 32f
private const val HAND_RADIUS = 18f
private const val ARM_CONTROL_OFFSET_X = 60f
private const val ARM_CONTROL_OFFSET_Y = -80f
private const val ARM_CONTROL_DX_FACTOR = 0.5f
private const val ARM_CONTROL_DY_FACTOR = 0.5f

private const val ARM_COLOR_ARGB = 0xFFDF205B
private val ArmColor = Color(ARM_COLOR_ARGB)

@Composable
fun StirringPot(modifier: Modifier = Modifier) {
	val bg = imageResource(Res.drawable.splash_pot_bg)
	val spoon = imageResource(Res.drawable.splash_pot_spoon)
	val fg = imageResource(Res.drawable.splash_pot_fg)

	val transition = rememberInfiniteTransition(label = "stir")
	val phase by transition.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = STIR_PERIOD_MILLIS, easing = LinearEasing),
			repeatMode = RepeatMode.Restart,
		),
		label = "stirPhase",
	)

	val angle = phase * FULL_TURN_RADIANS
	val dx = STIR_RADIUS_X * cos(angle)
	val dy = STIR_RADIUS_Y * sin(angle)

	Canvas(modifier = modifier) {
		val unit = min(size.width, size.height) / IMAGE_SIZE
		val left = (size.width - IMAGE_SIZE * unit) / HALF
		val top = (size.height - IMAGE_SIZE * unit) / HALF

		translate(left = left, top = top) {
			scale(scaleX = unit, scaleY = unit, pivot = Offset.Zero) {
				// 1. Background (dark interior)
				drawImage(bg)

				// 2. Spoon (translates in an ellipse)
				translate(left = dx, top = dy) {
					drawImage(spoon)
				}

				// 3. Dynamic Arm (bezier curve from shoulder to moving spoon handle)
				val hx = HANDLE_BASE_X + dx
				val hy = HANDLE_BASE_Y + dy
				val cx = SHOULDER_X + ARM_CONTROL_OFFSET_X + dx * ARM_CONTROL_DX_FACTOR
				val cy = SHOULDER_Y + ARM_CONTROL_OFFSET_Y + dy * ARM_CONTROL_DY_FACTOR

				val armPath = Path().apply {
					moveTo(SHOULDER_X, SHOULDER_Y)
					quadraticTo(cx, cy, hx, hy)
				}

				drawPath(
					path = armPath,
					color = ArmColor,
					style = Stroke(width = ARM_STROKE_WIDTH, cap = StrokeCap.Round)
				)
				drawCircle(
					color = ArmColor,
					radius = HAND_RADIUS,
					center = Offset(hx, hy)
				)

				// 4. Foreground (front rim, face, body, left arm, legs, ears)
				drawImage(fg)
			}
		}
	}
}

@Preview
@Composable
private fun StirringPotPreview() {
	StirringPot(modifier = Modifier)
}
