package app.purecipes.shared.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Composable
fun SplashHost(
	isAppReady: Boolean,
	splash: @Composable (isVisible: Boolean, onExitComplete: () -> Unit) -> Unit,
	modifier: Modifier = Modifier,
	minimumDuration: Duration = SplashTimings.MINIMUM_DWELL_MILLIS.milliseconds,
	maximumDuration: Duration = SplashTimings.MAXIMUM_DWELL_MILLIS.milliseconds,
	onSplashExitStart: () -> Unit = {},
	content: @Composable () -> Unit,
) {
	var showSplash by remember { mutableStateOf(true) }
	var isVisible by remember { mutableStateOf(true) }
	val startMark = remember { TimeSource.Monotonic.markNow() }
	val currentOnSplashExitStart by rememberUpdatedState(onSplashExitStart)

	LaunchedEffect(isVisible) {
		if (!isVisible) {
			currentOnSplashExitStart()
		}
	}

	Box(modifier = modifier.fillMaxSize()) {
		content()

		if (showSplash) {
			LaunchedEffect(isAppReady) {
				if (isAppReady) {
					val remaining = (minimumDuration - startMark.elapsedNow())
						.coerceAtLeast(Duration.ZERO)
					delay(remaining)
					isVisible = false
				}
			}
			LaunchedEffect(Unit) {
				delay(maximumDuration)
				isVisible = false
			}
			splash(isVisible) { showSplash = false }
		}
	}
}
