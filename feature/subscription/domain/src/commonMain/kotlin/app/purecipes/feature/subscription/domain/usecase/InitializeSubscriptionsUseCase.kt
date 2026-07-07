package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject

@Inject
class InitializeSubscriptionsUseCase(
	private val repository: SubscriptionRepository,
) {

	operator fun invoke() {
		repository.initialize()
	}
}
