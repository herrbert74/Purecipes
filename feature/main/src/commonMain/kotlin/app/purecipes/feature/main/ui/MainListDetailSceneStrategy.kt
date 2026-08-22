package app.purecipes.feature.main.ui

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

@Composable
fun rememberMainListDetailSceneStrategy(): ListDetailSceneStrategy<NavKey> {
	val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
	val directive = remember(windowAdaptiveInfo) {
		calculatePaneScaffoldDirective(windowAdaptiveInfo)
			.copy(horizontalPartitionSpacerSize = 0.dp)
	}
	return rememberListDetailSceneStrategy(directive = directive)
}
