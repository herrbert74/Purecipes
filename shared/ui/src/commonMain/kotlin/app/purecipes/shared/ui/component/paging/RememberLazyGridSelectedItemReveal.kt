package app.purecipes.shared.ui.component.paging

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun rememberLazyGridSelectedItemReveal(
	gridState: LazyGridState,
): (key: Any, index: Int) -> Unit {
	var requestCounter by remember(gridState) { mutableLongStateOf(0L) }
	var revealTarget by remember(gridState) { mutableStateOf<SelectedItemRevealTarget?>(null) }

	LaunchedEffect(gridState, revealTarget) {
		val target = revealTarget ?: return@LaunchedEffect
		var isFirstEmission = true
		withTimeoutOrNull(SELECTED_ITEM_RESIZE_OBSERVE_TIMEOUT_MS) {
			snapshotFlow { gridState.layoutInfo.viewportSize }
				.collect {
					val animate = isFirstEmission
					isFirstEmission = false
					if (animate || !gridState.isScrollInProgress) {
						revealLazyGridItem(gridState, target, animate = animate)
					}
				}
		}
		if (!gridState.isScrollInProgress) {
			revealLazyGridItem(gridState, target, animate = true)
		}
	}

	return remember(gridState) {
		{ key, index ->
			requestCounter += 1L
			revealTarget = SelectedItemRevealTarget(
				key = key,
				index = index,
				requestId = requestCounter,
			)
		}
	}
}

private suspend fun revealLazyGridItem(
	gridState: LazyGridState,
	target: SelectedItemRevealTarget,
	animate: Boolean,
) {
	val layoutInfo = gridState.layoutInfo
	val visibleEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
	if (visibleEnd <= 0) {
		return
	}
	val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { it.key == target.key }
	if (visibleItem != null) {
		val delta = calculateMinimalScrollDelta(
			itemStart = visibleItem.offset.y,
			itemSize = visibleItem.size.height,
			viewportStart = 0,
			viewportEnd = visibleEnd,
		)
		if (delta != 0f) {
			if (animate) {
				gridState.animateScrollBy(delta)
			} else {
				gridState.scrollBy(delta)
			}
		}
	} else if (target.index in 0 until layoutInfo.totalItemsCount) {
		if (animate) {
			gridState.animateScrollToItem(target.index)
		} else {
			gridState.scrollToItem(target.index)
		}
	}
}
