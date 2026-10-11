package app.purecipes.feature.search.ui.navigation

import androidx.navigation3.runtime.NavKey
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchFilters
import kotlinx.serialization.Serializable

@Serializable
data class SearchDestination(
	val openFiltersOnStart: Boolean = false,
	val initialFilters: SearchFilters = SearchFilters(),
	val seeAllShelf: HomeShelfId? = null,
	val launchId: Long = 0,
) : NavKey
