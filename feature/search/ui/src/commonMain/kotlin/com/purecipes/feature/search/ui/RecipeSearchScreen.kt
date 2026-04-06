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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.requestFocus
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.ui.component.BodyText
import com.purecipes.shared.ui.component.TitleText

@Composable
fun RecipeSearchScreen(
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	trackEvent: TrackEventUseCase,
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit = {},
	closeScreen: () -> Unit = {},
) {
	val viewModel = recipeSearchViewModel(
		filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
		getMeasurementPreferences = getMeasurementPreferences,
		searchRecipes = searchRecipes,
		trackEvent = trackEvent,
	)

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		viewModel.measurementFilterLabel?.let { label ->
			Text(
				text = label,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
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
						.semantics(mergeDescendants = true) {
							contentDescription = "Searchbar"
							requestFocus { false }
						},
					placeholder = { Text("Search recipes") },
					leadingIcon = {
						Text("🔎")
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
				recipes = viewModel.recipes,
				onRecipeSelect = onRecipeSelect,
			)
		}

		if (!viewModel.isSearchBarActive) {
			SearchResultsContent(
				isSearching = viewModel.isSearching,
				errorMessage = viewModel.errorMessage,
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
	recipes: SnapshotStateList<RecipeSummary>,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		isSearching -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			CircularProgressIndicator()
		}

		errorMessage != null -> Box(modifier = modifier.fillMaxWidth()) {
			Text(
				text = errorMessage,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.error,
			)
		}

		else -> LazyColumn(
			modifier = modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(bottom = 16.dp),
		) {
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
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(MaterialTheme.colorScheme.secondaryContainer),
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
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				recipe.measurementSystem?.let { measurementSystem ->
					Text(
						text = measurementSystem.displayName(),
						style = MaterialTheme.typography.labelMedium,
						color = MaterialTheme.colorScheme.primary,
					)
				}
			}
		}
	}
}

private fun MeasurementSystem.displayName(): String {
	return if (this == MeasurementSystem.IMPERIAL) "Imperial" else "Metric"
}
