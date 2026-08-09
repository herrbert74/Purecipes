package app.purecipes.feature.subscription.ui.navigation

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import kotlinx.serialization.Serializable

@Serializable
data class PaywallDestination(
	val feature: String = AnalyticsPremiumFeature.SETTINGS_PAYWALL,
	val origin: String = AnalyticsOrigin.SETTINGS.value,
) : NavKey
