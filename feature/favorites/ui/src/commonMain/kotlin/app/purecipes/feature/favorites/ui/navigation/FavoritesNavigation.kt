package app.purecipes.feature.favorites.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.favorites.ui.FavoritesScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

inline fun EntryProviderScope<NavKey>.installFavoritesFlow(
	refreshSignal: Int,
	sessionKey: String?,
	initialCookbookShareToken: String?,
	noinline onRecipeSelect: (Int) -> Unit,
) {
	entry<FavoritesDestination> {
		FavoritesScreen(
			refreshSignal = refreshSignal,
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			initialCookbookShareToken = initialCookbookShareToken,
			onRecipeSelect = onRecipeSelect,
		)
	}
}

fun favoritesNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(FavoritesDestination.serializer())
	}
}
