package app.purecipes.shared.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.surfaceLight

private const val FADE_OUT_DURATION_MILLIS = 600

@Composable
fun SplashOverlay(
	isVisible: Boolean,
	backgroundColor: Color,
	onExitComplete: () -> Unit,
	modifier: Modifier = Modifier,
	onOverlayDraw: () -> Unit = {},
) {
	val alpha = remember { Animatable(1f) }
	val currentOnExitComplete by rememberUpdatedState(onExitComplete)
	val currentOnOverlayDraw by rememberUpdatedState(onOverlayDraw)
	var overlayDrawReported by remember { mutableStateOf(false) }

	LaunchedEffect(isVisible) {
		if (!isVisible) {
			alpha.animateTo(
				targetValue = 0f,
				animationSpec = tween(durationMillis = FADE_OUT_DURATION_MILLIS, easing = FastOutLinearInEasing),
			)
			currentOnExitComplete()
		}
	}

	Box(
		modifier = modifier
			.fillMaxSize()
			.drawWithContent {
				drawContent()
				if (!overlayDrawReported) {
					overlayDrawReported = true
					currentOnOverlayDraw()
				}
			}
			.alpha(alpha.value)
			.background(backgroundColor),
		contentAlignment = Alignment.Center,
	) {
		StirringPot(modifier = Modifier.size(SplashAnimatedIconSpec.VISIBLE_SIZE_DP.dp))
	}
}

@Preview(
	name = "Splash overlay",
	showBackground = true,
)
@Composable
private fun SplashOverlayPreview() {
	PurecipesPreviewScaffold {
		SplashOverlay(
			isVisible = true,
			backgroundColor = surfaceLight,
			onExitComplete = {},
			modifier = Modifier.fillMaxSize(),
		)
	}
}
