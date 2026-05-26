package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.search.ui.filter.FilterBottomSheet
import app.purecipes.feature.search.ui.result.SearchResultsContent
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableSet

@Composable
fun RecipeSearchScreen(
	modifier: Modifier = Modifier,
	initialShowFilterSheet: Boolean = false,
	isSignedIn: Boolean = true,
	onRecipeSelect: (Int) -> Unit = {},
	onRequestLogInForFilters: () -> Unit = {},
	closeScreen: () -> Unit = {},
	sessionKey: String? = null,
	viewModel: RecipeSearchViewModel = assistedMetroViewModel<RecipeSearchViewModel, RecipeSearchViewModel.Factory> {
		create(
			initialShowFilterSheet = initialShowFilterSheet,
			sessionKey = sessionKey,
		)
	},
) {
	if (viewModel.isFilterSheetVisible) {
		val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
		FilterBottomSheet(
			filters = viewModel.activeFilters,
			isSignedIn = isSignedIn,
			pantryIngredients = viewModel.pantryIngredients.toImmutableSet(),
			sheetState = sheetState,
			onDismiss = viewModel::onFilterSheetDismiss,
			onFiltersChange = viewModel::onFiltersChange,
			onPantryIngredientsChange = viewModel::onPantryIngredientsChange,
			onRequestLogIn = {
				viewModel.onFilterSheetDismiss()
				onRequestLogInForFilters()
			},
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
		RecipeSearchHeader(
			isSearchBarActive = viewModel.isSearchBarActive,
			searchQuery = viewModel.searchQuery,
			hasActiveFilters = !viewModel.activeFilters.isEmpty,
			onFilterClick = viewModel::onFilterButtonClick,
			onExpandSearch = { viewModel.onSearchBarExpandedChange(true) },
			onCloseSearch = {
				viewModel.onSearchBarExpandedChange(false)
				closeScreen()
			},
			onSearchQueryChange = viewModel::onSearchQueryChange,
			onSearchImeSearch = viewModel::searchNow,
			onClearSearchText = {
				viewModel.onSearchQueryChange("")
				viewModel.searchNow()
			},
		)
		if (viewModel.isSearchBarActive) {
			Text(
				text = RECIPE_SEARCH_HELPER,
				style = PurecipesTheme.typography.labelMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
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

private val screenPreviewRecipeA = RecipeSummary(
	id = 1,
	title = "Tomato Pasta",
	cuisine = Cuisine.ITALIAN,
	imageUrl = null,
	totalTime = 20,
)

private val screenPreviewRecipeB = RecipeSummary(
	id = 2,
	title = "Green Salad",
	cuisine = Cuisine.FRENCH,
	imageUrl = null,
	totalTime = 15,
)

@Composable
private fun RecipeSearchScreenPreviewContent(
	darkTheme: Boolean,
	isSearchExpanded: Boolean,
	searchQuery: String,
	hasActiveFilters: Boolean,
	measurementLabel: String?,
	isSearching: Boolean,
	errorMessage: String?,
	totalMatches: Int,
	recipes: ImmutableList<RecipeSummary>,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			measurementLabel?.let { label ->
				Text(
					text = label,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			RecipeSearchHeader(
				isSearchBarActive = isSearchExpanded,
				searchQuery = searchQuery,
				hasActiveFilters = hasActiveFilters,
				onFilterClick = {},
				onExpandSearch = {},
				onCloseSearch = {},
				onSearchQueryChange = {},
				onSearchImeSearch = {},
				onClearSearchText = {},
			)
			if (isSearchExpanded) {
				Text(
					text = RECIPE_SEARCH_HELPER,
					style = PurecipesTheme.typography.labelMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			val recipeList = remember { mutableStateListOf<RecipeSummary>() }
			LaunchedEffect(recipes) {
				recipeList.clear()
				recipeList.addAll(recipes)
			}
			val paginationState = remember {
				PaginationState<Int, RecipeSummary>(
					initialPageKey = 1,
					onRequestPage = { },
				)
			}
			LaunchedEffect(recipes) {
				paginationState.refresh(initialPageKey = 1)
				if (recipes.isNotEmpty()) {
					paginationState.appendPage(1, recipes, nextPageKey = 2, isLastPage = true)
				}
			}
			SearchResultsContent(
				isSearching = isSearching,
				errorMessage = errorMessage,
				totalMatches = totalMatches,
				paginationState = paginationState,
				recipes = recipeList,
				onRecipeSelect = {},
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Preview(
	name = "Recipe search screen collapsed light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchScreenCollapsedLightPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenPreviewContent(
			darkTheme = false,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
			measurementLabel = null,
			isSearching = false,
			errorMessage = null,
			totalMatches = 2,
			recipes = persistentListOf(screenPreviewRecipeA, screenPreviewRecipeB),
		)
	}
}

@Preview(
	name = "Recipe search screen expanded light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchScreenExpandedLightPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenPreviewContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "tom",
			hasActiveFilters = true,
			measurementLabel = "Showing metric recipes only",
			isSearching = false,
			errorMessage = null,
			totalMatches = 2,
			recipes = persistentListOf(screenPreviewRecipeA, screenPreviewRecipeB),
		)
	}
}

@Preview(
	name = "Recipe search screen dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeSearchScreenDarkPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenPreviewContent(
			darkTheme = true,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
			measurementLabel = null,
			isSearching = false,
			errorMessage = null,
			totalMatches = 1,
			recipes = persistentListOf(screenPreviewRecipeA),
		)
	}
}

@Preview(
	name = "Recipe search screen loading",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchScreenLoadingPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenPreviewContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "x",
			hasActiveFilters = false,
			measurementLabel = null,
			isSearching = true,
			errorMessage = null,
			totalMatches = 0,
			recipes = persistentListOf(),
		)
	}
}

@Preview(
	name = "Recipe search screen error",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchScreenErrorPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenPreviewContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "broken",
			hasActiveFilters = false,
			measurementLabel = null,
			isSearching = false,
			errorMessage = "Search failed",
			totalMatches = 0,
			recipes = persistentListOf(),
		)
	}
}
