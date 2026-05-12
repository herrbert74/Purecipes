package app.purecipes.feature.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.PantryDelta
import app.purecipes.shared.domain.model.RecipeFormatHandling
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.paging.PaginationState
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val FIRST_PAGE_NUMBER = 1
private const val PAGE_SIZE = 20

internal class RecipeSearchViewModel(
	private val filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val searchRecipes: SearchRecipesUseCase,
	private val trackEvent: TrackEventUseCase,
	private val getSearchFilters: GetSearchFiltersUseCase,
	private val saveSearchFilters: SaveSearchFiltersUseCase,
	private val getUserPantry: GetUserPantryUseCase,
	private val updateUserPantry: UpdateUserPantryUseCase,
	private val initialShowFilterSheet: Boolean,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

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

	private var lastSearchedFilters: SearchFilters = SearchFilters()
	private var lastSavedPantry: Set<String> = emptySet()

	var totalMatches by mutableIntStateOf(0)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	val paginationState: PaginationState<Int, RecipeSummary> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			scope.launch {
				loadPageOfResults(pageKey)
			}
		},
	)

	init {
		scope.launch {
			val saved = getSearchFilters()
			activeFilters = if (saved.isEmpty) SearchFilters.default() else saved
			pantryIngredients = getUserPantry()
			lastSearchedFilters = activeFilters
			lastSavedPantry = pantryIngredients
			if (initialShowFilterSheet) {
				isFilterSheetVisible = true
			}
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
		if (!filtersChanged && !pantryChanged) return
		scope.launch {
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
			doSearch()
		}
	}

	fun onFiltersChange(filters: SearchFilters) {
		activeFilters = filters
	}

	fun onPantryIngredientsChange(ingredients: Set<String>) {
		pantryIngredients = ingredients
	}

	fun searchNow() {
		scope.launch { doSearch() }
	}

	private suspend fun doSearch() {
		isSearching = true
		errorMessage = null
		recipes.clear()
		totalMatches = 0
		paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
		loadPageOfResults(FIRST_PAGE_NUMBER)
		isSearchBarActive = false
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
		}
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
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
}

@Composable
internal fun recipeSearchViewModel(
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
	getSearchFilters: GetSearchFiltersUseCase,
	saveSearchFilters: SaveSearchFiltersUseCase,
	getUserPantry: GetUserPantryUseCase,
	updateUserPantry: UpdateUserPantryUseCase,
	initialShowFilterSheet: Boolean,
	sessionKey: String?,
): RecipeSearchViewModel {
	val viewModelKey = buildString {
		append("RecipeSearchViewModel:")
		append(searchRecipes.hashCode())
		append(':')
		append(getMeasurementPreferences.hashCode())
		append(':')
		append(trackEvent.hashCode())
		append(':')
		append(getSearchFilters.hashCode())
		append(':')
		append(saveSearchFilters.hashCode())
		append(':')
		append(getUserPantry.hashCode())
		append(':')
		append(updateUserPantry.hashCode())
		append(':')
		append(sessionKey ?: "guest")
	}
	return viewModel(
		key = viewModelKey,
		factory = viewModelFactory {
			initializer {
				RecipeSearchViewModel(
					filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
					getMeasurementPreferences = getMeasurementPreferences,
					searchRecipes = searchRecipes,
					trackEvent = trackEvent,
					getSearchFilters = getSearchFilters,
					saveSearchFilters = saveSearchFilters,
					getUserPantry = getUserPantry,
					updateUserPantry = updateUserPantry,
					initialShowFilterSheet = initialShowFilterSheet,
				)
			}
		},
	)
}
