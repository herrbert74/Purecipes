package com.purecipes.umbrella

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val graph = createGraph<WasmAppGraph>()
	ComposeViewport(content = {
		MainScreen(
			observeConsentState = graph.observeConsentStateUseCase,
			observeAuthenticationState = graph.observeAuthenticationStateUseCase,
			observeMeasurementPreferences = graph.observeMeasurementPreferencesUseCase,
			refreshConsent = graph.refreshConsentUseCase,
			setAnalyticsUserId = graph.setAnalyticsUserIdUseCase,
			showConsentForm = graph.showConsentFormUseCase,
			signInWithEmail = graph.signInWithEmailUseCase,
			registerWithEmail = graph.registerWithEmailUseCase,
			signInWithExternalProvider = graph.signInWithExternalProviderUseCase,
			signInWithGoogle = graph.signInWithGoogleUseCase,
			signOut = graph.signOutUseCase,
			addFavoriteRecipe = graph.addFavoriteRecipeUseCase,
			filterRecipesForMeasurementPreferences = graph.filterRecipesForMeasurementPreferencesUseCase,
			getCreatedRecipes = graph.getCreatedRecipesUseCase,
			getFavoriteRecipes = graph.getFavoriteRecipesUseCase,
			getMeasurementPreferences = graph.getMeasurementPreferencesUseCase,
			searchRecipes = graph.searchRecipesUseCase,
			getRecipeDetails = graph.getRecipeDetailsUseCase,
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
			markMeasurementMismatchSeen = graph.markMeasurementMismatchSeenUseCase,
			processRecipeDetailsForMeasurementPreferences = graph.processRecipeDetailsForMeasurementPreferencesUseCase,
			removeFavoriteRecipe = graph.removeFavoriteRecipeUseCase,
			resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
			saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
			saveCreatedRecipe = graph.saveCreatedRecipeUseCase,
			trackEvent = graph.trackEventUseCase,
		)
	})
}
