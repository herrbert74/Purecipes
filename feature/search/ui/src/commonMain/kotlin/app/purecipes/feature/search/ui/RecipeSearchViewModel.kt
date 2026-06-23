package app.purecipes.feature.search.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.PantryDelta
import app.purecipes.shared.domain.model.RecipeFormatHandling
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
	private val trackEvent: TrackEventUseCase,
	private val getSearchFilters: GetSearchFiltersUseCase,
	private val saveSearchFilters: SaveSearchFiltersUseCase,
	private val getUserPantry: GetUserPantryUseCase,
	private val updateUserPantry: UpdateUserPantryUseCase,
	private val getUserExcludedIngredients: GetUserExcludedIngredientsUseCase,
	private val updateUserExcludedIngredients: UpdateUserExcludedIngredientsUseCase,
	private val matchIngredientInRecipes: MatchIngredientInRecipesUseCase,
	private val searchReadiness: SearchReadinessCoordinator,
	@Assisted initialShowFilterSheet: Boolean,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

	var searchQuery by mutableStateOf("")
		private set

	var isSearching by mutableStateOf(false)
		private set

	var isSearchBarActive by mutableStateOf(false)
		private set

	var isFilterSheetVisible by mutableStateOf(false)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var measurementFilterLabel by mutableStateOf<String?>(null)
		private set

	var activeFilters by mutableStateOf(SearchFilters())
		private set

	var pantryIngredients by mutableStateOf(emptySet<String>())
		private set

	var excludedIngredients by mutableStateOf(emptySet<String>())
		private set

	var customPantryIngredients by mutableStateOf(emptySet<String>())
		private set

	var ingredientMatchPreview by mutableStateOf<IngredientMatchResponse?>(null)
		private set

	var isIngredientMatchLoading by mutableStateOf(false)
		private set

	private var ingredientMatchJob: Job? = null
	private var lastSearchedFilters: SearchFilters = SearchFilters()
	private var lastSavedPantry: Set<String> = emptySet()
	private var lastSavedExcludedIngredients: Set<String> = emptySet()
	private var loadedSessionKey: String? = null

	var totalMatches by mutableIntStateOf(0)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

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
			reloadSessionState(sessionKey)
			loadedSessionKey = sessionKey
			if (initialShowFilterSheet) {
				isFilterSheetVisible = true
			}
			doSearch()
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == loadedSessionKey) return
		viewModelScope.launch {
			reloadSessionState(sessionKey)
			loadedSessionKey = sessionKey
			doSearch()
		}
	}

	fun onSearchQueryChange(query: String) {
		searchQuery = query
	}

	fun onSearchBarExpandedChange(expanded: Boolean) {
		isSearchBarActive = expanded
	}

	fun onFilterButtonClick() {
		isFilterSheetVisible = true
	}

	fun onFilterSheetDismiss() {
		isFilterSheetVisible = false
		val filtersChanged = activeFilters != lastSearchedFilters
		val pantryChanged = pantryIngredients != lastSavedPantry
		val excludedChanged = excludedIngredients != lastSavedExcludedIngredients
		if (!filtersChanged && !pantryChanged && !excludedChanged) return
		viewModelScope.launch {
			if (filtersChanged) {
				saveSearchFilters(activeFilters)
				lastSearchedFilters = activeFilters
			}
			if (pantryChanged) {
				val updatedPantry = updateUserPantry(
					PantryDelta(
						add = pantryIngredients - lastSavedPantry,
						remove = lastSavedPantry - pantryIngredients,
					),
				)
				pantryIngredients = updatedPantry
				lastSavedPantry = updatedPantry
			}
			if (excludedChanged) {
				val updatedExcludedIngredients = updateUserExcludedIngredients(
					ExcludedIngredientsDelta(
						add = excludedIngredients - lastSavedExcludedIngredients,
						remove = lastSavedExcludedIngredients - excludedIngredients,
					),
				)
				excludedIngredients = updatedExcludedIngredients
				lastSavedExcludedIngredients = updatedExcludedIngredients
			}
			doSearch()
		}
	}

	fun onFiltersChange(filters: SearchFilters) {
		activeFilters = filters
	}

	fun onPantryIngredientsChange(ingredients: Set<String>) {
		pantryIngredients = ingredients
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

	private fun customIngredientsFromSession(
		pantry: Set<String>,
		excluded: Set<String>,
	): Set<String> = (pantry + excluded) - IngredientCatalogue.allItems

	private suspend fun reloadSessionState(sessionKey: String?) {
		val saved = getSearchFilters()
		activeFilters = if (saved.isEmpty) SearchFilters.default() else saved
		pantryIngredients = if (sessionKey != null) getUserPantry() else emptySet()
		excludedIngredients = if (sessionKey != null) getUserExcludedIngredients() else emptySet()
		customPantryIngredients = customIngredientsFromSession(pantryIngredients, excludedIngredients)
		lastSearchedFilters = activeFilters
		lastSavedPantry = pantryIngredients
		lastSavedExcludedIngredients = excludedIngredients
	}

	private suspend fun doSearch() {
		isSearching = true
		errorMessage = null
		recipes.clear()
		totalMatches = 0
		paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		loadPageOfResults(FIRST_PAGE_NUMBER)
	}

	private suspend fun loadPageOfResults(pageNumber: Int) {
		val preferences = getMeasurementPreferences()
		val outcome = searchRecipes(searchQuery, activeFilters, pageNumber, PAGE_SIZE)
		val paginatedResult = outcome.get()
		if (paginatedResult != null) {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				recipes.clear()
				totalMatches = paginatedResult.totalMatches
			}
			val filtered = filterRecipesForMeasurementPreferences(paginatedResult.items, preferences)
			recipes.addAll(filtered)
			measurementFilterLabel = preferences.filterSummary()
			val nextPageKey = pageNumber + 1
			val isLastPage = (paginatedResult.pageNumber * paginatedResult.pageSize) >= paginatedResult.totalMatches
			paginationState.appendPage(
				pageKey = pageNumber,
				items = filtered,
				nextPageKey = nextPageKey,
				isLastPage = isLastPage,
			)
			if (pageNumber == FIRST_PAGE_NUMBER) {
				trackEvent(
					AnalyticsEvent.SearchPerformed(
						query = searchQuery,
						resultCount = paginatedResult.totalMatches,
					),
				)
			}
		} else {
			val error = outcome.getError()
			if (error != null) {
				paginationState.setError(IllegalStateException(error.message))
				errorMessage = error.message
			}
		}
		if (pageNumber == FIRST_PAGE_NUMBER) {
			isSearching = false
			searchReadiness.reportReady()
		}
	}

	private fun MeasurementPreferences.filterSummary(): String? {
		return when (formatHandling) {
			RecipeFormatHandling.FILTER_OUT -> {
				val systemLabel = if (preferredSystem == MeasurementSystem.IMPERIAL) "imperial" else "metric"
				"Showing $systemLabel recipes only"
			}

			RecipeFormatHandling.CONVERT_TO_PREFERRED -> "Recipe details will use your preferred measurements"
			RecipeFormatHandling.KEEP_AS_IS -> null
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(initialShowFilterSheet: Boolean, sessionKey: String?): RecipeSearchViewModel
	}
}
