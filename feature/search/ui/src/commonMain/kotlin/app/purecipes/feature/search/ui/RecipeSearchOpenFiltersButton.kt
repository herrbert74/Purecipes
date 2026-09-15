package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG = "recipeSearchOpenFiltersButton"

@Composable
internal fun RecipeSearchOpenFiltersButton(
	hasActiveFilters: Boolean,
	onClick: () -> Unit,
) {
	IconButton(
		onClick = onClick,
		modifier = Modifier.testTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG),
	) {
		Icon(
			imageVector = Icons.Default.FilterList,
			contentDescription = "Open filters",
			tint = if (hasActiveFilters) {
				PurecipesTheme.colorScheme.primary
			} else {
				PurecipesTheme.colorScheme.onSurfaceVariant
			},
		)
	}
}

@Preview(
	name = "Recipe search open filters button light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchOpenFiltersButtonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchOpenFiltersButton(
				hasActiveFilters = false,
				onClick = {},
			)
		}
	}
}

@Preview(
	name = "Recipe search open filters button active",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchOpenFiltersButtonActivePreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchOpenFiltersButton(
				hasActiveFilters = true,
				onClick = {},
			)
		}
	}
}
