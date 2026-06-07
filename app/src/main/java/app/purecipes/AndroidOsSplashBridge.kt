package app.purecipes

import android.graphics.drawable.Animatable
import android.view.View
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import app.purecipes.shared.ui.splash.SplashTimings

class AndroidOsSplashBridge {

	private var splashScreenViewProvider: SplashScreenViewProvider? = null

	fun install(splashScreen: SplashScreen) {
		splashScreen.setOnExitAnimationListener(::onSplashExit)
	}

	private fun onSplashExit(provider: SplashScreenViewProvider) {
		splashScreenViewProvider = provider
		startThemeSplashAnimation(provider.iconView)
	}

	private fun startThemeSplashAnimation(iconView: View) {
		if (iconView is ImageView) {
			(iconView.drawable as? Animatable)?.start()
		}
	}

	fun dismiss(onEnd: () -> Unit = {}) {
		val provider = splashScreenViewProvider
		if (provider == null) {
			onEnd()
			return
		}
		val iconView = provider.iconView
		if (iconView is ImageView) {
			(iconView.drawable as? Animatable)?.stop()
		}
		provider.view.animate()
			.alpha(0f)
			.setDuration(SplashTimings.FADE_OUT_MILLIS.toLong())
			.withEndAction {
				provider.remove()
				splashScreenViewProvider = null
				onEnd()
			}
			.start()
	}
}
