package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.splash.SplashReadinessEffect

@Composable
actual fun PlatformSplash(
	isAppReady: Boolean,
	onSplashExitStart: () -> Unit,
	onMainContentStart: () -> Unit,
	modifier: Modifier,
	content: @Composable () -> Unit,
) {
	SplashReadinessEffect(
		isAppReady = isAppReady,
		onSplashDismiss = onSplashExitStart,
	)
	val currentOnMainContentStart by rememberUpdatedState(onMainContentStart)
	LaunchedEffect(Unit) {
		currentOnMainContentStart()
	}
	Box(modifier = modifier.fillMaxSize()) {
		content()
	}
}
