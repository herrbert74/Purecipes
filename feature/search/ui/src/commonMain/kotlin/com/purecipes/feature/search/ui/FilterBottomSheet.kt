package com.purecipes.feature.search.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

@Composable
internal fun FilterBottomSheet(
	filters: SearchFilters,
	sheetState: SheetState,
	onFiltersChange: (SearchFilters) -> Unit,
	onDismiss: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		FilterBottomSheetContent(
			filters = filters,
			onFiltersChange = onFiltersChange,
		)
	}
}

@Composable
private fun FilterBottomSheetContent(
	filters: SearchFilters,
	onFiltersChange: (SearchFilters) -> Unit,
) {
	LazyColumn(
		contentPadding = PaddingValues(bottom = PurecipesTheme.space.xxl),
	) {
		item {
			IngredientFilterSection(
				availableIngredients = filters.availableIngredients.toImmutableSet(),
				onSelectionChange = { available ->
					onFiltersChange(
						filters.copy(availableIngredients = available),
					)
				},
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
				onFiltersChange = {},
			)
		}
	}
}
