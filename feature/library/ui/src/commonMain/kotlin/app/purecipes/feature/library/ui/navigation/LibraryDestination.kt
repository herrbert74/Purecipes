package app.purecipes.feature.library.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class LibraryDestination(
	val cookbookShareToken: String? = null,
	val openMyRecipes: Boolean = false,
	val recipeSaveMessage: String? = null,
) : NavKey
