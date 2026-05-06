package com.purecipes.feature.search.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purecipes.shared.domain.model.CalorieRange
import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.CookingTimeRange
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.DietaryPreference
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType
import com.purecipes.shared.domain.model.NutritionFilter
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

private const val SCROLLBAR_MIN_THUMB_FRACTION = 0.1f

@Composable
internal fun FilterBottomSheet(
	filters: SearchFilters,
	pantryIngredients: ImmutableSet<String>,
	sheetState: SheetState,
	onFiltersChange: (SearchFilters) -> Unit,
	onPantryIngredientsChange: (Set<String>) -> Unit,
	onDismiss: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		FilterBottomSheetContent(
			filters = filters,
			pantryIngredients = pantryIngredients,
			onFiltersChange = onFiltersChange,
			onPantryIngredientsChange = onPantryIngredientsChange,
		)
	}
}

@Composable
private fun FilterBottomSheetContent(
	filters: SearchFilters,
	pantryIngredients: ImmutableSet<String>,
	onFiltersChange: (SearchFilters) -> Unit,
	onPantryIngredientsChange: (Set<String>) -> Unit,
) {
	val scrollState = rememberLazyListState()
	Box(modifier = Modifier.fillMaxWidth()) {
		LazyColumn(
			state = scrollState,
			contentPadding = PaddingValues(bottom = PurecipesTheme.space.xxl),
		) {
			item {
				Text(
					text = "We will show only the recipes that have no missing ingredients " +
						"from your pantry, unless there are no complete matches.",
					style = PurecipesTheme.typography.bodyMedium,
					modifier = Modifier.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						top = PurecipesTheme.space.s,
					),
				)
			}
			item {
				IngredientFilterSection(
					availableIngredients = pantryIngredients,
					onSelectionChange = onPantryIngredientsChange,
				)
			}
			item {
				Text(
					text = "With the filters below you can get a more tailored result.",
					style = PurecipesTheme.typography.bodyMedium,
					modifier = Modifier.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						top = PurecipesTheme.space.m,
					),
				)
			}
			item {
				FilterChipSection(
					title = "Dietary Preferences",
					items = DietaryPreference.entries.toImmutableList(),
					selected = filters.dietaryPreferences.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(dietaryPreferences = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Cuisine",
					items = Cuisine.entries.toImmutableList(),
					selected = filters.cuisines.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(cuisines = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Meal Type",
					items = MealType.entries.toImmutableList(),
					selected = filters.mealTypes.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(mealTypes = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Cooking Time",
					items = CookingTimeRange.entries.toImmutableList(),
					selected = filters.cookingTimeRanges.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(cookingTimeRanges = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Difficulty Level",
					items = DifficultyLevel.entries.toImmutableList(),
					selected = filters.difficultyLevels.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(difficultyLevels = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Cooking Method",
					items = CookingMethod.entries.toImmutableList(),
					selected = filters.cookingMethods.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(cookingMethods = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Calorie Range",
					items = CalorieRange.entries.toImmutableList(),
					selected = filters.calorieRanges.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(calorieRanges = it)) },
				)
			}
			item {
				FilterChipSection(
					title = "Nutrition",
					items = NutritionFilter.entries.toImmutableList(),
					selected = filters.nutritionFilters.toImmutableSet(),
					itemLabel = { it.displayName },
					onSelectionChange = { onFiltersChange(filters.copy(nutritionFilters = it)) },
				)
			}
		}
		VerticalScrollbar(
			state = scrollState,
			modifier = Modifier.align(Alignment.CenterEnd),
		)
	}
}

@Composable
private fun VerticalScrollbar(state: LazyListState, modifier: Modifier = Modifier) {
	val thumbFractions by remember {
		derivedStateOf {
			val layoutInfo = state.layoutInfo
			val totalItems = layoutInfo.totalItemsCount
			val visibleItems = layoutInfo.visibleItemsInfo
			if (totalItems == 0 || visibleItems.isEmpty()) return@derivedStateOf null
			val viewportH = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
			if (viewportH <= 0f) return@derivedStateOf null
			val firstItem = visibleItems.first()
			val lastItem = visibleItems.last()
			val avgH = if (visibleItems.size > 1) {
				(lastItem.offset + lastItem.size - firstItem.offset).toFloat() / visibleItems.size
			} else {
				firstItem.size.toFloat()
			}
			val totalH = totalItems * avgH
			if (totalH <= viewportH) return@derivedStateOf null
			val thumbH = (viewportH / totalH).coerceIn(SCROLLBAR_MIN_THUMB_FRACTION, 1f)
			val scrolled = firstItem.index * avgH - firstItem.offset.toFloat()
			val maxScroll = totalH - viewportH
			val thumbStart = if (maxScroll > 0f) {
				(scrolled / maxScroll * (1f - thumbH)).coerceIn(0f, 1f - thumbH)
			} else {
				0f
			}
			thumbStart to thumbStart + thumbH
		}
	}
	val (thumbStart, thumbEnd) = thumbFractions ?: return
	Canvas(
		modifier = modifier
			.fillMaxHeight()
			.width(6.dp),
	) {
		drawRoundRect(
			color = Color.Gray.copy(alpha = 0.4f),
			topLeft = Offset(1.dp.toPx(), size.height * thumbStart),
			size = Size(4.dp.toPx(), size.height * (thumbEnd - thumbStart)),
			cornerRadius = CornerRadius(2.dp.toPx()),
		)
	}
}

@Preview(
	name = "Filter Bottom Sheet Light Phone",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Preview(
	name = "Filter Bottom Sheet Light Tablet",
	device = Devices.TABLET,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FilterBottomSheetLightPreview() {
	FilterBottomSheetPreviewContent(darkTheme = false)
}

@Preview(
	name = "Filter Bottom Sheet Dark Phone",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Preview(
	name = "Filter Bottom Sheet Dark Tablet",
	device = Devices.TABLET,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun FilterBottomSheetDarkPreview() {
	FilterBottomSheetPreviewContent(darkTheme = true)
}

@Preview(
	name = "Filter Bottom Sheet Font 85",
	device = Devices.PIXEL_4,
	fontScale = 0.85f,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Preview(
	name = "Filter Bottom Sheet Font 130",
	device = Devices.PIXEL_4,
	fontScale = 1.3f,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FilterBottomSheetFontScalePreview() {
	FilterBottomSheetPreviewContent(darkTheme = false)
}

@Composable
private fun FilterBottomSheetPreviewContent(darkTheme: Boolean) {
	PurecipesTheme(darkTheme = darkTheme) {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = PurecipesTheme.colorScheme.surfaceContainerLow,
		) {
			FilterBottomSheetContent(
				filters = SearchFilters.default(),
				pantryIngredients = persistentSetOf(),
				onFiltersChange = {},
				onPantryIngredientsChange = {},
			)
		}
	}
}
