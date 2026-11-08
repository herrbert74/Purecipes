package app.purecipes.feature.main.ui

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import app.purecipes.feature.library.ui.navigation.CookbookDetailDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination

private val EMPTY_DETAIL_PANE_WIDTH = 280.dp
private val SELECTED_LIST_PANE_WIDTH = 480.dp

@Composable
fun rememberMainListDetailSceneStrategy(
	hasDetailPane: Boolean = false,
): ListDetailSceneStrategy<NavKey> {
	val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
	val widthDp = windowAdaptiveInfo.windowSizeClass.minWidthDp
	val directive = remember(widthDp) {
		mainListDetailDirective(windowAdaptiveInfo)
	}
	val placeholderAnchor = remember {
		PaneExpansionAnchor.Offset.fromEnd(EMPTY_DETAIL_PANE_WIDTH)
	}
	val selectedAnchor = remember {
		PaneExpansionAnchor.Offset.fromStart(SELECTED_LIST_PANE_WIDTH)
	}
	val anchors = remember(placeholderAnchor, selectedAnchor) {
		listOf(placeholderAnchor, selectedAnchor)
	}
	val paneExpansionState = rememberPaneExpansionState(
		anchors = anchors,
		initialAnchoredIndex = if (hasDetailPane) 1 else 0,
	)
	var skipInitialAnchorChange by remember { mutableStateOf(true) }
	LaunchedEffect(hasDetailPane) {
		if (skipInitialAnchorChange) {
			skipInitialAnchorChange = false
			return@LaunchedEffect
		}
		val anchor = if (hasDetailPane) {
			selectedAnchor
		} else {
			placeholderAnchor
		}
		paneExpansionState.animateTo(anchor)
	}
	return rememberListDetailSceneStrategy(
		directive = directive,
		paneExpansionState = paneExpansionState,
	)
}

internal fun mainListDetailDirective(windowAdaptiveInfo: WindowAdaptiveInfo): PaneScaffoldDirective {
	val calculated = calculatePaneScaffoldDirective(windowAdaptiveInfo)
	val sideBySide = windowAdaptiveInfo.windowSizeClass.minWidthDp >=
		WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND
	return PaneScaffoldDirective(
		maxHorizontalPartitions = if (sideBySide) 2 else 1,
		horizontalPartitionSpacerSize = 0.dp,
		maxVerticalPartitions = 1,
		verticalPartitionSpacerSize = 0.dp,
		defaultPanePreferredWidth = calculated.defaultPanePreferredWidth,
		defaultPanePreferredHeight = calculated.defaultPanePreferredHeight,
		excludedBounds = calculated.excludedBounds,
		shouldAutoFocusCurrentDestination = false,
	)
}

internal fun showsDetailPane(backStack: List<NavKey>): Boolean = backStack.any { destination ->
	destination is RecipeDetailsDestination || destination is CookbookDetailDestination
}
