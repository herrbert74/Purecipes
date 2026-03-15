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
			recipeSearchRepository = graph.recipeSearchRepository,
			recipeDetailsRepository = graph.recipeDetailsRepository,
		)
	})
}
