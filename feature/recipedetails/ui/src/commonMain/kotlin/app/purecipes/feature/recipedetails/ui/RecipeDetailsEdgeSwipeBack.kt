package app.purecipes.feature.recipedetails.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalPointerSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val RECIPE_DETAILS_EDGE_SWIPE_WIDTH: Dp = 28.dp
private const val DISMISS_THRESHOLD_RATIO = 0.35f
private const val DISMISS_ANIMATION_DURATION_MS = 200

@Composable
internal fun rememberRecipeDetailsEdgeSwipeBackState(
	enabled: Boolean,
	onDismiss: () -> Unit,
): RecipeDetailsEdgeSwipeBackState {
	val coroutineScope = rememberCoroutineScope()
	return remember(enabled, onDismiss) {
		RecipeDetailsEdgeSwipeBackState(
			enabled = enabled,
			coroutineScope = coroutineScope,
			onDismiss = onDismiss,
		)
	}
}

@Stable
internal class RecipeDetailsEdgeSwipeBackState(
	val enabled: Boolean,
	private val coroutineScope: CoroutineScope,
	private val onDismiss: () -> Unit,
) {

	var maxOffsetPx by mutableFloatStateOf(0f)
	val offsetX = Animatable(0f)

	fun onDrag(dragAmount: Float) {
		if (!enabled) return
		val current = offsetX.value
		val newOffset = (current + dragAmount).coerceIn(0f, maxOffsetPx)
		coroutineScope.launch {
			offsetX.snapTo(newOffset)
		}
	}

	fun onDragEnd() {
		if (!enabled) return
		val current = offsetX.value
		val progress = if (maxOffsetPx > 0f) current / maxOffsetPx else 0f
		coroutineScope.launch {
			if (progress >= DISMISS_THRESHOLD_RATIO) {
				offsetX.animateTo(
					targetValue = maxOffsetPx,
					animationSpec = tween(
						durationMillis = DISMISS_ANIMATION_DURATION_MS,
						easing = FastOutSlowInEasing,
					),
				)
				onDismiss()
			} else {
				offsetX.animateTo(
					targetValue = 0f,
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessMediumLow,
					),
				)
			}
		}
	}

	fun onDragCancel() {
		if (!enabled) return
		coroutineScope.launch {
			offsetX.animateTo(
				targetValue = 0f,
				animationSpec = spring(
					dampingRatio = Spring.DampingRatioMediumBouncy,
					stiffness = Spring.StiffnessMediumLow,
				),
			)
		}
	}
}

internal fun Modifier.recipeDetailsEdgeSwipeBack(
	state: RecipeDetailsEdgeSwipeBackState,
	edgeWidth: Dp = RECIPE_DETAILS_EDGE_SWIPE_WIDTH,
): Modifier = this
	.onSizeChanged { size ->
		state.maxOffsetPx = size.width.toFloat()
	}
	.graphicsLayer {
		translationX = state.offsetX.value
	}
	.pointerInput(state.enabled, edgeWidth) {
		if (!state.enabled) return@pointerInput
		val edgeWidthPx = edgeWidth.toPx()
		detectEdgeSwipeBackGestures(
			edgeWidthPx = edgeWidthPx,
			onHorizontalDrag = state::onDrag,
			onDragEnd = state::onDragEnd,
			onDragCancel = state::onDragCancel,
		)
	}

private suspend fun PointerInputScope.detectEdgeSwipeBackGestures(
	edgeWidthPx: Float,
	onHorizontalDrag: (Float) -> Unit,
	onDragEnd: () -> Unit,
	onDragCancel: () -> Unit,
) {
	awaitEachGesture {
		val down = awaitFirstDown(requireUnconsumed = false)
		if (down.position.x > edgeWidthPx) {
			return@awaitEachGesture
		}
		var overSlop = 0f
		val drag = awaitHorizontalPointerSlopOrCancellation(down.id, down.type) { change, over ->
			if (over > 0f) {
				change.consume()
				overSlop = over
			}
		}
		if (drag != null && overSlop > 0f) {
			onHorizontalDrag(overSlop)
			val completed = horizontalDrag(drag.id) { change ->
				onHorizontalDrag(change.positionChange().x)
				change.consume()
			}
			if (completed) {
				onDragEnd()
			} else {
				onDragCancel()
			}
		}
	}
}
