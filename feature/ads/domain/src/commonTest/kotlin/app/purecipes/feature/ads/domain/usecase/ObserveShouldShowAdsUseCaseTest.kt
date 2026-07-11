package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveShouldShowAdsUseCaseTest {

	@Test
	fun `invoke is true for free users`() = runTest {
		val useCase = ObserveShouldShowAdsUseCase(
			observePremiumStatus = ObservePremiumStatusUseCase(
				FakeSubscriptionRepository(SubscriptionState.FREE),
			),
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `invoke is false for premium users`() = runTest {
		val useCase = ObserveShouldShowAdsUseCase(
			observePremiumStatus = ObservePremiumStatusUseCase(
				FakeSubscriptionRepository(
					SubscriptionState(
						status = SubscriptionStatus.PREMIUM,
						isActive = true,
						expirationInstant = null,
						trialActive = false,
					),
				),
			),
		)

		useCase().first() shouldBe false
	}
}
