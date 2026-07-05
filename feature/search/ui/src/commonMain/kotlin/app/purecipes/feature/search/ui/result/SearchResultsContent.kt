package app.purecipes.feature.search.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.paging.PaginatedLazyColumn
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun SearchResultsContent(
	isSearching: Boolean,
	errorMessage: String?,
	totalMatches: Int,
	paginationState: PaginationState<Int, RecipeSummary>,
	recipes: SnapshotStateList<RecipeSummary>,
	onRecipeSelect: (Int) -> Unit,
	onRetryClick: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	when {
		isSearching -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			CircularProgressIndicator()
		}

		errorMessage != null -> Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Text(
					text = errorMessage,
					style = PurecipesTheme.typography.bodyLarge,
					color = PurecipesTheme.colorScheme.error,
					textAlign = TextAlign.Center,
				)
				Spacer(modifier = Modifier.height(PurecipesTheme.space.l))
				Button(onClick = onRetryClick) {
					Text(text = "Retry")
				}
			}
		}

		else -> PaginatedLazyColumn(
			paginationState = paginationState,
			requestInitialPageAutomatically = false,
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

private val previewRecipeA = RecipeSummary(
	id = 1,
	title = "Tomato pasta with basil",
	cuisine = Cuisine.ITALIAN,
	imageUrl = null,
	totalTime = 25,
	measurementSystem = MeasurementSystem.METRIC,
)

private val previewRecipeB = RecipeSummary(
	id = 2,
	title = "Green salad with vinaigrette",
	cuisine = Cuisine.FRENCH,
	imageUrl = null,
	totalTime = 15,
)

private val previewRecipeC = RecipeSummary(
	id = 3,
	title = "Chicken tikka masala",
	cuisine = Cuisine.INDIAN,
	imageUrl = null,
	totalTime = 45,
	measurementSystem = MeasurementSystem.IMPERIAL,
)

private val previewRecipeD = RecipeSummary(
	id = 4,
	title = "Miso soup",
	cuisine = Cuisine.JAPANESE,
	imageUrl = null,
	totalTime = 12,
)

private val previewRecipeE = RecipeSummary(
	id = 5,
	title = "Apple crumble",
	cuisine = Cuisine.BRITISH,
	imageUrl = null,
	totalTime = 50,
	measurementSystem = MeasurementSystem.MIXED,
)

private val previewRecipeList = persistentListOf(
	previewRecipeA,
	previewRecipeB,
	previewRecipeC,
	previewRecipeD,
	previewRecipeE,
)

@Preview(
	name = "Search results loading",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchResultsContentLoadingPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 240.dp),
		) {
			SearchResultsContent(
				isSearching = true,
				errorMessage = null,
				totalMatches = 0,
				paginationState = rememberPreviewPaginationState(persistentListOf()),
				recipes = remember { mutableStateListOf() },
				onRecipeSelect = {},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}

@Preview(
	name = "Search results error",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchResultsContentErrorPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 200.dp),
		) {
			SearchResultsContent(
				isSearching = false,
				errorMessage = "Search failed",
				totalMatches = 0,
				paginationState = rememberPreviewPaginationState(persistentListOf()),
				recipes = remember { mutableStateListOf() },
				onRecipeSelect = {},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}

@Preview(
	name = "Search results list light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchResultsContentListLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 560.dp),
		) {
			SearchResultsContentListPreviewBody()
		}
	}
}

@Preview(
	name = "Search results list dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun SearchResultsContentListDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 560.dp),
		) {
			SearchResultsContentListPreviewBody()
		}
	}
}

@Composable
private fun SearchResultsContentListPreviewBody() {
	val recipes = remember {
		mutableStateListOf<RecipeSummary>().apply { addAll(previewRecipeList) }
	}
	val paginationState = rememberPreviewPaginationState(previewRecipeList)
	SearchResultsContent(
		isSearching = false,
		errorMessage = null,
		totalMatches = previewRecipeList.size,
		paginationState = paginationState,
		recipes = recipes,
		onRecipeSelect = {},
		modifier = Modifier.fillMaxSize(),
	)
}

@Composable
private fun rememberPreviewPaginationState(
	items: ImmutableList<RecipeSummary>,
): PaginationState<Int, RecipeSummary> {
	return remember(items) {
		PaginationState<Int, RecipeSummary>(
			initialPageKey = 1,
			onRequestPage = { },
		).also { state ->
			if (items.isNotEmpty()) {
				state.appendPage(1, items, nextPageKey = 2, isLastPage = true)
			}
		}
	}
}
