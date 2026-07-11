package app.purecipes.feature.subscription.data.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.data.datasource.SubscriptionDataSource
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class)
class SubscriptionAccessor(
	private val subscriptionDataSource: SubscriptionDataSource,
	private val purecipesConfig: PurecipesConfig,
) : SubscriptionRepository {

	override fun observeSubscriptionState(): Flow<SubscriptionState> =
		subscriptionDataSource.observeSubscriptionState()

	override fun initialize() {
		subscriptionDataSource.initialize(purecipesConfig.revenueCatApiKey())
	}

	override suspend fun getSubscriptionPlans(): Outcome<List<SubscriptionPlan>> =
		subscriptionDataSource.getSubscriptionPlans()

	override suspend fun syncUserId(userId: String?) {
		subscriptionDataSource.syncUserId(userId)
	}

	override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> {
		return subscriptionDataSource.purchase(packageIdentifier)
	}

	override suspend fun restorePurchases(): Outcome<Unit> {
		return subscriptionDataSource.restorePurchases()
	}
}
