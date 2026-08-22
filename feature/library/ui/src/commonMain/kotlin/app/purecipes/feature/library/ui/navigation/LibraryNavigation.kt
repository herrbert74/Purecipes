package app.purecipes.feature.library.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.library.ui.CookbookDetailScreen
import app.purecipes.feature.library.ui.LibraryListDetailPlaceholder
import app.purecipes.feature.library.ui.LibraryScreen
import app.purecipes.shared.domain.model.CookbookSummary
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installLibraryFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onCookbookSelect: (CookbookSummary) -> Unit,
	onCookbookImportSuccess: (cookbookId: Int, name: String, recipeCount: Int) -> Unit,
	onCreateRecipe: () -> Unit,
	onEditCreatedRecipe: (Int) -> Unit,
	onRequestLogIn: () -> Unit,
) {
	entry<LibraryDestination>(
		metadata = ListDetailSceneStrategy.listPane(
			detailPlaceholder = { LibraryListDetailPlaceholder() },
		),
	) { destination ->
		LibraryScreen(
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			initialCookbookShareToken = destination.cookbookShareToken,
			openMyRecipes = destination.openMyRecipes,
			recipeSaveMessage = destination.recipeSaveMessage,
			onRecipeSelect = onRecipeSelect,
			onCookbookSelect = onCookbookSelect,
			onCookbookImportSuccess = onCookbookImportSuccess,
			onCreateRecipe = onCreateRecipe,
			onEditCreatedRecipe = onEditCreatedRecipe,
			onRequestLogIn = onRequestLogIn,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
		)
	}
}

fun EntryProviderScope<NavKey>.installCookbookDetailFlow(
	sessionKey: String?,
	onRecipeSelect: (Int) -> Unit,
	onBack: () -> Unit,
) {
	entry<CookbookDetailDestination>(
		metadata = ListDetailSceneStrategy.detailPane(),
	) { destination ->
		CookbookDetailScreen(
			cookbookId = destination.cookbookId,
			name = destination.name,
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			onBack = onBack,
			onRecipeSelect = onRecipeSelect,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
		)
	}
}

fun libraryNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(LibraryDestination.serializer())
		subclass(CookbookDetailDestination.serializer())
	}
}
