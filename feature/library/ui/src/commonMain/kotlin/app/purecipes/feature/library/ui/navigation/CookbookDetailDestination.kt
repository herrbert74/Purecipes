package app.purecipes.feature.library.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CookbookDetailDestination(
	val cookbookId: Int,
	val name: String,
) : NavKey
