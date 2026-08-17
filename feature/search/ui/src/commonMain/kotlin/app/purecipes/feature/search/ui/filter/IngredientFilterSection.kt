package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientCatalogueGroup
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet

internal const val FILTER_PANTRY_BULK_SELECT_ALL_TAG = "filterPantryBulkSelectAll"
internal const val FILTER_PANTRY_BULK_CLEAR_ALL_TAG = "filterPantryBulkClearAll"
internal const val FILTER_INGREDIENT_LEGEND_NEUTRAL_TAG = "filterIngredientLegendNeutral"
internal const val FILTER_INGREDIENT_LEGEND_PANTRY_TAG = "filterIngredientLegendPantry"
internal const val FILTER_INGREDIENT_LEGEND_EXCLUDED_TAG = "filterIngredientLegendExcluded"

@Composable
internal fun IngredientFilterLegend(modifier: Modifier = Modifier) {
	val chipShape = RoundedCornerShape(PurecipesTheme.space.l)
	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = PurecipesTheme.space.m),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		Text(
			text = "Not selected",
			style = PurecipesTheme.typography.labelLarge,
			modifier = Modifier
				.testTag(FILTER_INGREDIENT_LEGEND_NEUTRAL_TAG)
				.clip(chipShape)
				.background(PurecipesTheme.colorScheme.surfaceContainerHigh)
				.padding(
					horizontal = PurecipesTheme.space.m,
					vertical = PurecipesTheme.space.xs,
				),
		)
		Text(
			text = "In pantry",
			style = PurecipesTheme.typography.labelLarge,
			color = PurecipesTheme.colorScheme.onSecondaryContainer,
			modifier = Modifier
				.testTag(FILTER_INGREDIENT_LEGEND_PANTRY_TAG)
				.clip(chipShape)
				.background(PurecipesTheme.colorScheme.secondaryContainer)
				.padding(
					horizontal = PurecipesTheme.space.m,
					vertical = PurecipesTheme.space.xs,
				),
		)
		Text(
			text = "Excluded",
			style = PurecipesTheme.typography.labelLarge,
			color = PurecipesTheme.colorScheme.onErrorContainer,
			modifier = Modifier
				.testTag(FILTER_INGREDIENT_LEGEND_EXCLUDED_TAG)
				.clip(chipShape)
				.background(PurecipesTheme.colorScheme.errorContainer)
				.padding(
					horizontal = PurecipesTheme.space.m,
					vertical = PurecipesTheme.space.xs,
				),
		)
	}
}

@Composable
internal fun IngredientFilterSection(
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	onSelectionChange: (pantryIngredients: Set<String>, excludedIngredients: Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allItems = IngredientCatalogue.allItems
	val selectedPantryCount = pantryIngredients.size
	val selectedExcludedCount = excludedIngredients.size

	Column(modifier = modifier) {
		FilterBulkActionChips(
			onSelectAll = {
				onSelectionChange(
					pantryIngredients + allItems,
					excludedIngredients - allItems,
				)
			},
			onClearAll = {
				onSelectionChange(
					pantryIngredients - allItems,
					excludedIngredients - allItems,
				)
			},
			showClearAll = selectedPantryCount + selectedExcludedCount >= MIN_SELECTED_COUNT_FOR_CLEAR_ALL,
			selectAllTestTag = FILTER_PANTRY_BULK_SELECT_ALL_TAG,
			clearAllTestTag = FILTER_PANTRY_BULK_CLEAR_ALL_TAG,
		)
		IngredientCatalogue.groups.forEach { group ->
			IngredientGroupChips(
				group = group,
				pantryIngredients = pantryIngredients,
				excludedIngredients = excludedIngredients,
				onSelectionChange = onSelectionChange,
			)
		}
	}
}

@Composable
private fun IngredientGroupChips(
	group: IngredientCatalogueGroup,
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	onSelectionChange: (pantryIngredients: Set<String>, excludedIngredients: Set<String>) -> Unit,
) {
	var collapsed by rememberSaveable { mutableStateOf(true) }
	val groupItems = group.items.toSet()
	val selectedPantryCount = group.items.count { it in pantryIngredients }
	val selectedExcludedCount = group.items.count { it in excludedIngredients }
	val selectedLabels = group.items.filter { it in pantryIngredients || it in excludedIngredients }
	Column {
		FilterSectionHeader(
			title = group.name,
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			isCollapsed = collapsed,
			subtitle = formatFilterSectionSelectionSubtitle(selectedLabels),
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
						onSelectionChange(
							pantryIngredients + group.items,
							excludedIngredients - groupItems,
						)
					},
					onClearAll = {
						onSelectionChange(
							pantryIngredients - groupItems,
							excludedIngredients - groupItems,
						)
					},
					showClearAll = selectedPantryCount + selectedExcludedCount >= MIN_SELECTED_COUNT_FOR_CLEAR_ALL,
				)
				FlowRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.xl),
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					group.items.forEach { item ->
						val aliasSiblings = IngredientCatalogue.aliasSiblingsByItem[item].orEmpty()
						val state = when {
							item in excludedIngredients || aliasSiblings.any { it in excludedIngredients } ->
								IngredientChipState.EXCLUDED

							item in pantryIngredients || aliasSiblings.any { it in pantryIngredients } ->
								IngredientChipState.SELECTED

							else -> IngredientChipState.NEUTRAL
						}
						IngredientTriStateChip(
							item = item,
							state = state,
							onToggle = {
								val relatedItems = aliasSiblings + item
								val (newPantry, newExcluded) = when (state) {
									IngredientChipState.NEUTRAL ->
										(pantryIngredients + item) to (excludedIngredients - relatedItems)

									IngredientChipState.SELECTED ->
										(pantryIngredients - relatedItems) to (excludedIngredients + relatedItems)

									IngredientChipState.EXCLUDED ->
										(pantryIngredients - relatedItems) to (excludedIngredients - relatedItems)
								}
								onSelectionChange(newPantry, newExcluded)
							},
						)
					}
				}
			}
		}
	}
}
