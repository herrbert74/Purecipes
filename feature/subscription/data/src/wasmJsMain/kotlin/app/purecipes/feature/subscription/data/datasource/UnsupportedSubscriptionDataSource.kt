package app.purecipes.feature.subscription.data.datasource

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@ContributesBinding(AppScope::class)
class UnsupportedSubscriptionDataSource : SubscriptionDataSource {

	private val subscriptionState = MutableStateFlow(SubscriptionState.FREE)

	override fun observeSubscriptionState(): Flow<SubscriptionState> = subscriptionState.asStateFlow()

	override fun initialize(apiKey: String?) = Unit

	override suspend fun getSubscriptionPlans(): Outcome<List<SubscriptionPlan>> =
		Err(Failure.ServerError("Subscriptions are not supported on this platform"))

	override suspend fun syncUserId(userId: String?) = Unit

	override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> =
		Err(Failure.ServerError("Subscriptions are not supported on this platform"))

	override suspend fun restorePurchases(): Outcome<Unit> =
		Err(Failure.ServerError("Subscriptions are not supported on this platform"))
}
