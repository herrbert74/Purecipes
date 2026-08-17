package app.purecipes.feature.ads.data.repository

import app.purecipes.feature.ads.data.datasource.FakeAdsDataSource
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AdsAccessorTest {

	@Test
	fun `initialize forwards config values to data source`() {
		val dataSource = FakeAdsDataSource()
		val accessor = AdsAccessor(
			adsDataSource = dataSource,
			purecipesConfig = testPurecipesConfig(
				adMobAppId = "app-id",
				adMobInterstitialAdUnitId = "interstitial-id",
			),
		)

		accessor.initialize()

		dataSource.initializeCalls shouldBe 1
		dataSource.lastAppId shouldBe "app-id"
		dataSource.lastInterstitialAdUnitId shouldBe "interstitial-id"
	}

	@Test
	fun `showInterstitial forwards dismissal and optional callbacks`() {
		val dataSource = FakeAdsDataSource()
		val accessor = AdsAccessor(
			adsDataSource = dataSource,
			purecipesConfig = testPurecipesConfig(),
		)
		var dismissed = false
		var impressed = false
		var clicked = false

		accessor.showInterstitial(
			onDismissed = { dismissed = true },
			onImpression = { impressed = true },
			onClicked = { clicked = true },
		)

		dataSource.showInterstitialCalls shouldBe 1
		dismissed shouldBe true
		dataSource.lastOnImpression?.invoke()
		dataSource.lastOnClicked?.invoke()
		impressed shouldBe true
		clicked shouldBe true
	}

	private fun testPurecipesConfig(
		adMobAppId: String? = null,
		adMobInterstitialAdUnitId: String? = null,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = "0.0.0-test"

		override fun versionCode(): Long = 0L

		override fun adMobAppId(): String? = adMobAppId

		override fun adMobInterstitialAdUnitId(): String? = adMobInterstitialAdUnitId
	}
}
