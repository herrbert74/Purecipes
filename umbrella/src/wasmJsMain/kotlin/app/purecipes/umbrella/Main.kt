package app.purecipes.umbrella

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val graph = createGraph<WasmAppGraph>()
	ComposeViewport(content = {
		MainScreen(
			observeConsentState = graph.observeConsentStateUseCase,
			observeAuthenticationState = graph.observeAuthenticationStateUseCase,
			observeMeasurementPreferences = graph.observeMeasurementPreferencesUseCase,
			observeNotificationPreferences = graph.observeNotificationPreferencesUseCase,
			refreshConsent = graph.refreshConsentUseCase,
			setAnalyticsUserId = graph.setAnalyticsUserIdUseCase,
			showConsentForm = graph.showConsentFormUseCase,
			getCreatedRecipes = graph.getCreatedRecipesUseCase,
			getFavoriteRecipesPage = graph.getFavoriteRecipesPageUseCase,
			getCookbooksPage = graph.getCookbooksPageUseCase,
			createCookbook = graph.createCookbookUseCase,
			deleteCookbook = graph.deleteCookbookUseCase,
			getCookbookRecipesPage = graph.getCookbookRecipesPageUseCase,
			getCookbookCoverImageUrl = graph.getCookbookCoverImageUrlUseCase,
			getMeasurementPreferences = graph.getMeasurementPreferencesUseCase,
			getRecipeDetails = graph.getRecipeDetailsUseCase,
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
			processRecipeDetailsForMeasurementPreferences = graph.processRecipeDetailsForMeasurementPreferencesUseCase,
			resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
			saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
			saveNotificationPreferences = graph.saveNotificationPreferencesUseCase,
			sendTestNotification = graph.sendTestNotificationUseCase,
			saveCreatedRecipe = graph.saveCreatedRecipeUseCase,
			estimateRecipeNutrition = graph.estimateRecipeNutritionUseCase,
			trackEvent = graph.trackEventUseCase,
			observeIncomingLinks = graph.observeIncomingLinksUseCase,
			publishWebLaunchLink = graph.publishWebLaunchLinkUseCase,
			shareRecipe = graph.shareRecipeUseCase,
			shareCookbook = graph.shareCookbookUseCase,
			importCookbookShare = graph.importCookbookShareUseCase,
			metroViewModelFactory = graph.metroViewModelFactory,
		)
	})
}
