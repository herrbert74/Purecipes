package com.purecipes.feature.search.ui

import androidx.compose.runtime.Composable
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun RecipeSearchRoot(
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
	getSearchFilters: GetSearchFiltersUseCase,
	saveSearchFilters: SaveSearchFiltersUseCase,
) {
	PurecipesTheme {
		RecipeSearchScreen(
			filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
			getMeasurementPreferences = getMeasurementPreferences,
			searchRecipes = searchRecipes,
			trackEvent = trackEvent,
			getSearchFilters = getSearchFilters,
			saveSearchFilters = saveSearchFilters,
		)
	}
}
