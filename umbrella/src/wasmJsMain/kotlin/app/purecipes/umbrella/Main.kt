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
			observeAuthenticationState = graph.observeAuthenticationStateUseCase,
			refreshConsent = graph.refreshConsentUseCase,
			setAnalyticsUserId = graph.setAnalyticsUserIdUseCase,
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
			observeIncomingLinks = graph.observeIncomingLinksUseCase,
			publishWebLaunchLink = graph.publishWebLaunchLinkUseCase,
			metroViewModelFactory = graph.metroViewModelFactory,
		)
	})
}
