package app.purecipes.feature.cooking.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.cooking.ui.StepByStepCookingRoute
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installCookingFlow(
	navigator: Navigator,
) {
	entry<RecipeCookingDestination> { destination ->
		StepByStepCookingRoute(
			recipeId = destination.recipeId,
			onBack = { navigator.back() },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun cookingNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(RecipeCookingDestination.serializer())
	}
}
