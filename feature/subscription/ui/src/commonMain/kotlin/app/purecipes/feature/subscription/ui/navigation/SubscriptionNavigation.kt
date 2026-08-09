package app.purecipes.feature.subscription.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.subscription.ui.PaywallScreen
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installSubscriptionFlow(
	navigator: Navigator,
) {
	entry<PaywallDestination> { destination ->
		PaywallScreen(
			feature = destination.feature,
			origin = destination.origin,
			onBack = { navigator.back() },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun subscriptionNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(PaywallDestination.serializer())
	}
}
