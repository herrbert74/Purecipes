package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientCatalogueGroup
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet

internal const val FILTER_PANTRY_BULK_SELECT_ALL_TAG = "filterPantryBulkSelectAll"
internal const val FILTER_PANTRY_BULK_CLEAR_ALL_TAG = "filterPantryBulkClearAll"

private enum class IngredientChipState { NEUTRAL, SELECTED }

@Composable
internal fun IngredientFilterSection(
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allItems = IngredientCatalogue.allItems

	Column(modifier = modifier) {
		FilterBulkActionChips(
			onSelectAll = { onSelectionChange(allItems) },
			onClearAll = { onSelectionChange(emptySet()) },
			selectAllTestTag = FILTER_PANTRY_BULK_SELECT_ALL_TAG,
			clearAllTestTag = FILTER_PANTRY_BULK_CLEAR_ALL_TAG,
		)
		IngredientCatalogue.groups.forEach { group ->
			IngredientGroupChips(
				group = group,
				availableIngredients = availableIngredients,
				onSelectionChange = onSelectionChange,
			)
		}
	}
}

@Composable
private fun IngredientGroupChips(
	group: IngredientCatalogueGroup,
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
) {
	var collapsed by rememberSaveable { mutableStateOf(true) }
	Column {
		FilterSectionHeader(
			title = group.name,
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			isCollapsed = collapsed,
			onToggleCollapse = { collapsed = !collapsed },
		)
		AnimatedVisibility(
			visible = !collapsed,
			enter = expandVertically(),
			exit = shrinkVertically(),
		) {
			Column {
				FilterBulkActionChips(
					onSelectAll = {
						onSelectionChange(availableIngredients + group.items)
					},
					onClearAll = {
						onSelectionChange(availableIngredients - group.items.toSet())
					},
				)
				FlowRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.xl),
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					group.items.forEach { item ->
						val state = when (item) {
							in availableIngredients -> IngredientChipState.SELECTED
							else -> IngredientChipState.NEUTRAL
						}
						IngredientTriStateChip(
							item = item,
							state = state,
							onToggle = {
								val newAvailable = when (state) {
									IngredientChipState.NEUTRAL -> availableIngredients + item
									IngredientChipState.SELECTED -> availableIngredients - item
								}
								onSelectionChange(newAvailable)
							},
						)
					}
				}
			}
		}
	}
}

@Composable
private fun IngredientTriStateChip(
	item: String,
	state: IngredientChipState,
	onToggle: () -> Unit,
) {
	FilterChip(
		selected = state != IngredientChipState.NEUTRAL,
		onClick = onToggle,
		label = { Text(item) },
	)
}
