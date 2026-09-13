package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequest
import dev.zacsweers.metro.Inject

@Inject
class GetFeatureRequestUseCase(
	private val repository: FeatureRequestsRepository,
) {

	suspend operator fun invoke(requestId: Int): Outcome<FeatureRequest> {
		return repository.getFeatureRequest(requestId)
	}
}
