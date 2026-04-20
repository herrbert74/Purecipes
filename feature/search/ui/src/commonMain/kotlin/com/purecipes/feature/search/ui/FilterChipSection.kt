package com.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
internal fun <T : Any> FilterChipSection(
	title: String,
	items: ImmutableList<T>,
	selected: ImmutableSet<T>,
	itemLabel: (T) -> String,
	onSelectionChange: (Set<T>) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		FilterSectionHeader(
			title = title,
			onSelectAll = { onSelectionChange(items.toSet()) },
			onClearAll = { onSelectionChange(emptySet()) },
		)
		FlowRow(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
		) {
			items.forEach { item ->
				FilterChip(
					selected = item in selected,
					onClick = {
						val updated = if (item in selected) selected - item else selected + item
						onSelectionChange(updated)
					},
					label = { Text(itemLabel(item)) },
				)
			}
		}
	}
}

@Composable
internal fun FilterSectionHeader(
	title: String,
	onSelectAll: () -> Unit,
	onClearAll: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.xs,
				top = PurecipesTheme.space.s,
			),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = title,
			style = PurecipesTheme.typography.titleSmall,
			modifier = Modifier.weight(1f),
		)
		IconButton(onClick = onSelectAll) {
			Icon(
				imageVector = Icons.Default.DoneAll,
				contentDescription = "Select all $title",
			)
		}
		IconButton(onClick = onClearAll) {
			Icon(
				imageVector = Icons.Default.Clear,
				contentDescription = "Clear $title",
			)
		}
	}
}
