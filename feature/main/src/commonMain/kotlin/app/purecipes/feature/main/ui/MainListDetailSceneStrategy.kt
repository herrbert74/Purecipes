package app.purecipes.feature.main.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import app.purecipes.feature.library.ui.navigation.CookbookDetailDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination

const val LIST_DETAIL_DRAG_HANDLE_TAG = "listDetailDragHandle"
internal const val DEFAULT_DETAIL_ANCHOR_INDEX = 1
private const val DETAIL_FOCUSED_PROPORTION = 0.35f
private const val BALANCED_PROPORTION = 0.5f
private const val LIST_FOCUSED_PROPORTION = 0.65f
private const val LIST_FOCUSED_ANCHOR_INDEX = 2

@Composable
fun rememberMainListDetailSceneStrategy(
	hasDetailPane: Boolean = false,
	preferredDetailAnchorIndex: Int = DEFAULT_DETAIL_ANCHOR_INDEX,
	onPreferredDetailAnchorIndexChange: (Int) -> Unit = {},
): ListDetailSceneStrategy<NavKey> {
	val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
	val widthDp = windowAdaptiveInfo.windowSizeClass.minWidthDp
	val directive = remember(widthDp) {
		mainListDetailDirective(windowAdaptiveInfo)
	}
	val anchors = remember { mainListDetailAnchors() }
	val clampedDetailAnchorIndex = preferredDetailAnchorIndex.coerceIn(0, anchors.lastIndex)
	val listFocusedAnchor = anchors[LIST_FOCUSED_ANCHOR_INDEX]
	val currentDetailAnchorIndex = rememberUpdatedState(clampedDetailAnchorIndex)
	val currentOnDetailAnchorIndexChange = rememberUpdatedState(onPreferredDetailAnchorIndexChange)
	val paneExpansionState = rememberPaneExpansionState(
		anchors = anchors,
		initialAnchoredIndex = if (hasDetailPane) clampedDetailAnchorIndex else LIST_FOCUSED_ANCHOR_INDEX,
	)
	var skipInitialAnchorChange by remember { mutableStateOf(true) }
	var isAnimatingAnchor by remember { mutableStateOf(false) }
	LaunchedEffect(hasDetailPane) {
		if (skipInitialAnchorChange) {
			skipInitialAnchorChange = false
			return@LaunchedEffect
		}
		isAnimatingAnchor = true
		try {
			val anchor = if (hasDetailPane) {
				anchors[currentDetailAnchorIndex.value]
			} else {
				listFocusedAnchor
			}
			paneExpansionState.animateTo(anchor)
		} finally {
			isAnimatingAnchor = false
		}
	}
	LaunchedEffect(paneExpansionState, hasDetailPane) {
		if (!hasDetailPane) {
			return@LaunchedEffect
		}
		snapshotFlow { paneExpansionState.currentAnchor }
			.collect { anchor ->
				if (!isAnimatingAnchor && anchor != null) {
					val index = anchors.indexOf(anchor)
					if (index in anchors.indices && index != currentDetailAnchorIndex.value) {
						currentOnDetailAnchorIndexChange.value(index)
					}
				}
			}
	}
	val currentHasDetailPane = rememberUpdatedState(hasDetailPane)
	val paneExpansionDragHandle: @Composable ThreePaneScaffoldScope.(PaneExpansionState) -> Unit = remember {
		{ state ->
			if (currentHasDetailPane.value) {
				val interactionSource = remember { MutableInteractionSource() }
				VerticalDragHandle(
					modifier = Modifier
						.testTag(LIST_DETAIL_DRAG_HANDLE_TAG)
						.paneExpansionDraggable(
							state = state,
							minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
							interactionSource = interactionSource,
						),
					interactionSource = interactionSource,
				)
			}
		}
	}
	return rememberListDetailSceneStrategy(
		directive = directive,
		paneExpansionDragHandle = paneExpansionDragHandle,
		paneExpansionState = paneExpansionState,
	)
}

internal fun mainListDetailAnchors(): List<PaneExpansionAnchor> = listOf(
	PaneExpansionAnchor.Proportion(DETAIL_FOCUSED_PROPORTION),
	PaneExpansionAnchor.Proportion(BALANCED_PROPORTION),
	PaneExpansionAnchor.Proportion(LIST_FOCUSED_PROPORTION),
)

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
