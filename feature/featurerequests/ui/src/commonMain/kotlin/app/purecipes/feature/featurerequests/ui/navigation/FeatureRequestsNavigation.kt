package app.purecipes.feature.featurerequests.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.featurerequests.ui.FeatureRequestDetailScreen
import app.purecipes.feature.featurerequests.ui.FeatureRequestsScreen
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun EntryProviderScope<NavKey>.installFeatureRequestsFlow(
	navigator: Navigator,
	sessionKey: String?,
	onRequestLogIn: () -> Unit,
) {
	entry<FeatureRequestsDestination> {
		FeatureRequestsScreen(
			onBack = { navigator.back() },
			onFeatureRequestSelect = { requestId ->
				navigator.push(FeatureRequestDetailDestination(requestId = requestId))
			},
			modifier = Modifier.fillMaxSize(),
			sessionKey = sessionKey,
			onRequestLogIn = onRequestLogIn,
		)
	}
	entry<FeatureRequestDetailDestination> { destination ->
		FeatureRequestDetailScreen(
			requestId = destination.requestId,
			onBack = { navigator.back() },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun featureRequestsNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(FeatureRequestsDestination.serializer())
		subclass(FeatureRequestDetailDestination.serializer())
	}
}
