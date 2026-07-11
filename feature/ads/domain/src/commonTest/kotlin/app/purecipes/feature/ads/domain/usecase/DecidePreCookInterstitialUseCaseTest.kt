package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DecidePreCookInterstitialUseCaseTest {

	@Test
	fun `invoke returns false when user is premium`() = runTest {
		val useCase = DecidePreCookInterstitialUseCase(
			observePremiumStatus = ObservePremiumStatusUseCase(
				FakeSubscriptionRepository(premiumState()),
			),
			preCookInterstitialChance = PreCookInterstitialChance { true },
		)

		useCase() shouldBe false
	}

	@Test
	fun `invoke returns chance result when user is free`() = runTest {
		val useCase = DecidePreCookInterstitialUseCase(
			observePremiumStatus = ObservePremiumStatusUseCase(
				FakeSubscriptionRepository(SubscriptionState.FREE),
			),
			preCookInterstitialChance = PreCookInterstitialChance { true },
		)

		useCase() shouldBe true
	}

	@Test
	fun `default probability constant is fifty percent`() {
		AdMobDefaults.PRE_COOK_INTERSTITIAL_PROBABILITY shouldBe 0.5
	}

	private fun premiumState(): SubscriptionState = SubscriptionState(
		status = SubscriptionStatus.PREMIUM,
		isActive = true,
		expirationInstant = null,
		trialActive = false,
	)
}
