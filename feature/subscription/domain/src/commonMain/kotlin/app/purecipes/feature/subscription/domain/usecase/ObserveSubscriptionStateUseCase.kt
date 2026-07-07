package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveSubscriptionStateUseCase(
	private val repository: SubscriptionRepository,
) {

	operator fun invoke(): Flow<SubscriptionState> = repository.observeSubscriptionState()
}
