package app.purecipes.feature.favorites.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class FavoritesDestination(
	val cookbookShareToken: String? = null,
	val openMyRecipes: Boolean = false,
) : NavKey
