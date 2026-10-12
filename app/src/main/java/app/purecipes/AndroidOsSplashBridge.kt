package app.purecipes

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import app.purecipes.shared.ui.splash.SplashAnimatedIconSpec
import app.purecipes.shared.ui.splash.SplashTimings

class AndroidOsSplashBridge {

	private var splashScreenViewProvider: SplashScreenViewProvider? = null
	private var contentDrawListener: ViewTreeObserver.OnPreDrawListener? = null
	private var contentDrawObserver: ViewTreeObserver? = null
	private var exitRequested = false
	private var waitingForDraw = false
	private var removed = false
	private var drawnReadyFrames = 0

	fun install(splashScreen: SplashScreen) {
		splashScreen.setOnExitAnimationListener(::onSplashExit)
	}

	private fun onSplashExit(provider: SplashScreenViewProvider) {
		if (removed) {
			provider.remove()
			return
		}
		splashScreenViewProvider = provider
		showLoopingSplashIcon(provider)
		if (exitRequested) {
			waitForContentDraw(provider) {}
		}
	}

	private fun showLoopingSplashIcon(provider: SplashScreenViewProvider) {
		val splashView = provider.view as? ViewGroup ?: return
		val animatedDrawable = newLoopingSplashDrawable(splashView.context) ?: return
		val overlay = ImageView(splashView.context).apply {
			scaleType = ImageView.ScaleType.FIT_CENTER
			setImageDrawable(animatedDrawable)
		}
		val iconSize = splashIconSizePx(splashView)
		val params = FrameLayout.LayoutParams(iconSize, iconSize, Gravity.CENTER)
		splashView.addView(overlay, params)
		overlay.bringToFront()
		val iconView = provider.iconView
		if (iconView is ImageView) {
			iconView.setImageDrawable(null)
		}
		iconView.alpha = 0f
		iconView.visibility = View.INVISIBLE
		animatedDrawable.start()
		overlay.post { animatedDrawable.start() }
	}

	private fun newLoopingSplashDrawable(context: Context): AnimatedVectorDrawableCompat? {
		val created = AnimatedVectorDrawableCompat.create(
			context,
			R.drawable.ic_launcher_splash_animated_loop,
		) ?: return null
		return created.mutate() as? AnimatedVectorDrawableCompat ?: created
	}

	private fun splashIconSizePx(splashView: View): Int {
		val density = splashView.resources.displayMetrics.density
		return (SplashAnimatedIconSpec.VISIBLE_SIZE_DP * density).toInt()
	}

	fun dismiss(onEnd: () -> Unit = {}) {
		val provider = splashScreenViewProvider
		if (removed || provider == null) {
			if (!removed) {
				exitRequested = true
			}
			onEnd()
			return
		}
		if (waitingForDraw) {
			fadeOut(provider, onEnd)
			return
		}
		exitRequested = true
		waitForContentDraw(provider, onEnd)
	}

	private fun waitForContentDraw(
		provider: SplashScreenViewProvider,
		onEnd: () -> Unit,
	) {
		if (removed) {
			onEnd()
			return
		}
		waitingForDraw = true
		drawnReadyFrames = 0
		val content = provider.view.rootView.findViewById(android.R.id.content) ?: provider.view
		val observer = content.viewTreeObserver
		val listener = object : ViewTreeObserver.OnPreDrawListener {
			override fun onPreDraw(): Boolean {
				drawnReadyFrames = if (contentIsReady(content)) {
					drawnReadyFrames + 1
				} else {
					0
				}
				if (drawnReadyFrames >= DRAWN_FRAMES_BEFORE_FADE) {
					clearContentDrawListener()
					fadeOut(provider, onEnd)
				}
				return true
			}
		}
		contentDrawListener = listener
		contentDrawObserver = observer
		observer.addOnPreDrawListener(listener)
		content.invalidate()
	}

	private fun contentIsReady(content: View): Boolean {
		return content is ViewGroup &&
			content.childCount > 0 &&
			content.isLaidOut &&
			content.height > 0
	}

	private fun fadeOut(provider: SplashScreenViewProvider, onEnd: () -> Unit) {
		if (removed) {
			onEnd()
			return
		}
		removed = true
		waitingForDraw = false
		drawnReadyFrames = 0
		clearContentDrawListener()
		provider.view.animate()
			.alpha(0f)
			.setDuration(SplashTimings.FADE_OUT_MILLIS.toLong())
			.withEndAction {
				provider.remove()
				if (splashScreenViewProvider === provider) {
					splashScreenViewProvider = null
				}
				onEnd()
			}
			.start()
	}

	private fun clearContentDrawListener() {
		val listener = contentDrawListener ?: return
		val observer = contentDrawObserver
		if (observer != null && observer.isAlive) {
			observer.removeOnPreDrawListener(listener)
		}
		contentDrawListener = null
		contentDrawObserver = null
	}
}

private const val DRAWN_FRAMES_BEFORE_FADE = 2
