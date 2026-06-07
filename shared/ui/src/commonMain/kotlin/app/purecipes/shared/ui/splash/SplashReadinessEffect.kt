package app.purecipes.shared.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@Composable
fun SplashReadinessEffect(
	isAppReady: Boolean,
	onSplashDismiss: () -> Unit,
	minimumDuration: Duration = SplashTimings.MINIMUM_DWELL_MILLIS.milliseconds,
	maximumDuration: Duration = SplashTimings.MAXIMUM_DWELL_MILLIS.milliseconds,
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
