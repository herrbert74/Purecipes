package app.purecipes.feature.main.ui

import app.purecipes.feature.auth.ui.navigation.authNavigationSerializersModule
import app.purecipes.feature.cooking.ui.navigation.cookingNavigationSerializersModule
import app.purecipes.feature.favorites.ui.navigation.favoritesNavigationSerializersModule
import app.purecipes.feature.newrecipe.ui.navigation.createNavigationSerializersModule
import app.purecipes.feature.recipedetails.ui.navigation.recipeDetailsNavigationSerializersModule
import app.purecipes.feature.search.ui.navigation.searchNavigationSerializersModule
import app.purecipes.feature.settings.ui.navigation.settingsNavigationSerializersModule
import kotlinx.serialization.modules.SerializersModule

internal fun mainNavigationSerializersModule(): SerializersModule = SerializersModule {
	include(searchNavigationSerializersModule())
	include(recipeDetailsNavigationSerializersModule())
	include(cookingNavigationSerializersModule())
	include(favoritesNavigationSerializersModule())
	include(createNavigationSerializersModule())
	include(authNavigationSerializersModule())
	include(settingsNavigationSerializersModule())
}
