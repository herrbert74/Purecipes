package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable

@Composable
fun NavigationBackHandler(
	enabled: Boolean,
	backStackDepth: Int,
	onBack: () -> Unit,
) {
	PlatformNavigationHistorySync(backStackDepth)
	HandleSystemBack(enabled = enabled, onBack = onBack)
}
