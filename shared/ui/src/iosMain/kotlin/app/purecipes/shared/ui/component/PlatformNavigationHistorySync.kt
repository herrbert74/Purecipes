package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.UIKit.UIAccessibilityPostNotification
import platform.UIKit.UIAccessibilityScreenChangedNotification

@Composable
internal actual fun PlatformNavigationHistorySync(
	backStackDepth: Int,
	screenChangeKey: Any,
) {
	var previousScreenChangeKey by remember { mutableStateOf<Any?>(null) }
	LaunchedEffect(screenChangeKey) {
		val previous = previousScreenChangeKey
		if (previous != null && previous != screenChangeKey) {
			UIAccessibilityPostNotification(UIAccessibilityScreenChangedNotification, null)
		}
		previousScreenChangeKey = screenChangeKey
	}
}
