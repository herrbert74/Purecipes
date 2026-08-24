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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.InlineListBannerAdSlot
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.BrowseTileGrid
import app.purecipes.shared.ui.component.BrowseTileItem
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.paging.PaginatedLazyVerticalGrid
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.component.staggeredAppear
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal const val SEARCH_RESULTS_LIST_TAG = "searchResultsList"

@Composable
internal fun SearchResultsContent(
	isSearching: Boolean,
	errorMessage: String?,
	totalMatches: Int,
	paginationState: PaginationState<Int, RecipeSummary>,
	recipes: SnapshotStateList<RecipeSummary>,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	nearMissRecipes: ImmutableList<NearMissRecipe> = persistentListOf(),
	browseTiles: ImmutableList<BrowseTileItem> = persistentListOf(),
	onBrowseTileClick: (BrowseTileItem) -> Unit = {},
	onRetryClick: () -> Unit = {},
	bannerAdViewModel: BannerAdViewModel? = null,
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
				PurecipesButton(
					text = "Retry",
					onClick = onRetryClick,
				)
			}
		}

		else -> PaginatedLazyVerticalGrid(
			paginationState = paginationState,
			requestInitialPageAutomatically = false,
			modifier = modifier.fillMaxWidth(),
			lazyModifier = Modifier.testTag(SEARCH_RESULTS_LIST_TAG),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			contentPadding = PaddingValues(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				bottom = PurecipesTheme.space.m,
			),
		) {
			if (browseTiles.isNotEmpty()) {
				item(span = { GridItemSpan(maxLineSpan) }) {
					BrowseTileGrid(
						tiles = browseTiles,
						onTileClick = onBrowseTileClick,
					)
				}
			}
			item(span = { GridItemSpan(maxLineSpan) }) {
				Text(
					text = "$totalMatches recipes found",
					style = PurecipesTheme.typography.labelMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			items(recipes.size, key = { recipes[it].id }) { index ->
				val recipe = recipes[index]
				InlineListBannerAdSlot(
					contentIndex = index,
					contentCount = recipes.size,
					viewModel = bannerAdViewModel,
				) {
					RecipeCard(
						recipe = recipe,
						onClick = { onRecipeSelect(recipe.id) },
						modifier = Modifier.staggeredAppear(index = index),
					)
				}
			}
			if (nearMissRecipes.isNotEmpty()) {
				item(span = { GridItemSpan(maxLineSpan) }) {
					NearMissSearchResults(
						nearMissRecipes = nearMissRecipes,
						onRecipeSelect = onRecipeSelect,
					)
				}
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
	isFavorite = true,
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

private val previewNearMissRecipes = persistentListOf(
	NearMissRecipe(
		recipe = previewRecipeA,
		missingIngredient = "Basil",
	),
	NearMissRecipe(
		recipe = previewRecipeC,
		missingIngredient = "Basil",
	),
	NearMissRecipe(
		recipe = previewRecipeB,
		missingIngredient = "Balsamic vinegar",
	),
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
	name = "Search results empty with near misses",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchResultsContentNearMissPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 480.dp),
		) {
			SearchResultsContent(
				isSearching = false,
				errorMessage = null,
				totalMatches = 0,
				paginationState = rememberPreviewPaginationState(persistentListOf()),
				recipes = remember { mutableStateListOf() },
				nearMissRecipes = previewNearMissRecipes,
				onRecipeSelect = {},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}

@Preview(
	name = "Search results sparse with near misses",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchResultsContentSparseWithNearMissPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = 560.dp),
		) {
			val recipes = remember {
				mutableStateListOf(previewRecipeA)
			}
			SearchResultsContent(
				isSearching = false,
				errorMessage = null,
				totalMatches = 1,
				paginationState = rememberPreviewPaginationState(persistentListOf(previewRecipeA)),
				recipes = recipes,
				nearMissRecipes = previewNearMissRecipes,
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
