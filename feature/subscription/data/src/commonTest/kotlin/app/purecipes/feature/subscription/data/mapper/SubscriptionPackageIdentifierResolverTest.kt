package app.purecipes.feature.subscription.data.mapper

import app.purecipes.feature.subscription.domain.SubscriptionProducts
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SubscriptionPackageIdentifierResolverTest {

	@Test
	fun `resolve maps production product ids`() {
		SubscriptionPackageIdentifierResolver.resolve(
			productId = SubscriptionProducts.PREMIUM_MONTHLY,
			packageTypeName = null,
			packageIdentifier = null,
		) shouldBe SubscriptionPackageIdentifier.MONTHLY

		SubscriptionPackageIdentifierResolver.resolve(
			productId = SubscriptionProducts.PREMIUM_ANNUAL,
			packageTypeName = null,
			packageIdentifier = null,
		) shouldBe SubscriptionPackageIdentifier.ANNUAL
	}

	@Test
	fun `resolve maps revenuecat package types`() {
		SubscriptionPackageIdentifierResolver.resolve(
			productId = "monthly",
			packageTypeName = "MONTHLY",
			packageIdentifier = null,
		) shouldBe SubscriptionPackageIdentifier.MONTHLY

		SubscriptionPackageIdentifierResolver.resolve(
			productId = "yearly",
			packageTypeName = "ANNUAL",
			packageIdentifier = null,
		) shouldBe SubscriptionPackageIdentifier.ANNUAL
	}

	@Test
	fun `resolve maps revenuecat package identifiers`() {
		SubscriptionPackageIdentifierResolver.resolve(
			productId = "monthly",
			packageTypeName = "CUSTOM",
			packageIdentifier = "\$rc_monthly",
		) shouldBe SubscriptionPackageIdentifier.MONTHLY

		SubscriptionPackageIdentifierResolver.resolve(
			productId = "yearly",
			packageTypeName = "CUSTOM",
			packageIdentifier = "\$rc_annual",
		) shouldBe SubscriptionPackageIdentifier.ANNUAL
	}

	@Test
	fun `resolve ignores unsupported packages`() {
		SubscriptionPackageIdentifierResolver.resolve(
			productId = "lifetime",
			packageTypeName = "LIFETIME",
			packageIdentifier = "\$rc_lifetime",
		) shouldBe null
	}
}
