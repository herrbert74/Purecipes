package app.purecipes.feature.subscription.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

	fun observeSubscriptionState(): Flow<SubscriptionState>

	fun initialize()

	suspend fun syncUserId(userId: String?)

	suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit>

	suspend fun restorePurchases(): Outcome<Unit>
}
