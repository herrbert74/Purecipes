package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase

internal class ScreenViewTracker(
	private val trackScreenView: TrackScreenViewUseCase,
) {

	private var lastScreenName: String? = null

	fun onScreenVisible(screenName: String) {
		if (screenName == lastScreenName) {
			return
		}
		val origin = lastScreenName?.let(AnalyticsOrigin::fromValue)
		lastScreenName = screenName
		trackScreenView(screenName = screenName, origin = origin)
	}
}
