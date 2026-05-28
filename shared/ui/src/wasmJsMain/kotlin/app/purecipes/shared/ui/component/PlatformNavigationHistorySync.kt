package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
internal actual fun PlatformNavigationHistorySync(backStackDepth: Int) {
	var previousDepth by remember { mutableIntStateOf(backStackDepth) }
	LaunchedEffect(backStackDepth) {
		if (backStackDepth > previousDepth) {
			window.history.pushState(null, "", window.location.href)
		}
		previousDepth = backStackDepth
	}
}
