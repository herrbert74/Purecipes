package com.purecipes.umbrella

import androidx.compose.ui.window.ComposeUIViewController
import com.purecipes.feature.search.ui.RecipeSearchRoot
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

class RecipeSearchViewControllerFactory {
	fun make(): UIViewController {
		val graph = createGraph<IosAppGraph>()
		return ComposeUIViewController(
			configure = {
				enforceStrictPlistSanityCheck = false
			}
		) {
			RecipeSearchRoot(repository = graph.recipeSearchRepository)
		}
	}
}
