package app.purecipes.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.home.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.home.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.recipedetails.domain.usecase.ObserveRecipeViewsUseCase
import app.purecipes.feature.search.domain.usecase.ObserveSearchFilterChangesUseCase
import app.purecipes.shared.data.readiness.SearchReadinessCoordinator
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.shelfKey
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.splash.SplashTimings
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

private const val FIRST_PAGE_NUMBER = 1
private const val HOME_SKELETON_AFTER_SPLASH_MILLIS = 1_000
internal const val HOME_SKELETON_REVEAL_MILLIS =
	SplashTimings.MINIMUM_DWELL_MILLIS +
		SplashTimings.FADE_OUT_MILLIS +
		HOME_SKELETON_AFTER_SPLASH_MILLIS

@AssistedInject
class HomeViewModel(
	private val getHomeFeed: GetHomeFeedUseCase,
	private val getHomeShelfPage: GetHomeShelfPageUseCase,
	private val homeFeedRefresh: HomeFeedRefreshCoordinator,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	private val observeRecipeViews: ObserveRecipeViewsUseCase,
	private val observeSearchFilterChanges: ObserveSearchFilterChangesUseCase,
	private val searchReadiness: SearchReadinessCoordinator,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

	var homeFeed by mutableStateOf(HomeFeed())
		private set

	var shelfPagination by mutableStateOf<ImmutableMap<String, PaginationState<Int, RecipeSummary>>>(
		persistentMapOf(),
	)
		private set

	var isHomeLoading by mutableStateOf(true)
		private set

	var homeErrorMessage by mutableStateOf<String?>(null)
		private set

	var homeListFirstVisibleItemIndex by mutableIntStateOf(0)
		private set

	var homeListFirstVisibleItemScrollOffset by mutableIntStateOf(0)
		private set

	private var favoriteEventsJob: Job? = null
	private var recipeViewsJob: Job? = null
	private var loadedSessionKey: String? = null
	private var hasHeldInitialSkeleton = false
	private val homeLoadMutex = Mutex()

	val reloadHomeFeed: () -> Unit = {
		viewModelScope.launch { loadHome() }
	}

	init {
		viewModelScope.launch { loadHome() }
		startFavoriteEventsCollection(sessionKey)
		startRecipeViewsCollection(sessionKey)
		loadedSessionKey = sessionKey
		viewModelScope.launch {
			homeFeedRefresh.revision.collect { revision ->
				if (revision > 0) {
					refreshHomeFeatureRequest()
				}
			}
		}
		viewModelScope.launch {
			homeFeedRefresh.shelvesRevision.collect { revision ->
				if (revision > 0) {
					refreshHomeShelves()
				}
			}
		}
		viewModelScope.launch {
			observeSearchFilterChanges().collect {
				refreshHomeShelves()
			}
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == loadedSessionKey) return
		loadedSessionKey = sessionKey
		startFavoriteEventsCollection(sessionKey)
		startRecipeViewsCollection(sessionKey)
		viewModelScope.launch { loadHome() }
	}

	fun onHomeListScrolled(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
		homeListFirstVisibleItemIndex = firstVisibleItemIndex
		homeListFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
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
				refreshHomeShelves()
			}
		}
	}

	private fun startRecipeViewsCollection(sessionKey: String?) {
		recipeViewsJob?.cancel()
		recipeViewsJob = null
		if (sessionKey == null) {
			return
		}
		recipeViewsJob = viewModelScope.launch {
			observeRecipeViews().collect { ignored ->
				refreshHomeShelves()
			}
		}
	}

	private fun applyFavoriteEvent(event: FavoriteEvent) {
		val isFavorite = when (event) {
			is FavoriteEvent.Added -> true
			is FavoriteEvent.Removed -> false
		}
		homeFeed = homeFeed.copy(
			shelves = homeFeed.shelves.mapNotNull { shelf ->
				updatedShelf(shelf, event, isFavorite)
			},
		)
		val pagination = shelfPagination.toMutableMap()
		pagination.keys.toList().forEach { shelfKey ->
			val state = pagination.getValue(shelfKey)
			if (shelfKey == HomeShelfId.FAVORITES.name && event is FavoriteEvent.Removed) {
				val replacement = paginationWithoutRecipe(state, event.recipeId)
				if (replacement == null) {
					pagination.remove(shelfKey)
				} else {
					pagination[shelfKey] = replacement
				}
			} else {
				state.mapItems { recipe ->
					if (recipe.id == event.recipeId && recipe.isFavorite != isFavorite) {
						recipe.copy(isFavorite = isFavorite)
					} else {
						recipe
					}
				}
			}
		}
		shelfPagination = pagination.toImmutableMap()
	}

	private fun updatedShelf(
		shelf: HomeShelf,
		event: FavoriteEvent,
		isFavorite: Boolean,
	): HomeShelf? {
		val recipes = shelf.recipes.map { recipe ->
			if (recipe.id == event.recipeId) recipe.copy(isFavorite = isFavorite) else recipe
		}
		val visibleRecipes = if (shelf.id == HomeShelfId.FAVORITES && event is FavoriteEvent.Removed) {
			recipes.filter { recipe -> recipe.id != event.recipeId }
		} else {
			recipes
		}
		return when {
			shelf.id == HomeShelfId.FAVORITES && visibleRecipes.isEmpty() -> null
			visibleRecipes == shelf.recipes -> shelf
			else -> shelf.copy(recipes = visibleRecipes)
		}
	}

	private fun paginationWithoutRecipe(
		current: PaginationState<Int, RecipeSummary>,
		recipeId: Int,
	): PaginationState<Int, RecipeSummary>? {
		val remaining = current.allItems.filter { recipe -> recipe.id != recipeId }
		return if (remaining.isEmpty()) {
			null
		} else {
			val state = PaginationState<Int, RecipeSummary>(
				initialPageKey = FIRST_PAGE_NUMBER,
				onRequestPage = { pageKey ->
					viewModelScope.launch {
						loadShelfPage(HomeShelfId.FAVORITES, pageKey, collectionSlug = null)
					}
				},
			)
			state.appendPage(
				pageKey = FIRST_PAGE_NUMBER,
				items = remaining,
				nextPageKey = FIRST_PAGE_NUMBER + 1,
				isLastPage = true,
			)
			state
		}
	}

	private suspend fun loadHome() {
		homeLoadMutex.withLock {
			val holdInitialSkeleton = !hasHeldInitialSkeleton && homeFeed.shelves.isEmpty()
			isHomeLoading = true
			homeErrorMessage = null
			searchReadiness.reportReady()
			val outcome = coroutineScope {
				val skeletonHold = if (holdInitialSkeleton) {
					launch { delay(HOME_SKELETON_REVEAL_MILLIS.milliseconds) }
				} else {
					null
				}
				val result = getHomeFeed()
				skeletonHold?.join()
				result
			}
			if (holdInitialSkeleton) {
				hasHeldInitialSkeleton = true
			}
			val feed = outcome.get()
			if (feed != null) {
				applyFeed(feed)
			} else {
				homeErrorMessage = outcome.getError()?.message
			}
			isHomeLoading = false
		}
	}

	private suspend fun refreshHomeShelves() {
		homeLoadMutex.withLock {
			val feed = getHomeFeed().get() ?: return@withLock
			applyFeed(feed)
		}
	}

	private fun applyFeed(feed: HomeFeed) {
		shelfPagination = feed.shelves.associate { shelf ->
			shelf.shelfKey() to newShelfPagination(shelf)
		}.toImmutableMap()
		homeFeed = feed
	}

	private suspend fun refreshHomeFeatureRequest() {
		homeLoadMutex.withLock {
			val feed = getHomeFeed().get() ?: return@withLock
			homeFeed = homeFeed.copy(featureRequest = feed.featureRequest)
		}
	}

	private fun newShelfPagination(shelf: HomeShelf): PaginationState<Int, RecipeSummary> {
		val shelfId = shelf.id
		val collectionSlug = shelf.collectionSlug
		val habitCuisine = shelf.habitCuisine
		val state = PaginationState<Int, RecipeSummary>(
			initialPageKey = FIRST_PAGE_NUMBER,
			onRequestPage = { pageKey ->
				viewModelScope.launch {
					loadShelfPage(shelfId, pageKey, collectionSlug, habitCuisine)
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

	private suspend fun loadShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		collectionSlug: String?,
		habitCuisine: String? = null,
	) {
		val state = shelfPagination[collectionSlug ?: habitCuisine ?: shelfId.name] ?: return
		val outcome = getHomeShelfPage(
			shelfId = shelfId,
			pageNumber = pageNumber,
			pageSize = HOME_SHELF_PAGE_SIZE,
			collectionSlug = collectionSlug,
			habitCuisine = habitCuisine,
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
