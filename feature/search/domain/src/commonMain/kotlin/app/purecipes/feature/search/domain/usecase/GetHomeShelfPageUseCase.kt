package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.HomeFeedRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
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
	): SearchOutcome<SearchResultsPage> = repository.getShelfPage(shelfId, pageNumber, pageSize)
}
