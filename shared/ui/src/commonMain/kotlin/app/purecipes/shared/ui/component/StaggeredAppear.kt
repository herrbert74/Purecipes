package app.purecipes.shared.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.coroutines.delay

private const val STAGGER_MAX_ITEMS = 8
private const val STAGGER_ITEM_DELAY_MS = 45L
private const val STAGGER_DURATION_MS = 320

@Composable
fun Modifier.staggeredAppear(index: Int): Modifier {
	val shouldAnimate = index < STAGGER_MAX_ITEMS
	var visible by remember(index) { mutableStateOf(!shouldAnimate) }
	LaunchedEffect(index) {
		if (!shouldAnimate) {
			visible = true
			return@LaunchedEffect
		}
		visible = false
		delay(index * STAGGER_ITEM_DELAY_MS)
		visible = true
	}
	val alpha by animateFloatAsState(
		targetValue = if (visible) 1f else 0f,
		animationSpec = tween(
			durationMillis = STAGGER_DURATION_MS,
			easing = FastOutSlowInEasing,
		),
		label = "staggerAlpha",
	)
	val offsetY by animateDpAsState(
		targetValue = if (visible) PurecipesTheme.space.none else PurecipesTheme.space.sm,
		animationSpec = tween(
			durationMillis = STAGGER_DURATION_MS,
			easing = FastOutSlowInEasing,
		),
		label = "staggerOffset",
	)
	return this
		.graphicsLayer { this.alpha = alpha }
		.offset(y = offsetY)
}
