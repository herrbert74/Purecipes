package app.purecipes.feature.home.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.Inject

@Inject
class GetHomeShelfPageUseCase(
	private val repository: HomeFeedRepository,
) {

	suspend operator fun invoke(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
		collectionSlug: String? = null,
		habitCuisine: String? = null,
	): Outcome<SearchResultsPage> = if (collectionSlug.isNullOrBlank()) {
		repository.getShelfPage(
			shelfId = shelfId,
			pageNumber = pageNumber,
			pageSize = pageSize,
			habitCuisine = habitCuisine,
		)
	} else {
		repository.getCollectionPage(collectionSlug, pageNumber, pageSize)
	}
}
