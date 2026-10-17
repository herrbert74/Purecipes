package app.purecipes.feature.recipedetails.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val DISMISS_DRAG_THRESHOLD = 120.dp
private const val DRAG_DAMPING = 0.5f
private const val FLING_VELOCITY_DISMISS_THRESHOLD = 1000f

@Composable
internal fun rememberRecipeDetailsSwipeDismissState(
	listState: LazyListState,
	enabled: Boolean,
	onDismiss: () -> Unit,
): RecipeDetailsSwipeDismissState {
	val density = LocalDensity.current
	val thresholdPx = with(density) { DISMISS_DRAG_THRESHOLD.toPx() }
	val scope = rememberCoroutineScope()
	val animatable = remember { Animatable(0f) }

	val connection = remember(listState, enabled, thresholdPx) {
		RecipeDetailsSwipeDismissConnection(
			listState = listState,
			enabled = enabled,
			thresholdPx = thresholdPx,
			onDismiss = onDismiss,
			currentOffset = { animatable.value },
			onOffsetChange = { offset ->
				scope.launch { animatable.snapTo(offset) }
			},
			onResetAnimation = {
				scope.launch {
					animatable.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
				}
			},
		)
	}

	return remember(connection, animatable) {
		RecipeDetailsSwipeDismissState(
			connection = connection,
			offset = { animatable.value },
		)
	}
}

internal class RecipeDetailsSwipeDismissConnection(
	private val listState: LazyListState,
	private val enabled: Boolean,
	private val thresholdPx: Float,
	private val onDismiss: () -> Unit,
	private val currentOffset: () -> Float,
	private val onOffsetChange: (Float) -> Unit,
	private val onResetAnimation: () -> Unit,
) : NestedScrollConnection {

	private fun canHandleInput(source: NestedScrollSource): Boolean =
		enabled && source == NestedScrollSource.UserInput

	override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
		if (!canHandleInput(source)) return Offset.Zero
		val current = currentOffset()
		return if (current > 0f && available.y < 0f) {
			val newOffset = (current + available.y).coerceAtLeast(0f)
			onOffsetChange(newOffset)
			Offset(0f, available.y)
		} else {
			Offset.Zero
		}
	}

	override fun onPostScroll(
		consumed: Offset,
		available: Offset,
		source: NestedScrollSource,
	): Offset {
		if (!canHandleInput(source)) return Offset.Zero
		val isAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
		return if (available.y > 0f && isAtTop) {
			val delta = available.y * DRAG_DAMPING
			val newOffset = currentOffset() + delta
			onOffsetChange(newOffset)
			Offset(0f, available.y)
		} else {
			Offset.Zero
		}
	}

	override suspend fun onPreFling(available: Velocity): Velocity {
		if (!enabled) return Velocity.Zero
		val current = currentOffset()
		val shouldDismiss = current >= thresholdPx ||
			(available.y > FLING_VELOCITY_DISMISS_THRESHOLD && current > 0f)
		return if (shouldDismiss) {
			onDismiss()
			available
		} else {
			if (current > 0f) {
				onResetAnimation()
			}
			Velocity.Zero
		}
	}
}

internal class RecipeDetailsSwipeDismissState(
	val connection: NestedScrollConnection,
	val offset: () -> Float,
)

internal fun Modifier.recipeDetailsSwipeDismiss(state: RecipeDetailsSwipeDismissState): Modifier {
	return this
		.nestedScroll(state.connection)
		.graphicsLayer {
			translationY = state.offset()
		}
}
