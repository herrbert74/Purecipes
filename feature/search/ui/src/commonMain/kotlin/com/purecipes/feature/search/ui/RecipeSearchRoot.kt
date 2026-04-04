package com.purecipes.feature.search.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase

@Composable
fun RecipeSearchRoot(
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
) {
	MaterialTheme {
		RecipeSearchScreen(
			searchRecipes = searchRecipes,
			trackEvent = trackEvent,
		)
	}
}
