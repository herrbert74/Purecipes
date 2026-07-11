package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObservePremiumStatusUseCaseTest {

	@Test
	fun `invoke emits true for active premium state`() = runTest {
		val repository = FakeSubscriptionRepository(
			initialState = SubscriptionState(
				status = SubscriptionStatus.PREMIUM,
				isActive = true,
				expirationInstant = null,
				trialActive = false,
			),
		)
		val useCase = ObservePremiumStatusUseCase(
			repository,
			FakeMonetisationDebugOverridesRepository(),
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `invoke emits false for free state`() = runTest {
		val repository = FakeSubscriptionRepository()
		val useCase = ObservePremiumStatusUseCase(
			repository,
			FakeMonetisationDebugOverridesRepository(),
		)

		useCase().first() shouldBe false
	}

	@Test
	fun `force premium override emits true for free subscribers`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(premiumStatus = PremiumStatusOverride.FORCE_PREMIUM),
		)
		val useCase = ObservePremiumStatusUseCase(
			FakeSubscriptionRepository(),
			overrides,
		)

		useCase().first() shouldBe true
	}

	@Test
	fun `force free override emits false for premium subscribers`() = runTest {
		val overrides = FakeMonetisationDebugOverridesRepository(
			MonetisationDebugOverrides(premiumStatus = PremiumStatusOverride.FORCE_FREE),
		)
		val useCase = ObservePremiumStatusUseCase(
			FakeSubscriptionRepository(
				initialState = SubscriptionState(
					status = SubscriptionStatus.PREMIUM,
					isActive = true,
					expirationInstant = null,
					trialActive = false,
				),
			),
			overrides,
		)

		useCase().first() shouldBe false
	}
}
