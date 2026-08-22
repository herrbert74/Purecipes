package app.purecipes.shared.ui.component.paging

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.VerticalScrollbar

/**
 * Copied from: [https://github.com/Ahmad-Hamwi/lazy-pagination-compose]
 *
 * Modified: defaults [columns] to [GridCells.Adaptive] with [AdaptiveGridDefaults.MinItemWidth].
 */
@Composable
fun <KEY, T> PaginatedLazyVerticalGrid(
	paginationState: PaginationState<KEY, T>,
	modifier: Modifier = Modifier,
	columns: GridCells = GridCells.Adaptive(AdaptiveGridDefaults.MinItemWidth),
	firstPageProgressIndicator: @Composable () -> Unit = {},
	newPageProgressIndicator: @Composable () -> Unit = {},
	firstPageErrorIndicator: @Composable (e: Exception) -> Unit = {},
	newPageErrorIndicator: @Composable (e: Exception) -> Unit = {},
	firstPageEmptyIndicator: @Composable () -> Unit = {},
	newPageEmptyIndicator: @Composable () -> Unit = {},
	requestInitialPageAutomatically: Boolean = true,
	state: LazyGridState = rememberLazyGridState(),
	contentPadding: PaddingValues = PaddingValues(0.dp),
	reverseLayout: Boolean = false,
	verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
	horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
	flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
	userScrollEnabled: Boolean = true,
	lazyModifier: Modifier = Modifier,
	content: LazyGridScope.() -> Unit,
) {
	PaginatedLazyScrollable<KEY, T, LazyGridState, LazyGridScope>(
		paginationState,
		state,
		firstPageProgressIndicator,
		newPageProgressIndicator,
		firstPageErrorIndicator,
		newPageErrorIndicator,
		firstPageEmptyIndicator,
		newPageEmptyIndicator,
		requestInitialPageAutomatically,
	) { paginatedItemsHandler ->
		Box(modifier = modifier) {
			LazyVerticalGrid(
				columns = columns,
				modifier = Modifier
					.fillMaxSize()
					.then(lazyModifier),
				state = state,
				contentPadding = contentPadding,
				reverseLayout = reverseLayout,
				verticalArrangement = verticalArrangement,
				horizontalArrangement = horizontalArrangement,
				flingBehavior = flingBehavior,
				userScrollEnabled = userScrollEnabled,
			) {
				paginatedItemsHandler {
					content()
				}
			}
			VerticalScrollbar(
				state = state,
				modifier = Modifier.align(Alignment.CenterEnd),
			)
		}
	}
}
