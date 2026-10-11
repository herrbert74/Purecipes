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
	): Outcome<SearchResultsPage> = repository.getShelfPage(shelfId, pageNumber, pageSize)
}
