package app.purecipes.feature.subscription.ui

import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.GetSubscriptionPlansUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.PurchaseSubscriptionUseCase
import app.purecipes.feature.subscription.domain.usecase.RestorePurchasesUseCase
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaywallViewModelTest {

	@Test
	fun `loadPlans populates available plans`() = runViewModelTest {
		val plans = listOf(
			SubscriptionPlan(
				id = "premium_monthly_v1",
				name = "Premium Monthly",
				price = "$4.99",
				duration = "Monthly",
				packageIdentifier = SubscriptionPackageIdentifier.MONTHLY,
			),
		)
		val repository = FakeSubscriptionRepository(subscriptionPlans = plans)
		val viewModel = PaywallViewModel(
			getSubscriptionPlans = GetSubscriptionPlansUseCase(repository),
			observePremiumStatus = ObservePremiumStatusUseCase(repository),
			purchaseSubscription = PurchaseSubscriptionUseCase(repository),
			restorePurchases = RestorePurchasesUseCase(repository),
		)

		advanceUntilIdle()

		viewModel.isLoadingPlans shouldBe false
		viewModel.plans shouldBe plans
		viewModel.errorMessage shouldBe null
	}

	@Test
	fun `observePremiumStatus updates isPremium`() = runViewModelTest {
		val repository = FakeSubscriptionRepository(
			initialState = SubscriptionState(
				status = SubscriptionStatus.PREMIUM,
				isActive = true,
				expirationInstant = null,
				trialActive = false,
			),
		)
		val viewModel = PaywallViewModel(
			getSubscriptionPlans = GetSubscriptionPlansUseCase(repository),
			observePremiumStatus = ObservePremiumStatusUseCase(repository),
			purchaseSubscription = PurchaseSubscriptionUseCase(repository),
			restorePurchases = RestorePurchasesUseCase(repository),
		)

		advanceUntilIdle()

		viewModel.isPremium shouldBe true
	}

	@Test
	fun `onPurchase delegates to repository`() = runViewModelTest {
		val repository = FakeSubscriptionRepository()
		val viewModel = PaywallViewModel(
			getSubscriptionPlans = GetSubscriptionPlansUseCase(repository),
			observePremiumStatus = ObservePremiumStatusUseCase(repository),
			purchaseSubscription = PurchaseSubscriptionUseCase(repository),
			restorePurchases = RestorePurchasesUseCase(repository),
		)

		advanceUntilIdle()
		viewModel.onPurchase(SubscriptionPackageIdentifier.MONTHLY)
		advanceUntilIdle()

		repository.purchaseCalls shouldBe 1
	}
}
