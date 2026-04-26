package com.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet

private enum class IngredientChipState { NEUTRAL, SELECTED }

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
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
	modifier: Modifier = Modifier,
) {
	val allItems = INGREDIENT_GROUPS.flatMap { it.items }.toSet()

	Column(modifier = modifier) {
		FilterSectionHeader(
			title = "Ingredients",
			onSelectAll = { onSelectionChange(allItems) },
			onClearAll = { onSelectionChange(emptySet()) },
		)
		INGREDIENT_GROUPS.forEach { group ->
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
	group: IngredientChipGroup,
	availableIngredients: ImmutableSet<String>,
	onSelectionChange: (available: Set<String>) -> Unit,
) {
	Column {
		FilterSectionHeader(
			title = group.name,
			onSelectAll = {
				onSelectionChange(availableIngredients + group.items)
			},
			onClearAll = {
				onSelectionChange(availableIngredients - group.items.toSet())
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
