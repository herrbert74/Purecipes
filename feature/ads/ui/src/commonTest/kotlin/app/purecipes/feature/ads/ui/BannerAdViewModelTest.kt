package app.purecipes.feature.ads.ui

import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.usecase.ObserveShouldShowAdsUseCase
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BannerAdViewModelTest {

	@Test
	fun `shouldShowAds is true for free users`() = runUnconfinedViewModelTest {
		val overrides = FakeMonetisationDebugOverridesRepository()
		val viewModel = BannerAdViewModel(
			observeShouldShowAds = ObserveShouldShowAdsUseCase(
				observePremiumStatus = ObservePremiumStatusUseCase(
					repository = FakeSubscriptionRepository(SubscriptionState.FREE),
					monetisationDebugOverrides = overrides,
				),
				monetisationDebugOverrides = overrides,
			),
			purecipesConfig = testPurecipesConfig(),
		)

		viewModel.shouldShowAds shouldBe true
		viewModel.bannerAdUnitId shouldBe AdMobDefaults.BANNER_AD_UNIT_ID
	}

	@Test
	fun `shouldShowAds is false for premium users`() = runUnconfinedViewModelTest {
		val overrides = FakeMonetisationDebugOverridesRepository()
		val viewModel = BannerAdViewModel(
			observeShouldShowAds = ObserveShouldShowAdsUseCase(
				observePremiumStatus = ObservePremiumStatusUseCase(
					repository = FakeSubscriptionRepository(
						SubscriptionState(
							status = SubscriptionStatus.PREMIUM,
							isActive = true,
							expirationInstant = null,
							trialActive = false,
						),
					),
					monetisationDebugOverrides = overrides,
				),
				monetisationDebugOverrides = overrides,
			),
			purecipesConfig = testPurecipesConfig(bannerAdUnitId = "custom-banner"),
		)

		viewModel.shouldShowAds shouldBe false
		viewModel.bannerAdUnitId shouldBe "custom-banner"
	}

	private fun testPurecipesConfig(
		bannerAdUnitId: String? = null,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = "0.0.0-test"

		override fun versionCode(): Long = 0L

		override fun adMobBannerAdUnitId(): String? = bannerAdUnitId
	}
}
