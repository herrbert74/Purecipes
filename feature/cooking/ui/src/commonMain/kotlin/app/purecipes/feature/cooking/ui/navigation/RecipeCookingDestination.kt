package app.purecipes.feature.cooking.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RecipeCookingDestination(val recipeId: Int) : NavKey
