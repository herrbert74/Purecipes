package app.purecipes.feature.search.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.SearchListDetailPlaceholder
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installSearchFlow(
	isSignedIn: Boolean,
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onRequestLogInForFilters: () -> Unit,
	onOpenPaywall: (String) -> Unit,
	onCloseSearch: () -> Unit,
) {
	entry<SearchDestination>(
		metadata = ListDetailSceneStrategy.listPane(
			detailPlaceholder = { SearchListDetailPlaceholder() },
		),
	) { destination ->
		key(destination.launchId) {
			RecipeSearchScreen(
				launch = destination,
				isSignedIn = isSignedIn,
				modifier = Modifier.fillMaxSize(),
				onRecipeSelect = onRecipeSelect,
				onRequestLogInForFilters = onRequestLogInForFilters,
				onOpenPaywall = onOpenPaywall,
				closeScreen = onCloseSearch,
				sessionKey = sessionKey,
				bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
			)
		}
	}
}

fun searchNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(SearchDestination.serializer())
	}
}
