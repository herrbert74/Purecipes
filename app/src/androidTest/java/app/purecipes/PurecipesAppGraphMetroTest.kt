package app.purecipes

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import app.purecipes.feature.subscription.ui.PaywallViewModel
import dev.zacsweers.metro.createGraph
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PurecipesAppGraphMetroTest {

	@Test
	fun metroViewModelFactoryIncludesPaywallViewModel() {
		val graph = createGraph<PurecipesAppGraph>()
		val viewModel = graph.metroViewModelFactory.create(PaywallViewModel::class.java)
		assertNotNull(viewModel)
	}

	@Test
	fun metroViewModelFactoryIncludesRecipeSearchManualAssistedFactory() {
		val graph = createGraph<PurecipesAppGraph>()
		val factoryProvider = graph.metroViewModelFactory.createManuallyAssistedFactory(
			RecipeSearchViewModel.Factory::class,
		)
		assertNotNull(factoryProvider())
	}
}
