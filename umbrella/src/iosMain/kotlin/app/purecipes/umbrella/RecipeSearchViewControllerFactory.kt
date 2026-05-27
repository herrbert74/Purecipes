package app.purecipes.umbrella

import androidx.compose.ui.window.ComposeUIViewController
import app.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

class RecipeSearchViewControllerFactory {

	fun make(): UIViewController {
		val graph = createGraph<IosAppGraph>()
		IosIncomingLinkHandler.install(graph.deliverIncomingLinkUseCase)
		return ComposeUIViewController(
			configure = {
				enforceStrictPlistSanityCheck = false
			}
		) {
			MainScreen(
				observeAuthenticationState = graph.observeAuthenticationStateUseCase,
				observeMeasurementPreferences = graph.observeMeasurementPreferencesUseCase,
				observeNotificationPreferences = graph.observeNotificationPreferencesUseCase,
				refreshConsent = graph.refreshConsentUseCase,
				setAnalyticsUserId = graph.setAnalyticsUserIdUseCase,
				googleWebClientId = graph.purecipesConfig.googleWebClientId(),
				resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
				saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
				saveNotificationPreferences = graph.saveNotificationPreferencesUseCase,
				sendTestNotification = graph.sendTestNotificationUseCase,
				observeIncomingLinks = graph.observeIncomingLinksUseCase,
				publishWebLaunchLink = graph.publishWebLaunchLinkUseCase,
				metroViewModelFactory = graph.metroViewModelFactory,
			)
		}
	}
}
