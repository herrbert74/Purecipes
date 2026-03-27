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
			observeAuthenticationState = graph.observeAuthenticationStateUseCase,
			signInWithEmail = graph.signInWithEmailUseCase,
			registerWithEmail = graph.registerWithEmailUseCase,
			signInWithGoogle = graph.signInWithGoogleUseCase,
			signOut = graph.signOutUseCase,
			searchRecipes = graph.searchRecipesUseCase,
			getRecipeDetails = graph.getRecipeDetailsUseCase,
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
		)
	})
}
