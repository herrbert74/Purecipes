package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.window
import org.w3c.dom.events.Event
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun HandleSystemBack(enabled: Boolean, onBack: () -> Unit) {
	DisposableEffect(enabled, onBack) {
		if (!enabled) {
			return@DisposableEffect onDispose {}
		}
		val listener: (Event) -> Unit = {
			onBack()
		}
		window.addEventListener("popstate", listener)
		onDispose {
			window.removeEventListener("popstate", listener)
		}
	}
}
