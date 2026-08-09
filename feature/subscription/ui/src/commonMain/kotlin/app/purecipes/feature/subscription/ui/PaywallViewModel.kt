package app.purecipes.feature.subscription.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsRestoreResult
import app.purecipes.feature.analytics.domain.model.AnalyticsSubscriptionPlan
import app.purecipes.feature.analytics.domain.model.toAnalyticsErrorKind
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.usecase.GetSubscriptionPlansUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.PurchaseSubscriptionUseCase
import app.purecipes.feature.subscription.domain.usecase.RestorePurchasesUseCase
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val PURCHASE_CANCELLED_MESSAGE = "Purchase cancelled"

@AssistedInject
class PaywallViewModel(
	private val getSubscriptionPlans: GetSubscriptionPlansUseCase,
	private val observePremiumStatus: ObservePremiumStatusUseCase,
	private val purchaseSubscription: PurchaseSubscriptionUseCase,
	private val restorePurchases: RestorePurchasesUseCase,
	private val trackEvent: TrackEventUseCase,
	@Assisted private val feature: String,
	@Assisted private val origin: String,
) : ViewModel() {

	private val analyticsOrigin: AnalyticsOrigin =
		AnalyticsOrigin.fromValue(origin) ?: AnalyticsOrigin.SETTINGS

	var isPremium by mutableStateOf(false)
		private set

	var isLoadingPlans by mutableStateOf(true)
		private set

	var plans by mutableStateOf<List<SubscriptionPlan>>(emptyList())
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var successMessage by mutableStateOf<String?>(null)
		private set

	var isPurchasing by mutableStateOf(false)
		private set

	var isRestoring by mutableStateOf(false)
		private set

	init {
		trackEvent(
			AnalyticsEvent.PaywallViewed(
				feature = feature,
				origin = analyticsOrigin,
			),
		)
		viewModelScope.launch {
			observePremiumStatus().collectLatest { premium ->
				isPremium = premium
			}
		}
		viewModelScope.launch {
			loadPlans()
		}
	}

	fun onPurchase(packageIdentifier: SubscriptionPackageIdentifier) {
		if (isPurchasing || isRestoring) {
			return
		}
		val plan = packageIdentifier.toAnalyticsPlan()
		viewModelScope.launch {
			isPurchasing = true
			errorMessage = null
			successMessage = null
			trackEvent(
				AnalyticsEvent.PremiumUpgradeStarted(
					feature = feature,
					origin = analyticsOrigin,
					plan = plan,
				),
			)
			val outcome = purchaseSubscription(packageIdentifier)
			val failure = outcome.getError()
			if (failure != null) {
				errorMessage = failure.message
				trackEvent(
					AnalyticsEvent.PremiumUpgradeFailed(
						errorKind = failure.toPurchaseAnalyticsErrorKind(),
						feature = feature,
					),
				)
			} else {
				successMessage = "Purchase completed."
				trackEvent(
					AnalyticsEvent.PremiumUpgradeCompleted(
						feature = feature,
						plan = plan,
					),
				)
			}
			isPurchasing = false
		}
	}

	fun onRestorePurchases() {
		if (isPurchasing || isRestoring) {
			return
		}
		viewModelScope.launch {
			isRestoring = true
			errorMessage = null
			successMessage = null
			val outcome = restorePurchases()
			val failure = outcome.getError()
			if (failure != null) {
				errorMessage = failure.message
				trackEvent(
					AnalyticsEvent.RestorePurchasesCompleted(
						result = AnalyticsRestoreResult.FAILED,
					),
				)
			} else {
				successMessage = "Purchases restored."
				val premiumNow = observePremiumStatus().first()
				trackEvent(
					AnalyticsEvent.RestorePurchasesCompleted(
						result = if (premiumNow) {
							AnalyticsRestoreResult.RESTORED
						} else {
							AnalyticsRestoreResult.NOTHING_TO_RESTORE
						},
					),
				)
			}
			isRestoring = false
		}
	}

	fun onRetryLoadPlans() {
		viewModelScope.launch {
			loadPlans()
		}
	}

	private suspend fun loadPlans() {
		isLoadingPlans = true
		errorMessage = null
		val outcome = getSubscriptionPlans()
		val failure = outcome.getError()
		if (failure != null) {
			plans = emptyList()
			errorMessage = failure.message
		} else {
			plans = outcome.get().orEmpty()
			if (plans.isEmpty()) {
				errorMessage = "No subscription plans are available right now."
			}
		}
		isLoadingPlans = false
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(feature: String, origin: String): PaywallViewModel
	}
}

private fun SubscriptionPackageIdentifier.toAnalyticsPlan(): String = when (this) {
	SubscriptionPackageIdentifier.MONTHLY -> AnalyticsSubscriptionPlan.MONTHLY
	SubscriptionPackageIdentifier.ANNUAL -> AnalyticsSubscriptionPlan.ANNUAL
}

private fun Failure.toPurchaseAnalyticsErrorKind(): String =
	if (this is Failure.ServerError && message == PURCHASE_CANCELLED_MESSAGE) {
		AnalyticsErrorKind.USER_CANCELLED
	} else {
		toAnalyticsErrorKind()
	}
