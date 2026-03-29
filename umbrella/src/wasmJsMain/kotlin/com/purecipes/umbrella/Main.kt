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
			signInWithExternalProvider = graph.signInWithExternalProviderUseCase,
			signInWithGoogle = graph.signInWithGoogleUseCase,
			signOut = graph.signOutUseCase,
			addFavoriteRecipe = graph.addFavoriteRecipeUseCase,
			getFavoriteRecipes = graph.getFavoriteRecipesUseCase,
			searchRecipes = graph.searchRecipesUseCase,
			getRecipeDetails = graph.getRecipeDetailsUseCase,
			googleWebClientId = graph.purecipesConfig.googleWebClientId(),
			removeFavoriteRecipe = graph.removeFavoriteRecipeUseCase,
		)
	})
}
