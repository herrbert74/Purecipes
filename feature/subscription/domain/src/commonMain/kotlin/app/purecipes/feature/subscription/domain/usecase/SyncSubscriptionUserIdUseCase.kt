package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject

@Inject
class SyncSubscriptionUserIdUseCase(
	private val repository: SubscriptionRepository,
) {

	suspend operator fun invoke(userId: String?) {
		repository.syncUserId(userId)
	}
}
