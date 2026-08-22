package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
internal fun MainNavigationSuiteScaffold(
	selectedTab: MainTab,
	onTabSelect: (MainTab) -> Unit,
	modifier: Modifier = Modifier,
	isNavBarVisible: Boolean = true,
	content: @Composable () -> Unit,
) {
	val scaffoldState = rememberNavigationSuiteScaffoldState()
	LaunchedEffect(isNavBarVisible) {
		if (isNavBarVisible) {
			scaffoldState.show()
		} else {
			scaffoldState.hide()
		}
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
		content = content,
	)
}
