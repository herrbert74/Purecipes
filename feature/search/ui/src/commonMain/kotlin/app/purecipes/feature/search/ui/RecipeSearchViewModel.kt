package app.purecipes.feature.search.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.model.SearchPerformedContext
import app.purecipes.feature.analytics.domain.model.asHandledException
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.search.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.search.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.ObserveSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.filter.FilterTab
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.PantryDelta
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FIRST_PAGE_NUMBER = 1
private const val PAGE_SIZE = 20
private const val INGREDIENT_MATCH_DEBOUNCE_MS = 300L

@AssistedInject
class RecipeSearchViewModel(
	private val filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val searchRecipes: SearchRecipesUseCase,
	private val getHomeFeed: GetHomeFeedUseCase,
	private val getHomeShelfPage: GetHomeShelfPageUseCase,
	private val getFavoriteRecipesPage: GetFavoriteRecipesPageUseCase,
	private val trackEvent: TrackEventUseCase,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val sendHandledException: SendHandledExceptionUseCase,
	private val getSearchFilters: GetSearchFiltersUseCase,
	private val saveSearchFilters: SaveSearchFiltersUseCase,
	private val getSearchPreferences: GetSearchPreferencesUseCase,
	private val observeSearchPreferences: ObserveSearchPreferencesUseCase,
	private val getUserPantry: GetUserPantryUseCase,
	private val updateUserPantry: UpdateUserPantryUseCase,
	private val getUserExcludedIngredients: GetUserExcludedIngredientsUseCase,
	private val updateUserExcludedIngredients: UpdateUserExcludedIngredientsUseCase,
	private val matchIngredientInRecipes: MatchIngredientInRecipesUseCase,
	private val searchReadiness: SearchReadinessCoordinator,
	private val homeFeedRefresh: HomeFeedRefreshCoordinator,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	observePremiumStatus: ObservePremiumStatusUseCase,
	@Assisted initialShowFilterSheet: Boolean,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

	var searchQuery by mutableStateOf("")
		private set

	var isSearching by mutableStateOf(false)
		private set

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

	var isSearchBarActive by mutableStateOf(false)
		private set

	var isFilterSheetVisible by mutableStateOf(false)
		private set

	var selectedFilterTab by mutableStateOf(FilterTab.Pantry)

	var isPremium by mutableStateOf(false)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var searchFilterNote by mutableStateOf<String?>(null)
		private set

	var activeFilters by mutableStateOf(SearchFilters())
		private set

	var pantryIngredients by mutableStateOf(emptySet<String>())
		private set

	var excludedIngredients by mutableStateOf(emptySet<String>())
		private set

	var customPantryIngredients by mutableStateOf(emptySet<String>())
		private set

	var keyIngredients by mutableStateOf(emptySet<String>())
		private set

	var ingredientMatchPreview by mutableStateOf<IngredientMatchResponse?>(null)
		private set

	var isIngredientMatchLoading by mutableStateOf(false)
		private set

	private var isSeeAllActive by mutableStateOf(false)
	private var showsFavoritesResults by mutableStateOf(false)
	private var showsIngredientResults by mutableStateOf(false)

	val showsHomeFeed: Boolean
		get() = searchEntryIsIdle() && filtersAreIdle()

	private var ingredientMatchJob: Job? = null
	private var favoriteEventsJob: Job? = null
	private var lastSearchedFilters: SearchFilters = SearchFilters()
	private var lastSearchedKeyIngredients: Set<String> = emptySet()
	private var lastSavedPantry: Set<String> = emptySet()
	private var lastSavedExcludedIngredients: Set<String> = emptySet()
	private var loadedSessionKey: String? = null
	private var shouldReopenFilterSheetAfterNavigation = false
	private var searchPreferences = SearchPreferences()

	var totalMatches by mutableIntStateOf(0)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	val nearMissRecipes = mutableStateListOf<NearMissRecipe>()

	val paginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadPageOfResults(pageKey)
			}
		},
	)

	init {
		viewModelScope.launch {
			observePremiumStatus().collect { premium ->
				isPremium = premium
				if (!premium) {
					if (activeFilters.hasPremiumFilters()) {
						activeFilters = activeFilters.withoutPremiumFilters()
					}
					if (keyIngredients.isNotEmpty()) {
						keyIngredients = emptySet()
						lastSearchedKeyIngredients = emptySet()
					}
				}
			}
		}
		viewModelScope.launch {
			reloadSessionState(sessionKey)
			loadedSessionKey = sessionKey
			searchPreferences = getSearchPreferences()
			if (initialShowFilterSheet) {
				isFilterSheetVisible = true
			}
			if (showsHomeFeed) {
				loadHome()
			} else {
				doSearch()
			}
			observeSearchPreferences().collect { preferences ->
				if (preferences != searchPreferences) {
					searchPreferences = preferences
					if (showsHomeFeed) {
						refreshSearchFilterNote()
					} else {
						doSearch()
					}
				}
			}
		}
		startFavoriteEventsCollection(sessionKey)
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
		viewModelScope.launch {
			reloadSessionState(sessionKey)
			loadedSessionKey = sessionKey
			startFavoriteEventsCollection(sessionKey)
			if (showsHomeFeed) {
				loadHome()
			} else {
				doSearch()
			}
		}
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
		recipes.forEachIndexed { index, recipe ->
			if (recipe.id == event.recipeId && recipe.isFavorite != isFavorite) {
				recipes[index] = recipe.copy(isFavorite = isFavorite)
			}
		}
		nearMissRecipes.forEachIndexed { index, nearMiss ->
			if (nearMiss.recipe.id == event.recipeId && nearMiss.recipe.isFavorite != isFavorite) {
				nearMissRecipes[index] = nearMiss.copy(
					recipe = nearMiss.recipe.copy(isFavorite = isFavorite),
				)
			}
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
		val favorite = isFavorite
		shelfPagination.values.forEach { state ->
			state.mapItems { recipe ->
				if (recipe.id == event.recipeId && recipe.isFavorite != favorite) {
					recipe.copy(isFavorite = favorite)
				} else {
					recipe
				}
			}
		}
	}

	fun onSearchQueryChange(query: String) {
		searchQuery = query
	}

	fun onSearchBarExpandedChange(expanded: Boolean) {
		if (expanded) {
			isSeeAllActive = false
			showsFavoritesResults = false
			isSearchBarActive = true
			viewModelScope.launch { doSearch() }
		} else {
			isSearchBarActive = false
			searchQuery = ""
			isSeeAllActive = false
			showsFavoritesResults = false
			showsIngredientResults = false
			if (!showsHomeFeed) {
				viewModelScope.launch { doSearch() }
			}
		}
	}

	fun onFiltersChange(
		filters: SearchFilters,
		search: Boolean = false,
		seeAllShelf: HomeShelfId? = null,
	) {
		val opensUnfilteredShelf = seeAllShelf == HomeShelfId.NEW || seeAllShelf == HomeShelfId.FAVORITES
		showsFavoritesResults = seeAllShelf == HomeShelfId.FAVORITES
		isSeeAllActive = opensUnfilteredShelf
		if (opensUnfilteredShelf) {
			viewModelScope.launch { doSearch() }
		} else {
			activeFilters = if (isPremium) filters else filters.withoutPremiumFilters()
			if (search) {
				persistFilterSheetChangesAndSearch()
			}
		}
	}

	fun onFilterButtonClick() {
		isFilterSheetVisible = true
	}

	fun onNavigateToPaywall() {
		if (isFilterSheetVisible) {
			shouldReopenFilterSheetAfterNavigation = true
			isFilterSheetVisible = false
		}
	}

	fun onPremiumFeatureBlocked(feature: String) {
		trackEvent(
			AnalyticsEvent.PremiumFeatureBlocked(
				feature = feature,
				origin = AnalyticsOrigin.SEARCH,
			),
		)
		onNavigateToPaywall()
	}

	fun onSearchContentVisible() {
		if (shouldReopenFilterSheetAfterNavigation) {
			isFilterSheetVisible = true
			shouldReopenFilterSheetAfterNavigation = false
		}
	}

	fun onFilterSheetDismiss() {
		if (shouldReopenFilterSheetAfterNavigation) {
			return
		}
		isFilterSheetVisible = false
		stripPremiumFilterDraftIfNeeded()
		persistFilterSheetChangesAndSearch()
	}

	private fun stripPremiumFilterDraftIfNeeded() {
		if (isPremium) return
		if (activeFilters.hasPremiumFilters()) {
			activeFilters = activeFilters.withoutPremiumFilters()
		}
		if (keyIngredients.isNotEmpty()) {
			keyIngredients = emptySet()
		}
	}

	private fun persistFilterSheetChangesAndSearch() {
		val filtersChanged = activeFilters != lastSearchedFilters
		val keyIngredientsChanged = keyIngredients != lastSearchedKeyIngredients
		val pantryChanged = pantryIngredients != lastSavedPantry
		val excludedChanged = excludedIngredients != lastSavedExcludedIngredients
		val hasFilterSheetChanges = listOf(
			filtersChanged,
			keyIngredientsChanged,
			pantryChanged,
			excludedChanged,
		).any { changed -> changed }
		if (!hasFilterSheetChanges) {
			return
		}
		viewModelScope.launch {
			var saveErrorMessage: String? = null
			if (filtersChanged) {
				saveSearchFilters(activeFilters)
				lastSearchedFilters = activeFilters
			}
			if (keyIngredientsChanged) {
				lastSearchedKeyIngredients = keyIngredients
			}
			if (pantryChanged) {
				saveErrorMessage = persistPantryChanges() ?: saveErrorMessage
			}
			if (excludedChanged) {
				saveErrorMessage = persistExcludedIngredientChanges() ?: saveErrorMessage
			}
			if (shouldRestoreHomeFeed(pantryChanged, excludedChanged)) {
				showsIngredientResults = false
				loadHome()
			} else {
				if (pantryChanged || excludedChanged) {
					showsIngredientResults = true
				}
				doSearch()
			}
			if (saveErrorMessage != null) {
				errorMessage = saveErrorMessage
			}
		}
	}

	private suspend fun persistPantryChanges(): String? {
		val pantryOutcome = updateUserPantry(
			PantryDelta(
				add = pantryIngredients - lastSavedPantry,
				remove = lastSavedPantry - pantryIngredients,
			),
		)
		val updatedPantry = pantryOutcome.get()
		return if (updatedPantry != null) {
			pantryIngredients = updatedPantry
			lastSavedPantry = updatedPantry
			null
		} else {
			pantryIngredients = lastSavedPantry
			pantryOutcome.getError()?.message
		}
	}

	private suspend fun persistExcludedIngredientChanges(): String? {
		val excludedOutcome = updateUserExcludedIngredients(
			ExcludedIngredientsDelta(
				add = excludedIngredients - lastSavedExcludedIngredients,
				remove = lastSavedExcludedIngredients - excludedIngredients,
			),
		)
		val updatedExcludedIngredients = excludedOutcome.get()
		return if (updatedExcludedIngredients != null) {
			excludedIngredients = updatedExcludedIngredients
			lastSavedExcludedIngredients = updatedExcludedIngredients
			null
		} else {
			excludedIngredients = lastSavedExcludedIngredients
			excludedOutcome.getError()?.message
		}
	}

	fun onKeyIngredientsChange(ingredients: Set<String>) {
		keyIngredients = if (isPremium) ingredients else emptySet()
	}

	fun onIngredientSelectionChange(pantry: Set<String>, excluded: Set<String>) {
		pantryIngredients = pantry
		excludedIngredients = excluded
	}

	fun onAddIngredientQueryChange(query: String) {
		ingredientMatchJob?.cancel()
		val trimmedQuery = query.trim()
		if (trimmedQuery.isEmpty()) {
			ingredientMatchPreview = null
			isIngredientMatchLoading = false
			return
		}
		ingredientMatchJob = viewModelScope.launch {
			delay(INGREDIENT_MATCH_DEBOUNCE_MS)
			isIngredientMatchLoading = true
			ingredientMatchPreview = matchIngredientInRecipes(trimmedQuery).get()
			isIngredientMatchLoading = false
		}
	}

	fun onAddIngredient(name: String) {
		val trimmedName = name.trim()
		if (trimmedName.isEmpty()) return
		customPantryIngredients = customPantryIngredients + trimmedName
		pantryIngredients = pantryIngredients + trimmedName
		excludedIngredients = excludedIngredients - trimmedName
		clearIngredientMatchPreview()
	}

	fun onRemoveCustomIngredient(name: String) {
		customPantryIngredients = customPantryIngredients - name
		pantryIngredients = pantryIngredients - name
		excludedIngredients = excludedIngredients - name
	}

	fun onCustomIngredientToggle(name: String) {
		if (name in excludedIngredients) {
			excludedIngredients = excludedIngredients - name
			pantryIngredients = pantryIngredients + name
		} else {
			pantryIngredients = pantryIngredients - name
			excludedIngredients = excludedIngredients + name
		}
	}

	fun clearIngredientMatchPreview() {
		ingredientMatchJob?.cancel()
		ingredientMatchPreview = null
		isIngredientMatchLoading = false
	}

	fun searchNow() {
		viewModelScope.launch { doSearch() }
	}

	val reloadHomeFeed: () -> Unit = {
		viewModelScope.launch { loadHome() }
	}

	private fun customIngredientsFromSession(
		pantry: Set<String>,
		excluded: Set<String>,
	): Set<String> = (pantry + excluded) - IngredientCatalogue.allItems

	private suspend fun reloadSessionState(sessionKey: String?) {
		val saved = getSearchFilters()
		val loadedFilters = if (saved.isEmpty) SearchFilters.default() else saved
		activeFilters = if (isPremium) loadedFilters else loadedFilters.withoutPremiumFilters()
		pantryIngredients = if (sessionKey != null) getUserPantry() else emptySet()
		excludedIngredients = if (sessionKey != null) getUserExcludedIngredients() else emptySet()
		customPantryIngredients = customIngredientsFromSession(pantryIngredients, excludedIngredients)
		lastSearchedFilters = activeFilters
		lastSavedPantry = pantryIngredients
		lastSavedExcludedIngredients = excludedIngredients
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
		refreshSearchFilterNote()
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

	private fun searchEntryIsIdle(): Boolean = !isSearchBarActive && searchQuery.isBlank()

	private fun filtersAreIdle(): Boolean {
		val filtersAreClear = activeFilters.isEmpty && keyIngredients.isEmpty()
		val resultsModeIsClear = !isSeeAllActive && !showsIngredientResults
		return filtersAreClear && resultsModeIsClear
	}

	private fun shouldRestoreHomeFeed(pantryChanged: Boolean, excludedChanged: Boolean): Boolean =
		showsHomeFeed && !pantryChanged && !excludedChanged

	private suspend fun doSearch() {
		isSearching = true
		errorMessage = null
		recipes.clear()
		nearMissRecipes.clear()
		totalMatches = 0
		paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		loadPageOfResults(FIRST_PAGE_NUMBER)
	}

	private suspend fun loadPageOfResults(pageNumber: Int) {
		val preferences = getMeasurementPreferences()
		val outcome = if (showsFavoritesResults) {
			getFavoriteRecipesPage(pageNumber, PAGE_SIZE)
		} else {
			searchRecipes(
				searchQuery,
				filtersForSearch(),
				keyIngredients = keyIngredientsForSearch(),
				pageNumber = pageNumber,
				pageSize = PAGE_SIZE,
				applyRecipeFilters = applyRecipeFiltersForSearch(),
			)
		}
		val paginatedResult = outcome.get()
		if (paginatedResult != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				recipes.clear()
				nearMissRecipes.clear()
				totalMatches = paginatedResult.totalMatches
				nearMissRecipes.addAll(
					filterNearMissRecipes(paginatedResult.nearMissRecipes, preferences),
				)
			}
			val filtered = filterRecipesForMeasurementPreferences(paginatedResult.items, preferences)
			recipes.addAll(filtered)
			val nextPageKey = pageNumber + 1
			val isLastPage = (paginatedResult.pageNumber * paginatedResult.pageSize) >= paginatedResult.totalMatches
			paginationState.appendPage(
				pageKey = pageNumber,
				items = filtered,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
			if (pageNumber == FIRST_PAGE_NUMBER && !showsFavoritesResults) {
				logBreadcrumb(CrashBreadcrumb.SEARCH_PERFORMED)
				trackEvent(
					AnalyticsEvent.SearchPerformed.from(
						SearchPerformedContext(
							query = searchQuery,
							resultCount = paginatedResult.totalMatches,
							filters = selectedFilters(),
							pantryCount = pantryIngredients.size,
							excludedCount = excludedIngredients.size,
							keyIngredientCount = selectedKeyIngredients().size,
							nearMissCount = nearMissRecipes.size,
							isPremiumUser = isPremium,
						),
					),
				)
			}
		} else {
			val error = outcome.getError()
			if (error != null) {
				if (pageNumber == FIRST_PAGE_NUMBER) {
					logBreadcrumb(CrashBreadcrumb.SEARCH_PERFORMED)
				}
				sendHandledException(error.asHandledException())
				paginationState.setError(IllegalStateException(error.message))
				errorMessage = error.message
			}
		}
		if (pageNumber == FIRST_PAGE_NUMBER) {
			refreshSearchFilterNote()
			isSearching = false
			searchReadiness.reportReady()
		}
	}

	private fun applyRecipeFiltersForSearch(): Boolean =
		searchQuery.isBlank() || searchPreferences.applyRecipeFiltersToTitleSearch

	private fun refreshSearchFilterNote() {
		searchFilterNote = formatSearchFilterNote(
			isTitleSearch = searchQuery.isNotBlank(),
			hasPantry = pantryIngredients.isNotEmpty(),
			hasExclusions = excludedIngredients.isNotEmpty(),
			hasRecipeFilters = !selectedFilters().isEmpty || selectedKeyIngredients().isNotEmpty(),
			applyRecipeFiltersToTitleSearch = searchPreferences.applyRecipeFiltersToTitleSearch,
		)
	}

	private fun selectedFilters(): SearchFilters =
	// Temporary: client still gates on isPremium (Force Premium in settings). Backend does not
	// strip premium filters while TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM is true, until
		// RevenueCat/Google Play premium sync updates app_users.is_premium.
		if (isPremium) activeFilters else activeFilters.withoutPremiumFilters()

	private fun selectedKeyIngredients(): Set<String> =
	// Temporary: client still gates on isPremium (Force Premium in settings). Backend does not
	// strip keyIngredients while TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM is true, until
		// RevenueCat/Google Play premium sync updates app_users.is_premium.
		if (isPremium) keyIngredients else emptySet()

	private fun filtersForSearch(): SearchFilters =
		if (applyRecipeFiltersForSearch()) selectedFilters() else SearchFilters()

	private fun keyIngredientsForSearch(): Set<String> =
		if (applyRecipeFiltersForSearch()) selectedKeyIngredients() else emptySet()

	private fun filterNearMissRecipes(
		nearMissRecipes: List<NearMissRecipe>,
		preferences: MeasurementPreferences,
	): List<NearMissRecipe> {
		val allowedRecipeIds = filterRecipesForMeasurementPreferences(
			recipes = nearMissRecipes.map { nearMiss -> nearMiss.recipe },
			preferences = preferences,
		).map { recipe -> recipe.id }.toSet()
		return nearMissRecipes.filter { nearMiss -> nearMiss.recipe.id in allowedRecipeIds }
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(initialShowFilterSheet: Boolean, sessionKey: String?): RecipeSearchViewModel
	}
}
