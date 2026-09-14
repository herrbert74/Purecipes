package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.feature.featurerequests.domain.model.FeatureRequestEvent
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveFeatureRequestEventsUseCase(
	private val repository: FeatureRequestsRepository,
) {

	operator fun invoke(): Flow<FeatureRequestEvent> = repository.observeFeatureRequestEvents()
}
