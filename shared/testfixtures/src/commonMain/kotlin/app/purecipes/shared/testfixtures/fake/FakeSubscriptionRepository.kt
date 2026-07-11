package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSubscriptionRepository(
	initialState: SubscriptionState = SubscriptionState.FREE,
	private val subscriptionPlans: List<SubscriptionPlan> = emptyList(),
) : SubscriptionRepository {

	private val subscriptionState = MutableStateFlow(initialState)
	var initializeCalled = false
	var lastSyncedUserId: String? = null
	var purchaseCalls = 0
	var restoreCalls = 0

	override fun observeSubscriptionState(): Flow<SubscriptionState> = subscriptionState.asStateFlow()

	override fun initialize() {
		initializeCalled = true
	}

	override suspend fun getSubscriptionPlans(): Outcome<List<SubscriptionPlan>> = Ok(subscriptionPlans)

	override suspend fun syncUserId(userId: String?) {
		lastSyncedUserId = userId
	}

	override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> {
		purchaseCalls += 1
		return Ok(Unit)
	}

	override suspend fun restorePurchases(): Outcome<Unit> {
		restoreCalls += 1
		return Ok(Unit)
	}

	fun updateSubscriptionState(state: SubscriptionState) {
		subscriptionState.value = state
	}
}
