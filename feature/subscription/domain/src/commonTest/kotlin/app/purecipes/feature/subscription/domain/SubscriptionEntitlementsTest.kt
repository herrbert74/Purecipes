package app.purecipes.feature.subscription.domain

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SubscriptionEntitlementsTest {

	@Test
	fun `premium entitlement matches RevenueCat configuration`() {
		SubscriptionEntitlements.PREMIUM shouldBe "Purecipes Pro"
	}
}
