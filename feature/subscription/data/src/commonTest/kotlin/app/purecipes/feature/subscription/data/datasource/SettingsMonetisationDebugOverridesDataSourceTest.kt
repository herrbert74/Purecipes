package app.purecipes.feature.subscription.data.datasource

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class SettingsMonetisationDebugOverridesDataSourceTest {

	@Test
	fun `overrides stay in sync across datasource instances`() = runTest {
		val preferencesKey = "monetisation.debug.overrides.test.${Random.nextInt()}"
		val firstDataSource = SettingsMonetisationDebugOverridesDataSource(preferencesKey = preferencesKey)
		val secondDataSource = SettingsMonetisationDebugOverridesDataSource(preferencesKey = preferencesKey)

		firstDataSource.setPremiumStatusOverride(PremiumStatusOverride.FORCE_PREMIUM)
		firstDataSource.setAdsDisplayOverride(AdsDisplayOverride.FORCE_OFF)

		secondDataSource.observe().first().premiumStatus shouldBe PremiumStatusOverride.FORCE_PREMIUM
		secondDataSource.observe().first().adsDisplay shouldBe AdsDisplayOverride.FORCE_OFF
	}

	@Test
	fun `defaults to auto overrides`() = runTest {
		val preferencesKey = "monetisation.debug.overrides.test.${Random.nextInt()}"
		val dataSource = SettingsMonetisationDebugOverridesDataSource(preferencesKey = preferencesKey)

		dataSource.observe().first().premiumStatus shouldBe PremiumStatusOverride.AUTO
		dataSource.observe().first().adsDisplay shouldBe AdsDisplayOverride.AUTO
	}
}
