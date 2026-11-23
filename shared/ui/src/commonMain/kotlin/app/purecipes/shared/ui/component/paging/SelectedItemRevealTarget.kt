package app.purecipes.shared.ui.component.paging

internal data class SelectedItemRevealTarget(
	val key: Any,
	val index: Int,
	val requestId: Long,
)

internal fun calculateMinimalScrollDelta(
	itemStart: Int,
	itemSize: Int,
	viewportStart: Int,
	viewportEnd: Int,
): Float {
	if (viewportEnd <= viewportStart || itemSize <= 0) {
		return 0f
	}
	val itemEnd = itemStart + itemSize
	return when {
		itemStart < viewportStart -> (itemStart - viewportStart).toFloat()
		itemEnd > viewportEnd -> minOf(itemEnd - viewportEnd, itemStart - viewportStart).toFloat()
		else -> 0f
	}
}

internal const val SELECTED_ITEM_RESIZE_OBSERVE_TIMEOUT_MS = 1000L
