package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject

@Inject
class GetSubscriptionPlansUseCase(
	private val repository: SubscriptionRepository,
) {

	suspend operator fun invoke(): Outcome<List<SubscriptionPlan>> = repository.getSubscriptionPlans()
}
