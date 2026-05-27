package app.purecipes.feature.search.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchDestination(
	val openFiltersOnStart: Boolean = false,
) : NavKey
