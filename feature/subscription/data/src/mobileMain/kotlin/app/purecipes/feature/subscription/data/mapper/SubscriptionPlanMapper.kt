package app.purecipes.feature.subscription.data.mapper

import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import com.revenuecat.purchases.kmp.models.Package

internal fun Package.toSubscriptionPlan(): SubscriptionPlan? {
	val packageIdentifier = SubscriptionPackageIdentifierResolver.resolve(
		productId = storeProduct.id,
		packageTypeName = packageType.name,
		packageIdentifier = identifier,
	) ?: return null
	val duration = when (packageIdentifier) {
		SubscriptionPackageIdentifier.MONTHLY -> "Monthly"
		SubscriptionPackageIdentifier.ANNUAL -> "Annual"
	}
	return SubscriptionPlan(
		id = storeProduct.id,
		name = storeProduct.title,
		price = storeProduct.price.formatted,
		duration = duration,
		packageIdentifier = packageIdentifier,
	)
}

internal fun Package.subscriptionPackageIdentifier(): SubscriptionPackageIdentifier? =
	SubscriptionPackageIdentifierResolver.resolve(
		productId = storeProduct.id,
		packageTypeName = packageType.name,
		packageIdentifier = identifier,
	)
