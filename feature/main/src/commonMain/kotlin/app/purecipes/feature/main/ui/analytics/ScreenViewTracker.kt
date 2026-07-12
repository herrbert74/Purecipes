package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase

internal class ScreenViewTracker(
	private val trackScreenView: TrackScreenViewUseCase,
) {

	private var lastScreenName: String? = null

	fun onScreenVisible(screenName: String) {
		if (screenName == lastScreenName) {
			return
		}
		val origin = lastScreenName
		lastScreenName = screenName
		trackScreenView(screenName = screenName, origin = origin)
	}
}
