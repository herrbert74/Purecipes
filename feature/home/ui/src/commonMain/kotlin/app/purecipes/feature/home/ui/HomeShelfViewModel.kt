package app.purecipes.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.paging.PaginationState
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val FIRST_PAGE_NUMBER = 1

private const val PAGE_SIZE = 20

@AssistedInject
class HomeShelfViewModel(
	private val getHomeShelfPage: GetHomeShelfPageUseCase,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	@Assisted private val shelfId: HomeShelfId,
	@Assisted private val collectionSlug: String?,
	@Assisted private val habitCuisine: String?,
	@Assisted val title: String,
	@Assisted sessionKey: String?,
) : ViewModel() {

	private var activeSessionKey: String? = sessionKey
	private var favoriteEventsJob: Job? = null

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var isLoading by mutableStateOf(false)
		private set

	var totalMatches by mutableIntStateOf(0)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	val paginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadPage(pageKey)
			}
		},
	)

	init {
		startFavoriteEventsCollection()
		viewModelScope.launch {
			loadPage(FIRST_PAGE_NUMBER)
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == activeSessionKey) {
			return
		}
		activeSessionKey = sessionKey
		favoriteEventsJob?.cancel()
		favoriteEventsJob = null
		startFavoriteEventsCollection()
		refresh()
	}

	private fun startFavoriteEventsCollection() {
		if (activeSessionKey == null) {
			return
		}
		favoriteEventsJob = viewModelScope.launch {
			observeFavoriteEvents().collect {
				refresh()
			}
		}
	}

	private fun refresh() {
		viewModelScope.launch {
			recipes.clear()
			totalMatches = 0
			errorMessage = null
			paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
			loadPage(FIRST_PAGE_NUMBER)
		}
	}

	private suspend fun loadPage(pageNumber: Int) {
		if (pageNumber == FIRST_PAGE_NUMBER) {
			isLoading = true
		}
		val outcome = getHomeShelfPage(
			shelfId = shelfId,
			pageNumber = pageNumber,
			pageSize = PAGE_SIZE,
			collectionSlug = collectionSlug,
			habitCuisine = habitCuisine,
		)
		val page = outcome.get()
		if (page != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				recipes.clear()
				totalMatches = page.totalMatches
			}
			recipes.addAll(page.items)
			val nextPageKey = pageNumber + 1
			val isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches
			paginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
		} else {
			val err = outcome.getError()
			if (err != null) {
				paginationState.setError(IllegalStateException(err.message))
				errorMessage = err.message
			}
		}
		if (pageNumber == FIRST_PAGE_NUMBER) {
			isLoading = false
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(
			shelfId: HomeShelfId,
			collectionSlug: String?,
			habitCuisine: String?,
			title: String,
			sessionKey: String?,
		): HomeShelfViewModel
	}
}
