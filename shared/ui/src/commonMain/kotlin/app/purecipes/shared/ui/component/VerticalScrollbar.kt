package app.purecipes.shared.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme

private const val SCROLLBAR_MIN_THUMB_FRACTION = 0.1f

@Composable
fun VerticalScrollbar(
	state: LazyListState,
	modifier: Modifier = Modifier,
) {
	val thumbFractions by remember(state) {
		derivedStateOf {
			val layoutInfo = state.layoutInfo
			val totalItems = layoutInfo.totalItemsCount
			val visibleItems = layoutInfo.visibleItemsInfo
			if (totalItems == 0 || visibleItems.isEmpty()) return@derivedStateOf null
			val viewportH = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
			if (viewportH <= 0f) return@derivedStateOf null
			val firstItem = visibleItems.first()
			val lastItem = visibleItems.last()
			val avgH = if (visibleItems.size > 1) {
				(lastItem.offset + lastItem.size - firstItem.offset).toFloat() / visibleItems.size
			} else {
				firstItem.size.toFloat()
			}
			scrollbarThumbFractions(
				totalItems = totalItems,
				averageItemExtent = avgH,
				viewportExtent = viewportH,
				firstVisibleIndex = firstItem.index,
				firstVisibleOffset = firstItem.offset.toFloat(),
			)
		}
	}
	VerticalScrollbarThumb(
		thumbFractions = thumbFractions,
		modifier = modifier,
	)
}

@Composable
fun VerticalScrollbar(
	state: LazyGridState,
	modifier: Modifier = Modifier,
) {
	val thumbFractions by remember(state) {
		derivedStateOf {
			val layoutInfo = state.layoutInfo
			val totalItems = layoutInfo.totalItemsCount
			val visibleItems = layoutInfo.visibleItemsInfo
			if (totalItems == 0 || visibleItems.isEmpty()) return@derivedStateOf null
			val viewportH = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
			if (viewportH <= 0f) return@derivedStateOf null
			val firstItem = visibleItems.minBy { it.offset.y }
			val lastItem = visibleItems.maxBy { it.offset.y + it.size.height }
			val visibleSpan = (lastItem.offset.y + lastItem.size.height - firstItem.offset.y).toFloat()
			val avgH = if (visibleItems.size > 1) {
				visibleSpan / visibleItems.size
			} else {
				firstItem.size.height.toFloat()
			}
			scrollbarThumbFractions(
				totalItems = totalItems,
				averageItemExtent = avgH,
				viewportExtent = viewportH,
				firstVisibleIndex = firstItem.index,
				firstVisibleOffset = firstItem.offset.y.toFloat(),
			)
		}
	}
	VerticalScrollbarThumb(
		thumbFractions = thumbFractions,
		modifier = modifier,
	)
}

@Composable
private fun VerticalScrollbarThumb(
	thumbFractions: Pair<Float, Float>?,
	modifier: Modifier = Modifier,
) {
	val thumbColor = PurecipesTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
	Canvas(
		modifier = modifier
			.fillMaxHeight()
			.width(6.dp),
	) {
		val fractions = thumbFractions ?: return@Canvas
		val (thumbStart, thumbEnd) = fractions
		drawRoundRect(
			color = thumbColor,
			topLeft = Offset(1.dp.toPx(), size.height * thumbStart),
			size = Size(4.dp.toPx(), size.height * (thumbEnd - thumbStart)),
			cornerRadius = CornerRadius(2.dp.toPx()),
		)
	}
}

private fun scrollbarThumbFractions(
	totalItems: Int,
	averageItemExtent: Float,
	viewportExtent: Float,
	firstVisibleIndex: Int,
	firstVisibleOffset: Float,
): Pair<Float, Float>? {
	val totalH = totalItems * averageItemExtent
	return if (averageItemExtent <= 0f || totalH <= viewportExtent) {
		null
	} else {
		val thumbH = (viewportExtent / totalH).coerceIn(SCROLLBAR_MIN_THUMB_FRACTION, 1f)
		val scrolled = firstVisibleIndex * averageItemExtent - firstVisibleOffset
		val maxScroll = totalH - viewportExtent
		val thumbStart = if (maxScroll > 0f) {
			(scrolled / maxScroll * (1f - thumbH)).coerceIn(0f, 1f - thumbH)
		} else {
			0f
		}
		thumbStart to thumbStart + thumbH
	}
}
