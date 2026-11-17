package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable

@Composable
fun NavigationBackHandler(
	enabled: Boolean,
	backStackDepth: Int,
	screenChangeKey: Any = backStackDepth,
	onBack: () -> Unit,
) {
	PlatformNavigationHistorySync(
		backStackDepth = backStackDepth,
		screenChangeKey = screenChangeKey,
	)
	HandleSystemBack(enabled = enabled, onBack = onBack)
}
