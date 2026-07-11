package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveShouldShowAdsUseCaseTest {

	@Test
	fun `invoke is true for free users with auto overrides`() = runTest {
		val useCase = shouldShowAdsUseCase(
			subscriptionState = SubscriptionState.FREE,
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `invoke is false for premium users with auto overrides`() = runTest {
		val useCase = shouldShowAdsUseCase(
			subscriptionState = premiumState(),
		)

		useCase().first() shouldBe false
	}

	@Test
	fun `force free premium override shows ads for premium subscribers`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(premiumStatus = PremiumStatusOverride.FORCE_FREE),
		)
		val useCase = shouldShowAdsUseCase(
			subscriptionState = premiumState(),
			overrides = overrides,
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `force premium override hides ads for free users`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(premiumStatus = PremiumStatusOverride.FORCE_PREMIUM),
		)
		val useCase = shouldShowAdsUseCase(
			subscriptionState = SubscriptionState.FREE,
			overrides = overrides,
		)

		useCase().first() shouldBe false
	}

	@Test
	fun `force on ads override shows ads for premium users`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(adsDisplay = AdsDisplayOverride.FORCE_ON),
		)
		val useCase = shouldShowAdsUseCase(
			subscriptionState = premiumState(),
			overrides = overrides,
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `force off ads override hides ads for free users`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(adsDisplay = AdsDisplayOverride.FORCE_OFF),
		)
		val useCase = shouldShowAdsUseCase(
			subscriptionState = SubscriptionState.FREE,
			overrides = overrides,
		)

		useCase().first() shouldBe false
	}

	private fun shouldShowAdsUseCase(
		subscriptionState: SubscriptionState,
		overrides: FakeMonetisationDebugOverridesRepository = FakeMonetisationDebugOverridesRepository(),
	): ObserveShouldShowAdsUseCase = ObserveShouldShowAdsUseCase(
		observePremiumStatus = ObservePremiumStatusUseCase(
			repository = FakeSubscriptionRepository(subscriptionState),
			monetisationDebugOverrides = overrides,
		),
		monetisationDebugOverrides = overrides,
	)

	private fun premiumState(): SubscriptionState = SubscriptionState(
		status = SubscriptionStatus.PREMIUM,
		isActive = true,
		expirationInstant = null,
		trialActive = false,
	)
}
