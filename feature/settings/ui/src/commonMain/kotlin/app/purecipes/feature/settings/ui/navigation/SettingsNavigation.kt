package app.purecipes.feature.settings.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import app.purecipes.feature.settings.ui.SettingsScreen
import app.purecipes.feature.settings.ui.about.AboutScreen
import app.purecipes.feature.settings.ui.about.LicensesScreen
import app.purecipes.feature.subscription.ui.navigation.PaywallDestination
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installSettingsFlow(
	navigator: Navigator,
) {
	entry<AccountSettingsDestination> {
		SettingsScreen(
			onBack = { navigator.back() },
			onOpenPaywall = {
				navigator.push(
					PaywallDestination(
						feature = AnalyticsPremiumFeature.SETTINGS_PAYWALL,
						origin = AnalyticsOrigin.SETTINGS.value,
					),
				)
			},
			onOpenAbout = { navigator.push(AboutDestination) },
			modifier = Modifier.fillMaxSize(),
		)
	}
	entry<AboutDestination> {
		AboutScreen(
			onBack = { navigator.back() },
			onOpenLicenses = { navigator.push(LicensesDestination) },
			modifier = Modifier.fillMaxSize(),
		)
	}
	entry<LicensesDestination> {
		LicensesScreen(
			onBack = { navigator.back() },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun settingsNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(AccountSettingsDestination.serializer())
		subclass(AboutDestination.serializer())
		subclass(LicensesDestination.serializer())
	}
}
