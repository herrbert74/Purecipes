package app.purecipes.feature.library.ui

import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable

@Composable
fun rememberShowCookbookDetailBackNavigation(): Boolean {
	val listDetailSceneScope = LocalListDetailSceneScope.current ?: return true
	val scaffoldValue = listDetailSceneScope.scaffoldTransitionScope.scaffoldStateTransition.currentState
	var visiblePaneCount = 0
	if (scaffoldValue.primary != PaneAdaptedValue.Hidden) visiblePaneCount++
	if (scaffoldValue.secondary != PaneAdaptedValue.Hidden) visiblePaneCount++
	if (scaffoldValue.tertiary != PaneAdaptedValue.Hidden) visiblePaneCount++
	return visiblePaneCount <= 1
}
