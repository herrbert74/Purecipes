package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val MIN_SELECTED_COUNT_FOR_CLEAR_ALL = 2

@Composable
internal fun FilterBulkActionChips(
	onSelectAll: () -> Unit,
	onClearAll: () -> Unit,
	modifier: Modifier = Modifier,
	showClearAll: Boolean = true,
	selectAllTestTag: String? = null,
	clearAllTestTag: String? = null,
) {
	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.xs,
			),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		AssistChip(
			onClick = onSelectAll,
			label = { Text(text = "Select all") },
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.DoneAll,
					contentDescription = null,
				)
			},
			modifier = selectAllTestTag?.let { Modifier.testTag(it) } ?: Modifier,
		)
		if (showClearAll) {
			AssistChip(
				onClick = onClearAll,
				label = { Text(text = "Clear all") },
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Clear,
						contentDescription = null,
					)
				},
				modifier = clearAllTestTag?.let { Modifier.testTag(it) } ?: Modifier,
			)
		}
	}
}

@Composable
internal fun FilterClearActionChip(
	onClearAll: () -> Unit,
	modifier: Modifier = Modifier,
	clearAllTestTag: String? = null,
) {
	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.xs,
			),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		AssistChip(
			onClick = onClearAll,
			label = { Text(text = "Clear all") },
			leadingIcon = {
				Icon(
					imageVector = Icons.Default.Clear,
					contentDescription = null,
				)
			},
			modifier = clearAllTestTag?.let { Modifier.testTag(it) } ?: Modifier,
		)
	}
}
