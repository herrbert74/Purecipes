package com.purecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.feature.search.ui.RecipeSearchScreen
import com.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraph

class MainActivity : ComponentActivity() {

	@Inject
	lateinit var repository: RecipeSearchRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		val graph = createGraph<PurecipesAppGraph>()
		graph.inject(this)

		setContent {
			PurecipesTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					RecipeSearchScreen(
						modifier = Modifier.padding(innerPadding),
						repository = repository,
					)
				}
			}
		}
	}
}
