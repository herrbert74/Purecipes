package app.purecipes.feature.recipedetails.ui.navigation

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailsDestination(
	val recipeId: Int,
	val origin: String = AnalyticsOrigin.SEARCH.value,
) : NavKey
