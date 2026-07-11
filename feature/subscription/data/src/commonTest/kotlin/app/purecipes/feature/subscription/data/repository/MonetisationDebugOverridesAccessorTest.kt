package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MonetisationDebugOverridesAccessorTest {

	@Test
	fun `defaults to auto overrides`() = runTest {
		val accessor = MonetisationDebugOverridesAccessor()

		accessor.observe().first() shouldBe MonetisationDebugOverrides()
	}

	@Test
	fun `setPremiumStatusOverride updates observed value`() = runTest {
		val accessor = MonetisationDebugOverridesAccessor()

		accessor.setPremiumStatusOverride(PremiumStatusOverride.FORCE_PREMIUM)

		accessor.observe().first().premiumStatus shouldBe PremiumStatusOverride.FORCE_PREMIUM
	}

	@Test
	fun `setAdsDisplayOverride updates observed value`() = runTest {
		val accessor = MonetisationDebugOverridesAccessor()

		accessor.setAdsDisplayOverride(AdsDisplayOverride.FORCE_OFF)

		accessor.observe().first().adsDisplay shouldBe AdsDisplayOverride.FORCE_OFF
	}
}
