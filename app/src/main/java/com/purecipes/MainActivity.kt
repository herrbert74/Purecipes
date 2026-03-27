package com.purecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val graph = createGraph<PurecipesAppGraph>()

		setContent {
			MainScreen(
				observeAuthenticationState = graph.observeAuthenticationStateUseCase,
				signInWithEmail = graph.signInWithEmailUseCase,
				registerWithEmail = graph.registerWithEmailUseCase,
				signInWithGoogle = graph.signInWithGoogleUseCase,
				signOut = graph.signOutUseCase,
				searchRecipes = graph.searchRecipesUseCase,
				getRecipeDetails = graph.getRecipeDetailsUseCase,
				googleWebClientId = graph.purecipesConfig.googleWebClientId(),
				onExitRequest = ::finish,
			)
		}
	}
}
