package app.purecipes.feature.search.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.search.ui.RecipeSearchScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installSearchFlow(
	isSignedIn: Boolean,
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onRequestLogInForFilters: () -> Unit,
) {
	entry<SearchDestination> { destination ->
		RecipeSearchScreen(
			initialShowFilterSheet = destination.openFiltersOnStart,
			isSignedIn = isSignedIn,
			modifier = Modifier.fillMaxSize(),
			onRecipeSelect = onRecipeSelect,
			onRequestLogInForFilters = onRequestLogInForFilters,
			sessionKey = sessionKey,
		)
	}
}

fun searchNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(SearchDestination.serializer())
	}
}
