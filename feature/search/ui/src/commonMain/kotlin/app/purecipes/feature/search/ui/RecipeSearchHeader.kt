package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.requestFocus
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val RECIPE_SEARCH_INPUT_TAG = "recipeSearchInput"
internal const val RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG = "recipeSearchOpenFiltersButton"
internal const val RECIPE_SEARCH_COLLAPSED_BAR_TAG = "recipeSearchCollapsedBar"

internal const val RECIPE_SEARCH_TITLE = "Search in recipe titles"
internal const val RECIPE_SEARCH_HELPER =
	"Only recipe titles are matched. Use the filter button to narrow results by cuisine, time, pantry, and more."

@Composable
internal fun RecipeSearchHeader(
	isSearchBarActive: Boolean,
	searchQuery: String,
	hasActiveFilters: Boolean,
	onFilterClick: () -> Unit,
	onExpandSearch: () -> Unit,
	onCloseSearch: () -> Unit,
	onSearchQueryChange: (String) -> Unit,
	onSearchImeSearch: () -> Unit,
	onClearSearchText: () -> Unit,
) {
	val focusRequester = remember { FocusRequester() }
	val searchPillShape = RoundedCornerShape(PurecipesTheme.space.l)
	val pillFieldColors = OutlinedTextFieldDefaults.colors(
		focusedContainerColor = Color.Transparent,
		unfocusedContainerColor = Color.Transparent,
		disabledContainerColor = Color.Transparent,
		focusedBorderColor = Color.Transparent,
		unfocusedBorderColor = Color.Transparent,
		disabledBorderColor = Color.Transparent,
	)
	LaunchedEffect(isSearchBarActive) {
		if (isSearchBarActive) {
			focusRequester.requestFocus()
		}
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalAlignment = Alignment.CenterVertically,
	) {
		IconButton(
			onClick = onFilterClick,
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
		if (isSearchBarActive) {
			Surface(
				modifier = Modifier.weight(1f),
				shape = searchPillShape,
				color = PurecipesTheme.colorScheme.surfaceVariant,
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							start = PurecipesTheme.space.s,
							end = PurecipesTheme.space.s,
						),
					verticalAlignment = Alignment.CenterVertically,
				) {
					IconButton(
						onClick = onCloseSearch,
					) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Close title search",
						)
					}
					OutlinedTextField(
						value = searchQuery,
						onValueChange = onSearchQueryChange,
						modifier = Modifier
							.weight(1f)
							.focusRequester(focusRequester)
							.testTag(RECIPE_SEARCH_INPUT_TAG)
							.semantics(mergeDescendants = true) {
								contentDescription = RECIPE_SEARCH_TITLE
								requestFocus { false }
							},
						singleLine = true,
						placeholder = { Text(RECIPE_SEARCH_TITLE) },
						keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
						keyboardActions = KeyboardActions(
							onSearch = { onSearchImeSearch() },
						),
						trailingIcon = {
							if (searchQuery.isNotEmpty()) {
								IconButton(onClick = onClearSearchText) {
									Icon(
										imageVector = Icons.Default.Clear,
										contentDescription = "Clear search text",
									)
								}
							}
						},
						shape = RoundedCornerShape(0.dp),
						colors = pillFieldColors,
					)
				}
			}
		} else {
			IconButton(
				onClick = onExpandSearch,
				modifier = Modifier
					.testTag(RECIPE_SEARCH_COLLAPSED_BAR_TAG)
					.semantics {
						contentDescription = RECIPE_SEARCH_TITLE
					},
			) {
				Icon(
					imageVector = Icons.Default.Search,
					contentDescription = null,
					tint = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Preview(
	name = "Recipe search header collapsed light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchHeaderCollapsedLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchHeader(
				isSearchBarActive = false,
				searchQuery = "",
				hasActiveFilters = false,
				onFilterClick = {},
				onExpandSearch = {},
				onCloseSearch = {},
				onSearchQueryChange = {},
				onSearchImeSearch = {},
				onClearSearchText = {},
			)
		}
	}
}

@Preview(
	name = "Recipe search header collapsed filters active",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchHeaderCollapsedFiltersActivePreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchHeader(
				isSearchBarActive = false,
				searchQuery = "",
				hasActiveFilters = true,
				onFilterClick = {},
				onExpandSearch = {},
				onCloseSearch = {},
				onSearchQueryChange = {},
				onSearchImeSearch = {},
				onClearSearchText = {},
			)
		}
	}
}

@Preview(
	name = "Recipe search header expanded light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchHeaderExpandedLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchHeader(
				isSearchBarActive = true,
				searchQuery = "pasta",
				hasActiveFilters = false,
				onFilterClick = {},
				onExpandSearch = {},
				onCloseSearch = {},
				onSearchQueryChange = {},
				onSearchImeSearch = {},
				onClearSearchText = {},
			)
		}
	}
}

@Preview(
	name = "Recipe search header expanded dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeSearchHeaderExpandedDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface(modifier = Modifier.padding(PurecipesTheme.space.m)) {
			RecipeSearchHeader(
				isSearchBarActive = true,
				searchQuery = "",
				hasActiveFilters = true,
				onFilterClick = {},
				onExpandSearch = {},
				onCloseSearch = {},
				onSearchQueryChange = {},
				onSearchImeSearch = {},
				onClearSearchText = {},
			)
		}
	}
}
