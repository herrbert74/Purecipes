package app.purecipes.feature.subscription.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.usecase.GetSubscriptionPlansUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.PurchaseSubscriptionUseCase
import app.purecipes.feature.subscription.domain.usecase.RestorePurchasesUseCase
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class PaywallViewModel(
	private val getSubscriptionPlans: GetSubscriptionPlansUseCase,
	private val observePremiumStatus: ObservePremiumStatusUseCase,
	private val purchaseSubscription: PurchaseSubscriptionUseCase,
	private val restorePurchases: RestorePurchasesUseCase,
) : ViewModel() {

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
		viewModelScope.launch {
			isPurchasing = true
			errorMessage = null
			successMessage = null
			val outcome = purchaseSubscription(packageIdentifier)
			val failure = outcome.getError()
			if (failure != null) {
				errorMessage = failure.message
			} else {
				successMessage = "Purchase completed."
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
			} else {
				successMessage = "Purchases restored."
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
}
