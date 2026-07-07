package app.purecipes.feature.subscription.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSubscriptionDataSource(
	initialState: SubscriptionState = SubscriptionState.FREE,
) : SubscriptionDataSource {

	private val subscriptionState = MutableStateFlow(initialState)
	var initializeCalls = 0
	var lastApiKey: String? = null
	var lastSyncedUserId: String? = null

	override fun observeSubscriptionState(): Flow<SubscriptionState> = subscriptionState.asStateFlow()

	override fun initialize(apiKey: String?) {
		initializeCalls += 1
		lastApiKey = apiKey
	}

	override suspend fun syncUserId(userId: String?) {
		lastSyncedUserId = userId
	}

	override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> = Ok(Unit)

	override suspend fun restorePurchases(): Outcome<Unit> = Ok(Unit)

	fun updateSubscriptionState(state: SubscriptionState) {
		subscriptionState.value = state
	}
}
