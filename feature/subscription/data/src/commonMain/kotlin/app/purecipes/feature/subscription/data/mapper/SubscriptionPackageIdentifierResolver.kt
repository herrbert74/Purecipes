package app.purecipes.feature.subscription.data.mapper

import app.purecipes.feature.subscription.domain.SubscriptionProducts
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier

internal object SubscriptionPackageIdentifierResolver {

	private const val RC_MONTHLY = "\$rc_monthly"
	private const val RC_ANNUAL = "\$rc_annual"

	fun resolve(
		productId: String,
		packageTypeName: String?,
		packageIdentifier: String?,
	): SubscriptionPackageIdentifier? = when {
		productId == SubscriptionProducts.PREMIUM_MONTHLY -> SubscriptionPackageIdentifier.MONTHLY
		productId == SubscriptionProducts.PREMIUM_ANNUAL -> SubscriptionPackageIdentifier.ANNUAL
		packageTypeName == "MONTHLY" -> SubscriptionPackageIdentifier.MONTHLY
		packageTypeName == "ANNUAL" -> SubscriptionPackageIdentifier.ANNUAL
		packageIdentifier == RC_MONTHLY -> SubscriptionPackageIdentifier.MONTHLY
		packageIdentifier == RC_ANNUAL -> SubscriptionPackageIdentifier.ANNUAL
		else -> null
	}
}
