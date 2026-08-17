package app.purecipes.feature.main.ui

import androidx.lifecycle.viewmodel.CreationExtras
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import app.purecipes.feature.subscription.ui.PaywallViewModel
import dev.zacsweers.metro.createGraph
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.test.Test

class CommonAppGraphMetroTest {

	@Test
	fun metroViewModelFactoryIncludesBannerAdViewModel() {
		val graph = createGraph<TestAppGraph>()
		val viewModel = graph.metroViewModelFactory.create(
			BannerAdViewModel::class,
			CreationExtras.Empty,
		)
		viewModel.shouldNotBeNull()
	}

	@Test
	fun metroViewModelFactoryIncludesPaywallManualAssistedFactory() {
		val graph = createGraph<TestAppGraph>()
		val factoryProvider = graph.metroViewModelFactory.createManuallyAssistedFactory(
			PaywallViewModel.Factory::class,
		)
		factoryProvider().shouldNotBeNull()
	}

	@Test
	fun metroViewModelFactoryIncludesRecipeSearchManualAssistedFactory() {
		val graph = createGraph<TestAppGraph>()
		val factoryProvider = graph.metroViewModelFactory.createManuallyAssistedFactory(
			RecipeSearchViewModel.Factory::class,
		)
		factoryProvider().shouldNotBeNull()
	}
}
