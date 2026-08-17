package app.purecipes.feature.main.ui.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.main.ui.MainTab

@Composable
internal fun TrackActiveScreenViews(
	selectedTab: MainTab,
	backStack: NavBackStack<NavKey>,
	screenViewTracker: ScreenViewTracker,
) {
	val currentDestination = backStack.lastOrNull()
	LaunchedEffect(selectedTab, currentDestination) {
		val screenName = currentDestination?.toAnalyticsScreenName() ?: return@LaunchedEffect
		screenViewTracker.onScreenVisible(
			screenName = screenName,
			recipeId = currentDestination.toAnalyticsRecipeId(),
		)
	}
}
