package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformSplash(
	isAppReady: Boolean,
	onSplashExitStart: () -> Unit,
	onOverlayDraw: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
)
