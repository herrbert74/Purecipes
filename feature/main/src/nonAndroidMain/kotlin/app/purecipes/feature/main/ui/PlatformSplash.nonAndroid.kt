package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.splash.SplashHost
import app.purecipes.shared.ui.splash.SplashOverlay
import app.purecipes.shared.ui.theme.surfaceLight

@Composable
actual fun PlatformSplash(
	isAppReady: Boolean,
	onSplashExitStart: () -> Unit,
	onMainContentStart: () -> Unit,
	modifier: Modifier,
	content: @Composable () -> Unit,
) {
	SplashHost(
		isAppReady = isAppReady,
		onSplashExitStart = onSplashExitStart,
		splash = { isVisible, onExitComplete ->
			SplashOverlay(
				isVisible = isVisible,
				backgroundColor = surfaceLight,
				onExitComplete = onExitComplete,
				onOverlayDraw = onMainContentStart,
			)
		},
		modifier = modifier,
	) {
		content()
	}
}
