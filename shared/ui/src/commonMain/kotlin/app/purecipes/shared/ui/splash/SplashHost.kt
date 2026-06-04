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

private const val DEFAULT_MINIMUM_MILLIS = 1400
private const val DEFAULT_MAXIMUM_MILLIS = 6000

@Composable
fun SplashReadinessEffect(
	isAppReady: Boolean,
	onSplashDismiss: () -> Unit,
	minimumDuration: Duration = DEFAULT_MINIMUM_MILLIS.milliseconds,
	maximumDuration: Duration = DEFAULT_MAXIMUM_MILLIS.milliseconds,
) {
	val startMark = remember { TimeSource.Monotonic.markNow() }
	val currentOnSplashDismiss by rememberUpdatedState(onSplashDismiss)
	var dismissed by remember { mutableStateOf(false) }

	fun dismiss() {
		if (!dismissed) {
			dismissed = true
			currentOnSplashDismiss()
		}
	}

	LaunchedEffect(isAppReady) {
		if (isAppReady) {
			val remaining = (minimumDuration - startMark.elapsedNow())
				.coerceAtLeast(Duration.ZERO)
			delay(remaining)
			dismiss()
		}
	}
	LaunchedEffect(Unit) {
		delay(maximumDuration)
		dismiss()
	}
}

@Composable
fun SplashHost(
	isAppReady: Boolean,
	splash: @Composable (isVisible: Boolean, onExitComplete: () -> Unit) -> Unit,
	modifier: Modifier = Modifier,
	minimumDuration: Duration = DEFAULT_MINIMUM_MILLIS.milliseconds,
	maximumDuration: Duration = DEFAULT_MAXIMUM_MILLIS.milliseconds,
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
