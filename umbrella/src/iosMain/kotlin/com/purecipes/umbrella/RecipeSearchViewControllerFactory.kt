package com.purecipes.umbrella

import androidx.compose.ui.window.ComposeUIViewController
import com.purecipes.feature.search.network.createRecipeSearchClient
import com.purecipes.feature.search.network.createRecipeSearchHttpClient
import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.feature.search.ui.RecipeSearchRoot
import platform.UIKit.UIViewController

class RecipeSearchViewControllerFactory {
	fun make(): UIViewController {
		val httpClient = createRecipeSearchHttpClient()
		val client = createRecipeSearchClient(httpClient)
		val repository = RecipeSearchRepository(client.api)
		return ComposeUIViewController {
			RecipeSearchRoot(repository = repository)
		}
	}
}
