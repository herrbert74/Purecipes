package app.purecipes.shared.ui.component.paging

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
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
fun rememberLazyListSelectedItemReveal(
	listState: LazyListState,
): (key: Any, index: Int) -> Unit {
	var requestCounter by remember(listState) { mutableLongStateOf(0L) }
	var revealTarget by remember(listState) { mutableStateOf<SelectedItemRevealTarget?>(null) }

	LaunchedEffect(listState, revealTarget) {
		val target = revealTarget ?: return@LaunchedEffect
		var isFirstEmission = true
		withTimeoutOrNull(SELECTED_ITEM_RESIZE_OBSERVE_TIMEOUT_MS) {
			snapshotFlow { listState.layoutInfo.viewportSize }
				.collect {
					val animate = isFirstEmission
					isFirstEmission = false
					if (animate || !listState.isScrollInProgress) {
						revealLazyListItem(listState, target, animate = animate)
					}
				}
		}
		if (!listState.isScrollInProgress) {
			revealLazyListItem(listState, target, animate = true)
		}
	}

	return remember(listState) {
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

private suspend fun revealLazyListItem(
	listState: LazyListState,
	target: SelectedItemRevealTarget,
	animate: Boolean,
) {
	val layoutInfo = listState.layoutInfo
	val visibleEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
	if (visibleEnd <= 0) {
		return
	}
	val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { it.key == target.key }
	if (visibleItem != null) {
		val delta = calculateMinimalScrollDelta(
			itemStart = visibleItem.offset,
			itemSize = visibleItem.size,
			viewportStart = 0,
			viewportEnd = visibleEnd,
		)
		if (delta != 0f) {
			if (animate) {
				listState.animateScrollBy(delta)
			} else {
				listState.scrollBy(delta)
			}
		}
	} else if (target.index in 0 until layoutInfo.totalItemsCount) {
		if (animate) {
			listState.animateScrollToItem(target.index)
		} else {
			listState.scrollToItem(target.index)
		}
	}
}
