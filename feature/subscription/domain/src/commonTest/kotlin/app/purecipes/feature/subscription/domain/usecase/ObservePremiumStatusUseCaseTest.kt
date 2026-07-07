package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
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
		val useCase = ObservePremiumStatusUseCase(repository)

		useCase().first() shouldBe true
	}

	@Test
	fun `invoke emits false for free state`() = runTest {
		val repository = FakeSubscriptionRepository()
		val useCase = ObservePremiumStatusUseCase(repository)

		useCase().first() shouldBe false
	}
}
