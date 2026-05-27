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
			metroViewModelFactory = graph.metroViewModelFactory,
		)
	})
}
