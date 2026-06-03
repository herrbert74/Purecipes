package app.purecipes

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import app.purecipes.shared.ui.splash.SplashOverlay
import app.purecipes.shared.ui.theme.surfaceLight

private const val SPLASH_FADE_OUT_MILLIS = 600L

class AndroidOsSplashBridge(
	private val activity: ComponentActivity,
	private val onSplashDrawn: () -> Unit,
) {
	private var splashScreenViewProvider: SplashScreenViewProvider? = null
	private var splashDrawn = false

	fun install(splashScreen: SplashScreen) {
		splashScreen.setOnExitAnimationListener(::onSplashExit)
	}

	private fun onSplashExit(provider: SplashScreenViewProvider) {
		splashScreenViewProvider = provider
		provider.iconView.visibility = View.GONE
		val splashRoot = provider.view as ViewGroup
		val composeView = ComposeView(activity).apply {
			setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
			setContent {
				SplashOverlay(
					isVisible = true,
					backgroundColor = surfaceLight,
					onExitComplete = {},
					onOverlayDraw = ::reportSplashDrawn,
				)
			}
		}
		splashRoot.addView(
			composeView,
			FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT,
			),
		)
	}

	private fun reportSplashDrawn() {
		if (splashDrawn) {
			return
		}
		splashDrawn = true
		onSplashDrawn()
	}

	fun beginExit(onEnd: () -> Unit = {}) {
		val provider = splashScreenViewProvider
		if (provider == null) {
			onEnd()
			return
		}
		provider.view.animate()
			.alpha(0f)
			.setDuration(SPLASH_FADE_OUT_MILLIS)
			.withEndAction {
				provider.remove()
				splashScreenViewProvider = null
				onEnd()
			}
			.start()
	}
}
