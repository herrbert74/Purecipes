package app.purecipes.feature.newrecipe.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.newrecipe.ui.CreateRecipeScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

inline fun EntryProviderScope<NavKey>.installCreateFlow(
	canUploadRecipes: Boolean,
) {
	entry<CreateDestination> {
		CreateRecipeScreen(
			canUploadRecipes = canUploadRecipes,
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun createNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(CreateDestination.serializer())
	}
}
