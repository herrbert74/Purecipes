package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class ObservePremiumStatusUseCase(
	private val repository: SubscriptionRepository,
) {

	operator fun invoke(): Flow<Boolean> {
		return repository.observeSubscriptionState().map { state -> state.isPremium }
	}
}
