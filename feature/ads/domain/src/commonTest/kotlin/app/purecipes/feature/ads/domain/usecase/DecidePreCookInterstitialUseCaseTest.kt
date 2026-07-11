package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DecidePreCookInterstitialUseCaseTest {

	@Test
	fun `invoke returns false when ads are hidden for premium`() = runTest {
		val useCase = decideUseCase(
			subscriptionState = premiumState(),
			chanceResult = true,
		)

		useCase() shouldBe false
	}

	@Test
	fun `invoke returns chance result when ads are shown for free`() = runTest {
		val useCase = decideUseCase(
			subscriptionState = SubscriptionState.FREE,
			chanceResult = true,
		)

		useCase() shouldBe true
	}

	@Test
	fun `invoke returns false when ads override forces ads off`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(adsDisplay = AdsDisplayOverride.FORCE_OFF),
		)
		val useCase = decideUseCase(
			subscriptionState = SubscriptionState.FREE,
			overrides = overrides,
			chanceResult = true,
		)

		useCase() shouldBe false
	}

	@Test
	fun `default probability constant is fifty percent`() {
		AdMobDefaults.PRE_COOK_INTERSTITIAL_PROBABILITY shouldBe 0.5
	}

	private fun decideUseCase(
		subscriptionState: SubscriptionState,
		overrides: FakeMonetisationDebugOverridesRepository = FakeMonetisationDebugOverridesRepository(),
		chanceResult: Boolean,
	): DecidePreCookInterstitialUseCase = DecidePreCookInterstitialUseCase(
		observeShouldShowAds = ObserveShouldShowAdsUseCase(
			observePremiumStatus = ObservePremiumStatusUseCase(
				repository = FakeSubscriptionRepository(subscriptionState),
				monetisationDebugOverrides = overrides,
			),
			monetisationDebugOverrides = overrides,
		),
		preCookInterstitialChance = PreCookInterstitialChance { chanceResult },
	)

	private fun premiumState(): SubscriptionState = SubscriptionState(
		status = SubscriptionStatus.PREMIUM,
		isActive = true,
		expirationInstant = null,
		trialActive = false,
	)
}
