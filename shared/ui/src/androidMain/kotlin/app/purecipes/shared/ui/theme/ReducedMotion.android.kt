package app.purecipes.shared.ui.theme

import android.animation.ValueAnimator
import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
actual fun rememberReducedMotion(): Boolean {
	if (LocalInspectionMode.current) {
		return false
	}
	val context = LocalContext.current
	var reducedMotion by remember(context) {
		mutableStateOf(isReducedMotionEnabled(context))
	}
	DisposableEffect(context) {
		val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
			override fun onChange(selfChange: Boolean) {
				reducedMotion = isReducedMotionEnabled(context)
			}
		}
		context.contentResolver.registerContentObserver(
			Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
			false,
			observer,
		)
		onDispose {
			context.contentResolver.unregisterContentObserver(observer)
		}
	}
	return reducedMotion
}

private fun isReducedMotionEnabled(context: Context): Boolean {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()) {
		return true
	}
	val scale = Settings.Global.getFloat(
		context.contentResolver,
		Settings.Global.ANIMATOR_DURATION_SCALE,
		1f,
	)
	return scale == 0f
}
