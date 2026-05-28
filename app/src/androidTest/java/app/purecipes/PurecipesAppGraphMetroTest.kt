package app.purecipes

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import dev.zacsweers.metro.createGraph
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PurecipesAppGraphMetroTest {

	@Test
	fun metroViewModelFactoryIncludesRecipeSearchManualAssistedFactory() {
		val graph = createGraph<PurecipesAppGraph>()
		val factoryProvider = graph.metroViewModelFactory.createManuallyAssistedFactory(
			RecipeSearchViewModel.Factory::class,
		)
		assertNotNull(factoryProvider())
	}
}
