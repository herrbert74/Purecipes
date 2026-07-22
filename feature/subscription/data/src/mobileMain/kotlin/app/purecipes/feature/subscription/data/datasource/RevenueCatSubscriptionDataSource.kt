package app.purecipes.feature.subscription.data.datasource

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.subscription.data.mapper.subscriptionPackageIdentifier
import app.purecipes.feature.subscription.data.mapper.toSubscriptionPlan
import app.purecipes.feature.subscription.data.mapper.toSubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.ktx.awaitSyncPurchases
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.PurchasesTransactionException
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RevenueCatSubscriptionDataSource : SubscriptionDataSource {

	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	private val subscriptionState = MutableStateFlow(SubscriptionState.UNKNOWN)
	private val purchasesDelegate = object : PurchasesDelegate {
		override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
			subscriptionState.value = customerInfo.toSubscriptionState()
		}

		override fun onPurchasePromoProduct(
			product: StoreProduct,
			startPurchase: (
				onError: (error: PurchasesError, userCancelled: Boolean) -> Unit,
				onSuccess: (storeTransaction: StoreTransaction, customerInfo: CustomerInfo) -> Unit,
			) -> Unit,
		) = Unit
	}

	override fun observeSubscriptionState(): Flow<SubscriptionState> = subscriptionState.asStateFlow()

	override fun initialize(apiKey: String?) {
		val configuredApiKey = apiKey?.takeIf { it.isNotBlank() } ?: run {
			subscriptionState.value = SubscriptionState.FREE
			return
		}
		if (Purchases.isConfigured) {
			Purchases.sharedInstance.delegate = purchasesDelegate
			refreshCustomerInfo()
			return
		}
		Purchases.configure(PurchasesConfiguration(configuredApiKey))
		Purchases.sharedInstance.delegate = purchasesDelegate
		refreshCustomerInfo()
	}

	override suspend fun syncUserId(userId: String?) {
		if (!Purchases.isConfigured) {
			return
		}
		try {
			if (userId.isNullOrBlank()) {
				if (Purchases.sharedInstance.isAnonymous) {
					return
				}
				val customerInfo = Purchases.sharedInstance.awaitLogOut()
				subscriptionState.value = customerInfo.toSubscriptionState()
			} else {
				val loginResult = Purchases.sharedInstance.awaitLogIn(userId)
				subscriptionState.value = loginResult.customerInfo.toSubscriptionState()
			}
		} catch (_: PurchasesException) {
		}
	}

	override suspend fun getSubscriptionPlans(): Outcome<List<SubscriptionPlan>> {
		if (!Purchases.isConfigured) {
			return Err(Failure.ServerError("Subscriptions are not configured"))
		}
		return try {
			val offerings = Purchases.sharedInstance.awaitOfferings()
			Ok(offerings.subscriptionPlans())
		} catch (error: PurchasesException) {
			Err(Failure.ServerError(error.error.message))
		}
	}

	override suspend fun purchase(packageIdentifier: SubscriptionPackageIdentifier): Outcome<Unit> {
		if (!Purchases.isConfigured) {
			return Err(Failure.ServerError("Subscriptions are not configured"))
		}
		return purchaseConfiguredPackage(packageIdentifier)
	}

	private suspend fun purchaseConfiguredPackage(
		packageIdentifier: SubscriptionPackageIdentifier,
	): Outcome<Unit> {
		return try {
			val offerings = Purchases.sharedInstance.awaitOfferings()
			val revenueCatPackage = offerings.packageFor(packageIdentifier)
				?: return Err(Failure.ServerError("Subscription package is unavailable"))
			val purchaseResult = Purchases.sharedInstance.awaitPurchase(revenueCatPackage)
			subscriptionState.value = purchaseResult.customerInfo.toSubscriptionState()
			Ok(Unit)
		} catch (cancellation: PurchasesTransactionException) {
			if (cancellation.userCancelled) {
				Err(Failure.ServerError("Purchase cancelled"))
			} else {
				Err(Failure.ServerError(cancellation.error.message))
			}
		} catch (error: PurchasesException) {
			Err(Failure.ServerError(error.error.message))
		}
	}

	override suspend fun restorePurchases(): Outcome<Unit> {
		if (!Purchases.isConfigured) {
			return Err(Failure.ServerError("Subscriptions are not configured"))
		}
		return try {
			Purchases.sharedInstance.awaitSyncPurchases()
			Purchases.sharedInstance.invalidateCustomerInfoCache()
			val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
			subscriptionState.value = customerInfo.toSubscriptionState()
			Ok(Unit)
		} catch (error: PurchasesException) {
			Err(Failure.ServerError(error.error.message))
		}
	}

	private fun refreshCustomerInfo() {
		scope.launch {
			if (!Purchases.isConfigured) {
				return@launch
			}
			runCatching {
				Purchases.sharedInstance.awaitCustomerInfo()
			}.onSuccess { customerInfo ->
				subscriptionState.value = customerInfo.toSubscriptionState()
			}.onFailure {
				subscriptionState.value = SubscriptionState.FREE
			}
		}
	}

	private fun Offerings.subscriptionPlans(): List<SubscriptionPlan> {
		val packages = current?.availablePackages.orEmpty().ifEmpty {
			all.values.flatMap { offering -> offering.availablePackages }
		}
		return packages.mapNotNull { revenueCatPackage -> revenueCatPackage.toSubscriptionPlan() }
	}

	private fun Offerings.packageFor(packageIdentifier: SubscriptionPackageIdentifier): Package? {
		val packages = current?.availablePackages.orEmpty().ifEmpty {
			all.values.flatMap { offering -> offering.availablePackages }
		}
		return packages.firstOrNull { revenueCatPackage ->
			revenueCatPackage.subscriptionPackageIdentifier() == packageIdentifier
		}
	}
}
