package app.purecipes

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
		if (iconView !is ImageView) {
			return
		}
		val animation = iconView.drawable as? Animatable ?: loadPreApi31SplashAnimation(iconView)
		animation?.start()
	}

	private fun loadPreApi31SplashAnimation(iconView: ImageView): Animatable? {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			return null
		}
		val animatedDrawable = AnimatedVectorDrawableCompat.create(
			iconView.context,
			R.drawable.ic_launcher_splash_animated,
		) ?: return null
		iconView.setImageDrawable(animatedDrawable)
		return animatedDrawable
	}

	fun dismiss(onEnd: () -> Unit = {}) {
		val provider = splashScreenViewProvider
		if (provider == null) {
			onEnd()
			return
		}
		val iconView = provider.iconView
		if (iconView is ImageView) {
			stopSplashAnimation(iconView.drawable)
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

	private fun stopSplashAnimation(drawable: Drawable?) {
		(drawable as? Animatable)?.stop()
	}
}
