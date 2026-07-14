package app.purecipes.feature.subscription.data.mapper

import app.purecipes.feature.subscription.domain.SubscriptionEntitlements
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PeriodType
import kotlin.time.Instant

internal fun CustomerInfo.toSubscriptionState(): SubscriptionState {
	val entitlement = entitlements[SubscriptionEntitlements.PREMIUM]
		?: entitlements.active[SubscriptionEntitlements.PREMIUM]

	return when {
		entitlement?.isActive == true -> SubscriptionState(
			status = SubscriptionStatus.PREMIUM,
			isActive = true,
			expirationInstant = entitlement.expirationDate?.toKotlinxInstant(),
			trialActive = entitlement.periodType == PeriodType.TRIAL,
		)

		entitlement != null -> SubscriptionState(
			status = SubscriptionStatus.EXPIRED,
			isActive = false,
			expirationInstant = entitlement.expirationDate?.toKotlinxInstant(),
			trialActive = false,
		)

		else -> SubscriptionState.FREE
	}
}

private fun Instant.toKotlinxInstant(): Instant =
	Instant.fromEpochMilliseconds(toEpochMilliseconds())
