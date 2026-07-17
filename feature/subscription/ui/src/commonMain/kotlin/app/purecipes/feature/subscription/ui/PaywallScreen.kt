package app.purecipes.feature.subscription.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.subscription.domain.model.SubscriptionPackageIdentifier
import app.purecipes.feature.subscription.domain.model.SubscriptionPlan
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal const val PAYWALL_SCREEN_TAG = "paywallScreen"
internal const val PAYWALL_PREMIUM_MESSAGE_TAG = "paywallPremiumMessage"
internal const val PAYWALL_PLAN_ROW_TAG_PREFIX = "paywallPlanRow"
internal const val PAYWALL_RESTORE_BUTTON_TAG = "paywallRestoreButton"

@Composable
fun PaywallScreen(
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: PaywallViewModel = metroViewModel(),
) {
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(viewModel.errorMessage) {
		val message = viewModel.errorMessage ?: return@LaunchedEffect
		snackbarHostState.showSnackbar(message)
	}

	LaunchedEffect(viewModel.successMessage) {
		val message = viewModel.successMessage ?: return@LaunchedEffect
		snackbarHostState.showSnackbar(message)
	}

	Scaffold(
		modifier = modifier
			.fillMaxSize()
			.testTag(PAYWALL_SCREEN_TAG),
		topBar = {
			TopAppBar(
				title = { Text(text = "Go Premium") },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
	) { innerPadding ->
		PaywallScreenContent(
			isPremium = viewModel.isPremium,
			isLoadingPlans = viewModel.isLoadingPlans,
			plans = viewModel.plans.toImmutableList(),
			isPurchasing = viewModel.isPurchasing,
			isRestoring = viewModel.isRestoring,
			onPurchase = viewModel::onPurchase,
			onRestorePurchases = viewModel::onRestorePurchases,
			onRetryLoadPlans = viewModel::onRetryLoadPlans,
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
		)
	}
}

@Composable
internal fun PaywallScreenContent(
	isPremium: Boolean,
	isLoadingPlans: Boolean,
	plans: ImmutableList<SubscriptionPlan>,
	isPurchasing: Boolean,
	isRestoring: Boolean,
	onPurchase: (SubscriptionPackageIdentifier) -> Unit,
	onRestorePurchases: () -> Unit,
	onRetryLoadPlans: () -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		isPremium -> PremiumSubscribedContent(modifier = modifier)
		isLoadingPlans -> LoadingContent(modifier = modifier)
		plans.isEmpty() -> EmptyPlansContent(
			onRetryLoadPlans = onRetryLoadPlans,
			modifier = modifier,
		)

		else -> PlansContent(
			plans = plans,
			isPurchasing = isPurchasing,
			isRestoring = isRestoring,
			onPurchase = onPurchase,
			onRestorePurchases = onRestorePurchases,
			modifier = modifier,
		)
	}
}

@Composable
private fun PremiumSubscribedContent(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.testTag(PAYWALL_PREMIUM_MESSAGE_TAG),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = "You are subscribed to Premium.",
			style = PurecipesTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
		)
		Text(
			text = "Enjoy ad-free cooking and advanced filters.",
			style = PurecipesTheme.typography.bodyMedium,
			modifier = Modifier.padding(top = PurecipesTheme.space.s),
		)
	}
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun EmptyPlansContent(
	onRetryLoadPlans: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = "Subscription plans are unavailable.",
			style = PurecipesTheme.typography.bodyLarge,
		)
		Button(
			onClick = onRetryLoadPlans,
			modifier = Modifier.padding(top = PurecipesTheme.space.m),
		) {
			Text(text = "Try again")
		}
	}
}

@Composable
private fun PlansContent(
	plans: ImmutableList<SubscriptionPlan>,
	isPurchasing: Boolean,
	isRestoring: Boolean,
	onPurchase: (SubscriptionPackageIdentifier) -> Unit,
	onRestorePurchases: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Text(
			text = "Choose a plan",
			style = PurecipesTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
		)
		Text(
			text = "Remove ads and unlock key ingredients, nutrition, and calorie filters.",
			style = PurecipesTheme.typography.bodyMedium,
		)
		plans.forEach { plan ->
			SubscriptionPlanCard(
				plan = plan,
				enabled = !isPurchasing && !isRestoring,
				onPurchase = onPurchase,
			)
		}
		OutlinedButton(
			onClick = onRestorePurchases,
			enabled = !isPurchasing && !isRestoring,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(PAYWALL_RESTORE_BUTTON_TAG),
		) {
			Text(text = if (isRestoring) "Restoring..." else "Restore purchases")
		}
	}
}

@Composable
private fun SubscriptionPlanCard(
	plan: SubscriptionPlan,
	enabled: Boolean,
	onPurchase: (SubscriptionPackageIdentifier) -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m)
				.testTag("$PAYWALL_PLAN_ROW_TAG_PREFIX:${plan.id}"),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = plan.name,
				style = PurecipesTheme.typography.titleSmall,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = "${plan.price} / ${plan.duration.lowercase()}",
				style = PurecipesTheme.typography.bodyLarge,
			)
			Button(
				onClick = { onPurchase(plan.packageIdentifier) },
				enabled = enabled,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(text = "Subscribe")
			}
		}
	}
}

@Preview(
	name = "Paywall plans",
	device = Devices.PIXEL_7,
)
@Composable
private fun PaywallScreenPlansPreview() {
	PurecipesTheme {
		PaywallScreenContent(
			isPremium = false,
			isLoadingPlans = false,
			plans = listOf(
				SubscriptionPlan(
					id = "premium_monthly_v1",
					name = "Premium Monthly",
					price = "$4.99",
					duration = "Monthly",
					packageIdentifier = SubscriptionPackageIdentifier.MONTHLY,
				),
				SubscriptionPlan(
					id = "premium_annual_v1",
					name = "Premium Annual",
					price = "$39.99",
					duration = "Annual",
					packageIdentifier = SubscriptionPackageIdentifier.ANNUAL,
				),
			).toImmutableList(),
			isPurchasing = false,
			isRestoring = false,
			onPurchase = {},
			onRestorePurchases = {},
			onRetryLoadPlans = {},
		)
	}
}

@Preview(
	name = "Paywall subscribed",
	device = Devices.PIXEL_7,
)
@Composable
private fun PaywallScreenSubscribedPreview() {
	PurecipesTheme {
		PaywallScreenContent(
			isPremium = true,
			isLoadingPlans = false,
			plans = persistentListOf(),
			isPurchasing = false,
			isRestoring = false,
			onPurchase = {},
			onRestorePurchases = {},
			onRetryLoadPlans = {},
		)
	}
}
