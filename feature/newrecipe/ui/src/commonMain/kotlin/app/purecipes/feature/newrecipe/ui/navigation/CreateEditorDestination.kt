package app.purecipes.feature.newrecipe.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CreateEditorDestination(
	val recipeId: Int,
) : NavKey
