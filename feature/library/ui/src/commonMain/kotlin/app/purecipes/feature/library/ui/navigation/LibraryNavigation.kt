package app.purecipes.feature.library.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.library.ui.LibraryScreen
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installLibraryFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onCreateRecipe: () -> Unit,
	onEditCreatedRecipe: (Int) -> Unit,
	onRequestLogIn: () -> Unit,
) {
	entry<LibraryDestination> { destination ->
		LibraryScreen(
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
fun libraryNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(LibraryDestination.serializer())
	}
}
