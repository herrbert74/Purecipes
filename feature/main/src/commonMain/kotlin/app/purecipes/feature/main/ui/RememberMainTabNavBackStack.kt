package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer

@Composable
internal fun rememberMainTabNavBackStack(
	saveStateKey: String,
	configuration: SavedStateConfiguration,
	root: NavKey,
): NavBackStack<NavKey> = rememberSerializable(
	saveStateKey,
	configuration = configuration,
	serializer = NavBackStackSerializer(PolymorphicSerializer(NavKey::class)),
) {
	NavBackStack(root)
}
