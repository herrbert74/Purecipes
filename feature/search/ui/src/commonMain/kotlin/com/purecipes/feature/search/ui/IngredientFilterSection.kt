package com.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet

private enum class IngredientChipState { NEUTRAL, INCLUDE, EXCLUDE }

private data class IngredientChipGroup(
	val name: String,
	val items: List<String>,
)

private val INGREDIENT_GROUPS = listOf(
	IngredientChipGroup(
		name = "Proteins",
		items = listOf("Chicken", "Beef", "Pork", "Lamb", "Fish", "Shrimp", "Eggs", "Tofu"),
	),
	IngredientChipGroup(
		name = "Vegetables",
		items = listOf(
			"Onion",
			"Garlic",
			"Tomato",
			"Potato",
			"Carrot",
			"Spinach",
			"Pepper",
			"Mushroom",
			"Broccoli",
			"Zucchini",
			"Corn",
			"Peas",
		),
	),
	IngredientChipGroup(
		name = "Fruits",
		items = listOf("Lemon", "Lime", "Apple", "Banana", "Avocado", "Orange"),
	),
	IngredientChipGroup(
		name = "Dairy",
		items = listOf("Milk", "Butter", "Cheese", "Cream", "Yogurt"),
	),
	IngredientChipGroup(
		name = "Grains & Starches",
		items = listOf("Rice", "Pasta", "Bread", "Flour", "Oats", "Noodles", "Lentils", "Chickpeas", "Beans"),
	),
	IngredientChipGroup(
		name = "Herbs & Spices",
		items = listOf("Basil", "Parsley", "Cilantro", "Cumin", "Paprika", "Oregano", "Thyme", "Chili"),
	),
	IngredientChipGroup(
		name = "Oils & Condiments",
		items = listOf("Olive Oil", "Soy Sauce", "Vinegar", "Tomato Paste", "Coconut Milk"),
	),
	IngredientChipGroup(
		name = "Nuts",
		items = listOf("Almonds", "Peanuts", "Sesame", "Cashews"),
	),
	IngredientChipGroup(
		name = "Baking",
		items = listOf("Sugar", "Honey", "Chocolate", "Vanilla", "Baking Powder"),
	),
)

@Composable
internal fun IngredientFilterSection(
	includeIngredients: ImmutableSet<String>,
	excludeIngredients: ImmutableSet<String>,
	onSelectionChange: (include: Set<String>, exclude: Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allItems = INGREDIENT_GROUPS.flatMap { it.items }.toSet()

	Column(modifier = modifier) {
		FilterSectionHeader(
			title = "Ingredients",
			onSelectAll = { onSelectionChange(allItems, emptySet()) },
			onClearAll = { onSelectionChange(emptySet(), emptySet()) },
		)
		INGREDIENT_GROUPS.forEach { group ->
			IngredientGroupChips(
				group = group,
				includeIngredients = includeIngredients,
				excludeIngredients = excludeIngredients,
				onSelectionChange = onSelectionChange,
			)
		}
	}
}

@Composable
private fun IngredientGroupChips(
	group: IngredientChipGroup,
	includeIngredients: ImmutableSet<String>,
	excludeIngredients: ImmutableSet<String>,
	onSelectionChange: (include: Set<String>, exclude: Set<String>) -> Unit,
) {
	Column {
		FilterSectionHeader(
			title = group.name,
			onSelectAll = {
				onSelectionChange(
					includeIngredients + group.items,
					excludeIngredients - group.items.toSet(),
				)
			},
			onClearAll = {
				onSelectionChange(
					includeIngredients - group.items.toSet(),
					excludeIngredients - group.items.toSet(),
				)
			},
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
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
					in includeIngredients -> IngredientChipState.INCLUDE
					in excludeIngredients -> IngredientChipState.EXCLUDE
					else -> IngredientChipState.NEUTRAL
				}
				IngredientTriStateChip(
					item = item,
					state = state,
					onToggle = {
						val (newInclude, newExclude) = when (state) {
							IngredientChipState.NEUTRAL ->
								(includeIngredients + item) to (excludeIngredients - item)
							IngredientChipState.INCLUDE ->
								(includeIngredients - item) to (excludeIngredients + item)
							IngredientChipState.EXCLUDE ->
								(includeIngredients - item) to (excludeIngredients - item)
						}
						onSelectionChange(newInclude, newExclude)
					},
				)
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
	val colors = when (state) {
		IngredientChipState.EXCLUDE -> FilterChipDefaults.filterChipColors(
			selectedContainerColor = PurecipesTheme.colorScheme.errorContainer,
			selectedLabelColor = PurecipesTheme.colorScheme.onErrorContainer,
			selectedLeadingIconColor = PurecipesTheme.colorScheme.onErrorContainer,
		)
		else -> FilterChipDefaults.filterChipColors()
	}
	FilterChip(
		selected = state != IngredientChipState.NEUTRAL,
		onClick = onToggle,
		label = { Text(item) },
		colors = colors,
		leadingIcon = if (state == IngredientChipState.EXCLUDE) {
			{
				Icon(
					imageVector = Icons.Default.Close,
					contentDescription = null,
					modifier = Modifier.size(FilterChipDefaults.IconSize),
				)
			}
		} else {
			null
		},
	)
}
