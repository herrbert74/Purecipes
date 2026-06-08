package app.purecipes.shared.ui.splash

// Single source of truth for splash timing in Kotlin. Android theme/AVD XML must mirror STIR_PERIOD_MILLIS
// manually (see themes.xml, ic_launcher_splash_animated.xml).
object SplashTimings {
	const val STIR_PERIOD_MILLIS = 1000
	const val FADE_OUT_MILLIS = 300
	const val MINIMUM_DWELL_MILLIS = 1100
	const val MAXIMUM_DWELL_MILLIS = 6000
}
