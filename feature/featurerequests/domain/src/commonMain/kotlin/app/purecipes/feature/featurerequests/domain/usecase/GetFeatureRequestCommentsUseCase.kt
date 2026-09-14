package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequestComment
import dev.zacsweers.metro.Inject

@Inject
class GetFeatureRequestCommentsUseCase(
	private val repository: FeatureRequestsRepository,
) {

	suspend operator fun invoke(requestId: Int): Outcome<List<FeatureRequestComment>> {
		return repository.getFeatureRequestComments(requestId)
	}
}
