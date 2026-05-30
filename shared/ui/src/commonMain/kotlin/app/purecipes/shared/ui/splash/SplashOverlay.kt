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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.surfaceLight

private const val FADE_OUT_DURATION_MILLIS = 400
private const val POT_SIZE_DP = 200

@Composable
fun SplashOverlay(
	isVisible: Boolean,
	backgroundColor: Color,
	onExitComplete: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val alpha = remember { Animatable(1f) }
	val currentOnExitComplete by rememberUpdatedState(onExitComplete)

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
			.alpha(alpha.value)
			.background(backgroundColor),
		contentAlignment = Alignment.Center,
	) {
		StirringPot(modifier = Modifier.size(POT_SIZE_DP.dp))
	}
}

@Preview
@Composable
private fun SplashOverlayPreview() {
	SplashOverlay(
		isVisible = true,
		backgroundColor = surfaceLight,
		onExitComplete = {},
	)
}
