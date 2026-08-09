package app.purecipes.feature.subscription.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import app.purecipes.feature.analytics.domain.model.AnalyticsRestoreResult
import app.purecipes.feature.analytics.domain.model.AnalyticsSubscriptionPlan
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import app.purecipes.feature.subscription.domain.usecase.GetSubscriptionPlansUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.PurchaseSubscriptionUseCase
import app.purecipes.feature.subscription.domain.usecase.RestorePurchasesUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaywallViewModelAnalyticsTest {

	@Test
	fun `init tracks paywall viewed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		paywallViewModel(analyticsRepository = analyticsRepository)
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.PaywallViewed>().single() shouldBe
			AnalyticsEvent.PaywallViewed(
				feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
				origin = AnalyticsOrigin.SEARCH,
			)
	}

	@Test
	fun `onPurchase tracks started and completed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = paywallViewModel(analyticsRepository = analyticsRepository)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.onPurchase(SubscriptionPackageIdentifier.MONTHLY)
		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.PremiumUpgradeStarted(
				feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
				origin = AnalyticsOrigin.SEARCH,
				plan = AnalyticsSubscriptionPlan.MONTHLY,
			),
			AnalyticsEvent.PremiumUpgradeCompleted(
				feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
				plan = AnalyticsSubscriptionPlan.MONTHLY,
			),
		)
	}

	@Test
	fun `onPurchase tracks cancelled failure`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = object : SubscriptionRepository by FakeSubscriptionRepository() {
			override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> =
				Err(Failure.ServerError("Purchase cancelled"))
		}
		val viewModel = paywallViewModel(
			subscriptionRepository = repository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.onPurchase(SubscriptionPackageIdentifier.ANNUAL)
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.PremiumUpgradeFailed>().single() shouldBe
			AnalyticsEvent.PremiumUpgradeFailed(
				errorKind = AnalyticsErrorKind.USER_CANCELLED,
				feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
			)
	}

	@Test
	fun `onRestorePurchases tracks nothing to restore for free users`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = paywallViewModel(analyticsRepository = analyticsRepository)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.onRestorePurchases()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.RestorePurchasesCompleted(
			result = AnalyticsRestoreResult.NOTHING_TO_RESTORE,
		)
	}

	@Test
	fun `onRestorePurchases tracks restored for premium users`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = FakeSubscriptionRepository(
			initialState = SubscriptionState(
				status = SubscriptionStatus.PREMIUM,
				isActive = true,
				expirationInstant = null,
				trialActive = false,
			),
		)
		val viewModel = paywallViewModel(
			subscriptionRepository = repository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.onRestorePurchases()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.RestorePurchasesCompleted(
			result = AnalyticsRestoreResult.RESTORED,
		)
	}

	@Test
	fun `onRestorePurchases tracks failed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = object : SubscriptionRepository by FakeSubscriptionRepository() {
			override suspend fun restorePurchases(): Outcome<Unit> =
				Err(Failure.ServerError("Restore failed"))
		}
		val viewModel = paywallViewModel(
			subscriptionRepository = repository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.onRestorePurchases()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.RestorePurchasesCompleted(
			result = AnalyticsRestoreResult.FAILED,
		)
	}

	private fun paywallViewModel(
		subscriptionRepository: SubscriptionRepository = FakeSubscriptionRepository(
			subscriptionPlans = listOf(
				SubscriptionPlan(
					id = "premium_monthly_v1",
					name = "Premium Monthly",
					price = "$4.99",
					duration = "Monthly",
					packageIdentifier = SubscriptionPackageIdentifier.MONTHLY,
				),
			),
		),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		feature: String = AnalyticsPremiumFeature.KEY_INGREDIENTS,
		origin: String = AnalyticsOrigin.SEARCH.value,
	) = PaywallViewModel(
		getSubscriptionPlans = GetSubscriptionPlansUseCase(subscriptionRepository),
		observePremiumStatus = ObservePremiumStatusUseCase(
			subscriptionRepository,
			FakeMonetisationDebugOverridesRepository(),
		),
		purchaseSubscription = PurchaseSubscriptionUseCase(subscriptionRepository),
		restorePurchases = RestorePurchasesUseCase(subscriptionRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
		feature = feature,
		origin = origin,
	)
}
