package app.purecipes.feature.recipedetails.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailsDestination(val recipeId: Int) : NavKey
