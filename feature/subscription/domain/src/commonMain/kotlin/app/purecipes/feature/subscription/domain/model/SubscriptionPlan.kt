package app.purecipes.feature.subscription.domain.model

data class SubscriptionPlan(
	val id: String,
	val name: String,
	val price: String,
	val duration: String,
	val packageIdentifier: SubscriptionPackageIdentifier,
)
