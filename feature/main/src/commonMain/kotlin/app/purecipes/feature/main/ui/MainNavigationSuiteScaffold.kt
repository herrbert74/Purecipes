package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun MainNavigationSuiteScaffold(
	selectedTab: MainTab,
	onTabSelect: (MainTab) -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	NavigationSuiteScaffold(
		modifier = modifier.fillMaxSize(),
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
