package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

const val HOME_SHELF_PAGE_SIZE = 12

@Serializable
enum class HomeShelfId {

	NEW,
	RIGHT_NOW,
	QUICK,
	FAVORITES,
}

@Serializable
data class HomeShelf(
	val id: HomeShelfId,
	val title: String,
	val filters: SearchFilters = SearchFilters(),
	val recipes: List<RecipeSummary> = emptyList(),
)

@Serializable
data class HomeFeed(
	val shelves: List<HomeShelf> = emptyList(),
	val featureRequest: FeatureRequest? = null,
)
