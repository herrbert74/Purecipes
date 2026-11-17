package app.purecipes.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
actual fun rememberReducedMotion(): Boolean {
	val mediaQuery = remember {
		window.matchMedia("(prefers-reduced-motion: reduce)")
	}
	var reducedMotion by remember(mediaQuery) {
		mutableStateOf(mediaQuery.matches)
	}
	DisposableEffect(mediaQuery) {
		val listener: (Event) -> Unit = {
			reducedMotion = mediaQuery.matches
		}
		mediaQuery.addEventListener("change", listener)
		onDispose {
			mediaQuery.removeEventListener("change", listener)
		}
	}
	return reducedMotion
}
