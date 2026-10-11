package app.purecipes.feature.home.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.home.ui.HomeListDetailPlaceholder
import app.purecipes.feature.home.ui.HomeScreen
import app.purecipes.feature.home.ui.HomeShelfScreen
import app.purecipes.shared.domain.model.HomeShelf
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installHomeFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onOpenShelf: (HomeShelf) -> Unit,
	onOpenFeatureRequests: () -> Unit,
) {
	entry<HomeDestination>(
		metadata = ListDetailSceneStrategy.listPane(
			detailPlaceholder = { HomeListDetailPlaceholder() },
		),
	) {
		HomeScreen(
			modifier = Modifier.fillMaxSize(),
			onRecipeSelect = onRecipeSelect,
			onOpenShelf = onOpenShelf,
			onOpenFeatureRequests = onOpenFeatureRequests,
			sessionKey = sessionKey,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
		)
	}
}

fun EntryProviderScope<NavKey>.installHomeShelfFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onBack: () -> Unit,
) {
	entry<HomeShelfDestination>(
		metadata = ListDetailSceneStrategy.listPane(
			detailPlaceholder = { HomeListDetailPlaceholder() },
		),
	) { destination ->
		HomeShelfScreen(
			title = destination.title,
			shelfId = destination.shelfId,
			collectionSlug = destination.collectionSlug,
			habitCuisine = destination.habitCuisine,
			onBack = onBack,
			onRecipeSelect = onRecipeSelect,
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
		)
	}
}

fun homeNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(HomeDestination.serializer())
		subclass(HomeShelfDestination.serializer())
	}
}
