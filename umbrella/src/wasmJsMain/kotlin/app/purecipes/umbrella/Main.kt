package app.purecipes.umbrella

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val graph = createGraph<WasmAppGraph>()
	ComposeViewport(content = {
		LaunchedEffect(Unit) {
			withFrameNanos { }
			document.getElementById("splash-loader")?.remove()
		}
		MainScreen(
			metroViewModelFactory = graph.metroViewModelFactory,
		)
	})
}
