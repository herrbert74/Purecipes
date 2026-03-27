package com.purecipes.umbrella

import androidx.compose.ui.window.ComposeUIViewController
import com.purecipes.feature.main.ui.MainScreen
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
		}
	}
}
