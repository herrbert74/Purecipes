package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject

@Inject
class PurchaseSubscriptionUseCase(
	private val repository: SubscriptionRepository,
) {

	suspend operator fun invoke(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> {
		return repository.purchase(packageIdentifier)
	}
}
