package app.purecipes.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.home.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.home.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.shared.data.readiness.SearchReadinessCoordinator
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
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
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val FIRST_PAGE_NUMBER = 1

@AssistedInject
class HomeViewModel(
	private val getHomeFeed: GetHomeFeedUseCase,
	private val getHomeShelfPage: GetHomeShelfPageUseCase,
	private val homeFeedRefresh: HomeFeedRefreshCoordinator,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	private val searchReadiness: SearchReadinessCoordinator,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

	var homeFeed by mutableStateOf(HomeFeed())
		private set

	var shelfPagination by mutableStateOf<ImmutableMap<HomeShelfId, PaginationState<Int, RecipeSummary>>>(
		persistentMapOf(),
	)
		private set

	var isHomeLoading by mutableStateOf(false)
		private set

	var homeErrorMessage by mutableStateOf<String?>(null)
		private set

	private var favoriteEventsJob: Job? = null
	private var loadedSessionKey: String? = null

	val reloadHomeFeed: () -> Unit = {
		viewModelScope.launch { loadHome() }
	}

	init {
		viewModelScope.launch { loadHome() }
		startFavoriteEventsCollection(sessionKey)
		loadedSessionKey = sessionKey
		viewModelScope.launch {
			homeFeedRefresh.revision.collect { revision ->
				if (revision > 0) {
					refreshHomeFeatureRequest()
				}
			}
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == loadedSessionKey) return
		loadedSessionKey = sessionKey
		startFavoriteEventsCollection(sessionKey)
		viewModelScope.launch { loadHome() }
	}

	private fun startFavoriteEventsCollection(sessionKey: String?) {
		favoriteEventsJob?.cancel()
		favoriteEventsJob = null
		if (sessionKey == null) {
			return
		}
		favoriteEventsJob = viewModelScope.launch {
			observeFavoriteEvents().collect { event ->
				applyFavoriteEvent(event)
			}
		}
	}

	private fun applyFavoriteEvent(event: FavoriteEvent) {
		val isFavorite = when (event) {
			is FavoriteEvent.Added -> true
			is FavoriteEvent.Removed -> false
		}
		val updatedShelves = homeFeed.shelves.map { shelf ->
			val updatedRecipes = shelf.recipes.map { recipe ->
				if (recipe.id == event.recipeId && recipe.isFavorite != isFavorite) {
					recipe.copy(isFavorite = isFavorite)
				} else {
					recipe
				}
			}
			if (updatedRecipes == shelf.recipes) shelf else shelf.copy(recipes = updatedRecipes)
		}
		if (updatedShelves != homeFeed.shelves) {
			homeFeed = homeFeed.copy(shelves = updatedShelves)
		}
		shelfPagination.values.forEach { state ->
			state.mapItems { recipe ->
				if (recipe.id == event.recipeId && recipe.isFavorite != isFavorite) {
					recipe.copy(isFavorite = isFavorite)
				} else {
					recipe
				}
			}
		}
	}

	private suspend fun loadHome() {
		isHomeLoading = true
		homeErrorMessage = null
		val outcome = getHomeFeed()
		val feed = outcome.get()
		if (feed != null) {
			shelfPagination = feed.shelves.associate { shelf ->
				shelf.id to newShelfPagination(shelf)
			}.toImmutableMap()
			homeFeed = feed
		} else {
			homeErrorMessage = outcome.getError()?.message
		}
		isHomeLoading = false
		searchReadiness.reportReady()
	}

	private suspend fun refreshHomeFeatureRequest() {
		val feed = getHomeFeed().get() ?: return
		homeFeed = homeFeed.copy(featureRequest = feed.featureRequest)
	}

	private fun newShelfPagination(shelf: HomeShelf): PaginationState<Int, RecipeSummary> {
		val state = PaginationState<Int, RecipeSummary>(
			initialPageKey = FIRST_PAGE_NUMBER,
			onRequestPage = { pageKey ->
				viewModelScope.launch {
					loadShelfPage(shelf.id, pageKey)
				}
			},
		)
		state.appendPage(
			pageKey = FIRST_PAGE_NUMBER,
			items = shelf.recipes,
			nextPageKey = FIRST_PAGE_NUMBER + 1,
			isLastPage = shelf.recipes.size < HOME_SHELF_PAGE_SIZE,
		)
		return state
	}

	private suspend fun loadShelfPage(shelfId: HomeShelfId, pageNumber: Int) {
		val state = shelfPagination[shelfId] ?: return
		val outcome = getHomeShelfPage(
			shelfId = shelfId,
			pageNumber = pageNumber,
			pageSize = HOME_SHELF_PAGE_SIZE,
		)
		val page = outcome.get()
		if (page == null) {
			state.setError(IllegalStateException(outcome.getError()?.message ?: "Couldn't load recipes"))
			return
		}
		state.appendPage(
			pageKey = page.pageNumber,
			items = page.items,
			nextPageKey = page.pageNumber + 1,
			isLastPage = page.pageNumber * page.pageSize >= page.totalMatches,
		)
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(sessionKey: String?): HomeViewModel
	}
}
