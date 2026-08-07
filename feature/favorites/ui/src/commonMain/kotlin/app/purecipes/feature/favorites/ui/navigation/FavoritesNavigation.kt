package app.purecipes.feature.favorites.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.favorites.ui.FavoritesScreen
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installFavoritesFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onCreateRecipe: () -> Unit,
	onEditCreatedRecipe: (Int) -> Unit,
	onRequestLogIn: () -> Unit,
) {
	entry<FavoritesDestination> { destination ->
		FavoritesScreen(
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			initialCookbookShareToken = destination.cookbookShareToken,
			openMyRecipes = destination.openMyRecipes,
			recipeSaveMessage = destination.recipeSaveMessage,
			onRecipeSelect = onRecipeSelect,
			onCreateRecipe = onCreateRecipe,
			onEditCreatedRecipe = onEditCreatedRecipe,
			onRequestLogIn = onRequestLogIn,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
		)
	}
}

fun favoritesNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(FavoritesDestination.serializer())
	}
}
