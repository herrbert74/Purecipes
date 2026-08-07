package app.purecipes.feature.newrecipe.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.newrecipe.ui.CreateRecipeScreen
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installCreateFlow(
	navigator: Navigator,
	canUploadRecipes: Boolean,
	onSaveSuccess: () -> Unit,
) {
	entry<CreateDestination> {
		CreateRecipeScreen(
			canUploadRecipes = canUploadRecipes,
			onSaveSuccess = onSaveSuccess,
			modifier = Modifier.fillMaxSize(),
		)
	}
	entry<CreateEditorDestination> { destination ->
		CreateRecipeScreen(
			canUploadRecipes = canUploadRecipes,
			recipeId = destination.recipeId,
			onBack = { navigator.back() },
			onSaveSuccess = onSaveSuccess,
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun createNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(CreateDestination.serializer())
		subclass(CreateEditorDestination.serializer())
	}
}
