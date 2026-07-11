package app.purecipes.feature.recipedetails.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.shared.ui.navigation.Navigator
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installRecipeDetailsFlow(
	navigator: Navigator,
	canManageFavorites: Boolean,
	sessionKey: String?,
	onStartCooking: (Int) -> Unit,
	onOpenMeasurementPreferences: () -> Unit,
) {
	entry<RecipeDetailsDestination> { destination ->
		RecipeDetailsScreen(
			recipeId = destination.recipeId,
			canManageFavorites = canManageFavorites,
			onOpenMeasurementPreferences = onOpenMeasurementPreferences,
			onBack = { navigator.back() },
			onStartCooking = onStartCooking,
			sessionKey = sessionKey,
			bannerAdViewModel = metroViewModel<BannerAdViewModel>(),
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun recipeDetailsNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(RecipeDetailsDestination.serializer())
	}
}
