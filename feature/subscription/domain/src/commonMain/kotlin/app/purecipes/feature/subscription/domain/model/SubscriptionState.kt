package app.purecipes.feature.subscription.domain.model

import kotlin.time.Instant

data class SubscriptionState(
	val status: SubscriptionStatus,
	val isActive: Boolean,
	val expirationInstant: Instant?,
	val trialActive: Boolean,
) {

	val isPremium: Boolean
		get() = status == SubscriptionStatus.PREMIUM && isActive

	companion object {

		val UNKNOWN = SubscriptionState(
			status = SubscriptionStatus.UNKNOWN,
			isActive = false,
			expirationInstant = null,
			trialActive = false,
		)

		val FREE = SubscriptionState(
			status = SubscriptionStatus.FREE,
			isActive = false,
			expirationInstant = null,
			trialActive = false,
		)
	}
}
