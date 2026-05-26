package app.purecipes.shared.ui.component.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Copied from: [https://github.com/Ahmad-Hamwi/lazy-pagination-compose]
 *
 * Modified: Issue #389 from FlickSlate: Problem with PaginationState and fetchCacheThenRemote
 */
@Suppress("UNCHECKED_CAST", "unused")
@Stable
class PaginationState<KEY, T>(
	initialPageKey: KEY,
	val onRequestPage: PaginationState<KEY, T>.(KEY) -> Unit
) {
	var internalState =
		mutableStateOf<PaginationInternalState<KEY, T>>(PaginationInternalState.Initial(initialPageKey))

	private val pages = LinkedHashMap<KEY, List<T>>()

	val requestedPageKey: KEY?
		get() = (internalState.value as? PaginationInternalState.IHasRequestedPageKey<KEY>)?.requestedPageKey

	val allItems: List<T>
		get() = if (internalState.value.items == null) {
			throw NoSuchElementException("No pages are appended yet")
		} else {
			internalState.value.items!!
		}

	fun setError(exception: Exception) {
		val internalStateSnapshot = internalState.value
		val nextPageKeyOfLoadingState: KEY? =
			(internalStateSnapshot as? PaginationInternalState.Loaded)?.nextPageKey
		val requestedPageKeyOfLoadingOrErrorState: KEY? =
			(internalStateSnapshot as? PaginationInternalState.IHasRequestedPageKey<KEY>)?.requestedPageKey

		// requestedPageKey is going to either be
		// 1- the nextPageKey of the loaded state: because the page has already been loaded and showing an error means a new page error state
		// OR
		// 2- the requestedPageKey of either a loading or a previous error state
		internalState.value = PaginationInternalState.Error(
			initialPageKey = internalStateSnapshot.initialPageKey,
			requestedPageKey = nextPageKeyOfLoadingState
				?: requestedPageKeyOfLoadingOrErrorState
				?: internalStateSnapshot.initialPageKey,
			exception = exception,
			items = internalState.value.items
		)
	}

	fun appendPage(pageKey: KEY, items: List<T>, nextPageKey: KEY, isLastPage: Boolean = false) {
		pages[pageKey] = items
		val newItems = pages.values.flatten()
		val internalStateSnapshot = internalState.value
		val requestedPageKeyOfLoadingOrErrorState: KEY? =
			(internalStateSnapshot as? PaginationInternalState.IHasRequestedPageKey<KEY>)?.requestedPageKey

		internalState.value = PaginationInternalState.Loaded(
			initialPageKey = internalState.value.initialPageKey,
			requestedPageKey = requestedPageKeyOfLoadingOrErrorState
				?: internalStateSnapshot.initialPageKey,
			nextPageKey = nextPageKey,
			items = newItems,
			isLastPage = isLastPage
		)
	}

	fun retryLastFailedRequest() {
		val internalStateSnapshot = internalState.value

		val isError = internalStateSnapshot is PaginationInternalState.Error
		val isLoading = internalStateSnapshot is PaginationInternalState.Loading
		require(isError || isLoading) {
			"Cannot retry: current state is $internalStateSnapshot"
		}

		requestPage(
			initialPageKey = internalStateSnapshot.initialPageKey,
			requestedPageKey = internalStateSnapshot.requestedPageKey,
			items = internalStateSnapshot.items
		)
	}

	fun refresh(initialPageKey: KEY? = null) {
		pages.clear()
		internalState.value = PaginationInternalState.Initial(
			initialPageKey ?: internalState.value.initialPageKey
		)
	}

	fun requestPage(
		initialPageKey: KEY,
		requestedPageKey: KEY,
		items: List<T>? = null,
	) {
		if (internalState.value is PaginationInternalState.Loading) {
			return
		}

		internalState.value = PaginationInternalState.Loading(
			initialPageKey = initialPageKey,
			requestedPageKey = requestedPageKey ?: initialPageKey,
			items = items
		)

		onRequestPage(requestedPageKey ?: initialPageKey)
	}
}

@Suppress("unused")
@Composable
fun <KEY, T> rememberPaginationState(
	initialPageKey: KEY,
	onRequestPage: PaginationState<KEY, T>.(KEY) -> Unit
): PaginationState<KEY, T> {
	return remember {
		PaginationState(
			initialPageKey = initialPageKey,
			onRequestPage = onRequestPage
		)
	}
}
