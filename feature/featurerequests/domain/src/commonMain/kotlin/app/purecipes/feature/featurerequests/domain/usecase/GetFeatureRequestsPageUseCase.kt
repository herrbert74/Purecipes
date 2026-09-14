package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import dev.zacsweers.metro.Inject

@Inject
class GetFeatureRequestsPageUseCase(
	private val repository: FeatureRequestsRepository,
) {

	suspend operator fun invoke(
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<FeatureRequestListPage> {
		return repository.getFeatureRequestsPage(sort, status, pageNumber, pageSize)
	}
}
