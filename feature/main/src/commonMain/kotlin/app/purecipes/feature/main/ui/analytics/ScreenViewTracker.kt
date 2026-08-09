package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase

internal class ScreenViewTracker(
	private val trackScreenView: TrackScreenViewUseCase,
) {

	private var lastScreenName: String? = null
	private var lastRecipeId: Int? = null

	fun onScreenVisible(screenName: String, recipeId: Int? = null) {
		if (screenName == lastScreenName && recipeId == lastRecipeId) {
			return
		}
		val origin = lastScreenName?.let(AnalyticsOrigin::fromValue)
		lastScreenName = screenName
		lastRecipeId = recipeId
		trackScreenView(screenName = screenName, origin = origin, recipeId = recipeId)
	}
}
