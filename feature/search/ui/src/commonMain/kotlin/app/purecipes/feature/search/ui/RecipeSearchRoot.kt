package app.purecipes.feature.search.ui

import androidx.compose.runtime.Composable
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun RecipeSearchRoot(
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
	getSearchFilters: GetSearchFiltersUseCase,
	saveSearchFilters: SaveSearchFiltersUseCase,
	getUserPantry: GetUserPantryUseCase,
	updateUserPantry: UpdateUserPantryUseCase,
) {
	PurecipesTheme {
		RecipeSearchScreen(
			filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
			getMeasurementPreferences = getMeasurementPreferences,
			searchRecipes = searchRecipes,
			trackEvent = trackEvent,
			getSearchFilters = getSearchFilters,
			saveSearchFilters = saveSearchFilters,
			getUserPantry = getUserPantry,
			updateUserPantry = updateUserPantry,
		)
	}
}
