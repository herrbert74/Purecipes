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
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
			resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
			saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
			saveNotificationPreferences = graph.saveNotificationPreferencesUseCase,
			sendTestNotification = graph.sendTestNotificationUseCase,
			observeIncomingLinks = graph.observeIncomingLinksUseCase,
			publishWebLaunchLink = graph.publishWebLaunchLinkUseCase,
			shareRecipe = graph.shareRecipeUseCase,
			shareCookbook = graph.shareCookbookUseCase,
			metroViewModelFactory = graph.metroViewModelFactory,
		)
	})
}
