package app.purecipes.feature.subscription.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import kotlinx.coroutines.flow.Flow

interface SubscriptionDataSource {

	fun observeSubscriptionState(): Flow<SubscriptionState>

	fun initialize(apiKey: String?)

	suspend fun getSubscriptionPlans(): Outcome<List<SubscriptionPlan>>

	suspend fun syncUserId(userId: String?)

	suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit>

	suspend fun restorePurchases(): Outcome<Unit>
}
