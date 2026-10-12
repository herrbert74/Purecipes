package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun MainNavigationSuiteScaffold(
	selectedTab: MainTab,
	onTabSelect: (MainTab) -> Unit,
	modifier: Modifier = Modifier,
	isNavBarVisible: Boolean = true,
	content: @Composable () -> Unit,
) {
	val scaffoldState = rememberNavigationSuiteScaffoldState()
	val scope = rememberCoroutineScope()
	val canChangeVisibility = rememberUpdatedState(isNavBarVisible)
	val scrollConnection = remember(scaffoldState, scope) {
		TabBarScrollConnection(
			scope = scope,
			scaffoldState = scaffoldState,
			canChangeVisibility = { canChangeVisibility.value },
		)
	}
	LaunchedEffect(isNavBarVisible, selectedTab.stackId) {
		scrollConnection.reset(visible = isNavBarVisible)
	}
	NavigationSuiteScaffold(
		modifier = modifier.fillMaxSize(),
		state = scaffoldState,
		navigationItems = {
			mainTabs.forEach { tab ->
				NavigationSuiteItem(
					selected = tab.stackId == selectedTab.stackId,
					onClick = { onTabSelect(tab) },
					icon = {
						Icon(
							imageVector = tab.icon,
							contentDescription = tab.label,
						)
					},
					label = { Text(text = tab.label) },
				)
			}
		},
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.nestedScroll(scrollConnection),
		) {
			content()
		}
	}
}

private class TabBarScrollConnection(
	private val scope: CoroutineScope,
	private val scaffoldState: NavigationSuiteScaffoldState,
	private val canChangeVisibility: () -> Boolean,
) : NestedScrollConnection {

	private var visibilityJob: Job? = null
	private var idleShowJob: Job? = null
	private var tabBarVisible = true
	private var accumulatedScroll = 0f

	fun reset(visible: Boolean) {
		idleShowJob?.cancel()
		accumulatedScroll = 0f
		setVisible(visible)
	}

	override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
		if (!canChangeVisibility() || !source.isDragOrFling() || abs(available.y) <= abs(available.x)) {
			return Offset.Zero
		}
		accumulatedScroll += available.y
		if (abs(accumulatedScroll) >= TAB_BAR_SCROLL_THRESHOLD || !tabBarVisible) {
			if (abs(accumulatedScroll) >= TAB_BAR_SCROLL_THRESHOLD && tabBarVisible) {
				setVisible(visible = false)
			}
			accumulatedScroll = 0f
			scheduleShowWhenIdle()
		}
		return Offset.Zero
	}

	override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
		accumulatedScroll = 0f
		idleShowJob?.cancel()
		if (canChangeVisibility() && !tabBarVisible) {
			setVisible(visible = true)
		}
		return Velocity.Zero
	}

	private fun scheduleShowWhenIdle() {
		idleShowJob?.cancel()
		idleShowJob = scope.launch {
			delay(TAB_BAR_IDLE_SHOW_MILLIS)
			if (canChangeVisibility() && !tabBarVisible) {
				setVisible(visible = true)
			}
		}
	}

	private fun setVisible(visible: Boolean) {
		tabBarVisible = visible
		visibilityJob?.cancel()
		visibilityJob = scope.launch {
			if (visible) {
				scaffoldState.show()
			} else {
				scaffoldState.hide()
			}
		}
	}
}

private fun NestedScrollSource.isDragOrFling(): Boolean {
	return this == NestedScrollSource.UserInput || this == NestedScrollSource.SideEffect
}

private const val TAB_BAR_SCROLL_THRESHOLD = 24f
private const val TAB_BAR_IDLE_SHOW_MILLIS = 200L
