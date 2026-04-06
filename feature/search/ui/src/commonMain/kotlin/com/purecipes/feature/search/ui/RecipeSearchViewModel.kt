package com.purecipes.feature.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class RecipeSearchViewModel(
	private val filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val searchRecipes: SearchRecipesUseCase,
	private val trackEvent: TrackEventUseCase,
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

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var measurementFilterLabel by mutableStateOf<String?>(null)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	init {
		searchNow()
	}

	fun onSearchQueryChange(query: String) {
		searchQuery = query
	}

	fun onSearchBarExpandedChange(expanded: Boolean) {
		isSearchBarActive = expanded
	}

	fun searchNow() {
		scope.launch {
			isSearching = true
			errorMessage = null
			val preferences = getMeasurementPreferences()
			val outcome = searchRecipes(searchQuery)
			recipes.clear()
			recipes.addAll(filterRecipesForMeasurementPreferences(outcome.get() ?: emptyList(), preferences))
			measurementFilterLabel = preferences.filterSummary()
			trackEvent(AnalyticsEvent.SearchPerformed(query = searchQuery, resultCount = recipes.size))
			errorMessage = outcome.getError()?.message
			isSearching = false
			isSearchBarActive = false
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
): RecipeSearchViewModel {
	val viewModelKey = buildString {
		append("RecipeSearchViewModel:")
		append(searchRecipes.hashCode())
		append(':')
		append(getMeasurementPreferences.hashCode())
		append(':')
		append(trackEvent.hashCode())
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
				)
			}
		},
	)
}
