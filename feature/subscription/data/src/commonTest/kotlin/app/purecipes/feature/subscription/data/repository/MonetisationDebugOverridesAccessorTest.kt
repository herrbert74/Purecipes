package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.data.datasource.SettingsMonetisationDebugOverridesDataSource
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class MonetisationDebugOverridesAccessorTest {

	@Test
	fun `defaults to auto overrides`() = runTest {
		val accessor = accessor()

		accessor.observe().first() shouldBe MonetisationDebugOverrides()
	}

	@Test
	fun `setPremiumStatusOverride updates observed value`() = runTest {
		val accessor = accessor()

		accessor.setPremiumStatusOverride(PremiumStatusOverride.FORCE_PREMIUM)

		accessor.observe().first().premiumStatus shouldBe PremiumStatusOverride.FORCE_PREMIUM
	}

	@Test
	fun `setAdsDisplayOverride updates observed value`() = runTest {
		val accessor = accessor()

		accessor.setAdsDisplayOverride(AdsDisplayOverride.FORCE_OFF)

		accessor.observe().first().adsDisplay shouldBe AdsDisplayOverride.FORCE_OFF
	}

	@Test
	fun `release build ignores persisted overrides`() = runTest {
		val preferencesKey = "monetisation.debug.overrides.accessor.test.${Random.nextInt()}"
		val dataSource = SettingsMonetisationDebugOverridesDataSource(preferencesKey = preferencesKey)
		dataSource.setPremiumStatusOverride(PremiumStatusOverride.FORCE_FREE)
		val accessor = MonetisationDebugOverridesAccessor(
			dataSource,
			testPurecipesConfig(showDebugOverrides = false),
		)

		accessor.observe().first() shouldBe MonetisationDebugOverrides()
	}

	private fun accessor(showDebugOverrides: Boolean = true): MonetisationDebugOverridesAccessor {
		val preferencesKey = "monetisation.debug.overrides.accessor.test.${Random.nextInt()}"
		return MonetisationDebugOverridesAccessor(
			SettingsMonetisationDebugOverridesDataSource(preferencesKey = preferencesKey),
			testPurecipesConfig(showDebugOverrides),
		)
	}

	private fun testPurecipesConfig(showDebugOverrides: Boolean): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = "0.0.0-test"

		override fun versionCode(): Long = 0L

		override fun showMonetisationDebugOverrides(): Boolean = showDebugOverrides
	}
}
