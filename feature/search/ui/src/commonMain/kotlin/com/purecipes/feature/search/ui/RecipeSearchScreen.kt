package com.purecipes.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.requestFocus
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.ui.component.BodyText
import com.purecipes.shared.ui.component.ErrorText
import com.purecipes.shared.ui.component.TitleText
import com.purecipes.shared.ui.component.paging.PaginatedLazyColumn
import com.purecipes.shared.ui.component.paging.PaginationState
import com.purecipes.shared.ui.theme.PurecipesTheme

internal const val RECIPE_SEARCH_INPUT_TAG = "recipeSearchInput"
internal const val RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG = "recipeSearchOpenFiltersButton"

@Composable
fun RecipeSearchScreen(
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
	getSearchFilters: GetSearchFiltersUseCase,
	saveSearchFilters: SaveSearchFiltersUseCase,
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit = {},
	closeScreen: () -> Unit = {},
) {
	val viewModel = recipeSearchViewModel(
		filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
		getMeasurementPreferences = getMeasurementPreferences,
		searchRecipes = searchRecipes,
		trackEvent = trackEvent,
		getSearchFilters = getSearchFilters,
		saveSearchFilters = saveSearchFilters,
	)
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	if (viewModel.isFilterSheetVisible) {
		FilterBottomSheet(
			filters = viewModel.activeFilters,
			sheetState = sheetState,
			onFiltersChange = viewModel::onFiltersChange,
			onDismiss = viewModel::onFilterSheetDismiss,
		)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(PurecipesTheme.space.m),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		viewModel.measurementFilterLabel?.let { label ->
			Text(
				text = label,
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
		SearchBar(
			inputField = {
				SearchBarDefaults.InputField(
					query = viewModel.searchQuery,
					onQueryChange = viewModel::onSearchQueryChange,
					onSearch = { query ->
						viewModel.onSearchQueryChange(query)
						viewModel.searchNow()
					},
					expanded = viewModel.isSearchBarActive,
					onExpandedChange = {
						viewModel.onSearchBarExpandedChange(it)
						if (!viewModel.isSearchBarActive) {
							closeScreen()
						}
					},
					modifier = Modifier
						.testTag(RECIPE_SEARCH_INPUT_TAG)
						.semantics(mergeDescendants = true) {
							contentDescription = "Searchbar"
							requestFocus { false }
						},
					placeholder = { Text("Search recipes") },
					leadingIcon = {
						Text("🔎")
					},
					trailingIcon = {
						val hasActiveFilters = !viewModel.activeFilters.isEmpty
						IconButton(
							onClick = viewModel::onFilterButtonClick,
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
					},
				)
			},
			expanded = viewModel.isSearchBarActive,
			onExpandedChange = { viewModel.onSearchBarExpandedChange(it) },
			modifier = Modifier.fillMaxWidth(),
		) {
			SearchResultsContent(
				isSearching = viewModel.isSearching,
				errorMessage = viewModel.errorMessage,
				totalMatches = viewModel.totalMatches,
				paginationState = viewModel.paginationState,
				recipes = viewModel.recipes,
				onRecipeSelect = onRecipeSelect,
			)
		}

		if (!viewModel.isSearchBarActive) {
			SearchResultsContent(
				isSearching = viewModel.isSearching,
				errorMessage = viewModel.errorMessage,
				totalMatches = viewModel.totalMatches,
				paginationState = viewModel.paginationState,
				recipes = viewModel.recipes,
				onRecipeSelect = onRecipeSelect,
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun SearchResultsContent(
	isSearching: Boolean,
	errorMessage: String?,
	totalMatches: Int,
	paginationState: PaginationState<Int, RecipeSummary>,
	recipes: SnapshotStateList<RecipeSummary>,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		isSearching -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			CircularProgressIndicator()
		}

		errorMessage != null -> Box(modifier = modifier.fillMaxWidth()) {
			ErrorText(text = errorMessage)
		}

		else -> PaginatedLazyColumn(
			paginationState = paginationState,
			modifier = modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			contentPadding = PaddingValues(bottom = PurecipesTheme.space.m),
		) {
			item {
				Text(
					text = "$totalMatches recipes found",
					style = PurecipesTheme.typography.labelMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			items(recipes, key = { it.id }) { recipe ->
				RecipeRow(
					recipe = recipe,
					onClick = { onRecipeSelect(recipe.id) },
				)
			}
		}
	}
}

@Composable
private fun RecipeRow(recipe: RecipeSummary, onClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)

			Column(modifier = Modifier.weight(1f)) {
				TitleText(
					text = recipe.title,
				)
				BodyText(
					text = listOfNotNull(
						recipe.cuisine?.displayName ?: "Unknown cuisine",
						recipe.totalTime?.let { "$it min" },
					).joinToString(separator = " • "),
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
				recipe.measurementSystem?.let { measurementSystem ->
					Text(
						text = measurementSystem.displayName(),
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

private fun MeasurementSystem.displayName(): String {
	return when (this) {
		MeasurementSystem.IMPERIAL -> "Imperial"
		MeasurementSystem.METRIC -> "Metric"
		MeasurementSystem.MIXED -> "Mixed"
	}
}
