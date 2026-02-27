package com.purecipes.umbrella

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.purecipes.feature.search.network.createRecipeSearchClient
import com.purecipes.feature.search.network.createRecipeSearchHttpClient
import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.feature.search.ui.RecipeSearchRoot

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val httpClient = createRecipeSearchHttpClient()
	val client = createRecipeSearchClient(httpClient)
	val repository = RecipeSearchRepository(client.api)
	ComposeViewport(content = {
		RecipeSearchRoot(repository = repository)
	})
}
