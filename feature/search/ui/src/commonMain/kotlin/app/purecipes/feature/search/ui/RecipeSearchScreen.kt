package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.search.ui.filter.FilterBottomSheet
import app.purecipes.feature.search.ui.result.SearchResultsContent
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

@Composable
fun RecipeSearchScreen(
	modifier: Modifier = Modifier,
	initialShowFilterSheet: Boolean = false,
	isSignedIn: Boolean = true,
	onRecipeSelect: (Int) -> Unit = {},
	onRequestLogInForFilters: () -> Unit = {},
	onOpenPaywall: (String) -> Unit = {},
	closeScreen: () -> Unit = {},
	sessionKey: String? = null,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: RecipeSearchViewModel = assistedMetroViewModel<RecipeSearchViewModel, RecipeSearchViewModel.Factory> {
		create(
			initialShowFilterSheet = initialShowFilterSheet,
			sessionKey = sessionKey,
		)
	},
) {
	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}
	LaunchedEffect(Unit) {
		viewModel.onSearchContentVisible()
	}

	if (viewModel.isFilterSheetVisible) {
		val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
		FilterBottomSheet(
			filters = viewModel.activeFilters,
			isSignedIn = isSignedIn,
			pantryIngredients = viewModel.pantryIngredients.toImmutableSet(),
			excludedIngredients = viewModel.excludedIngredients.toImmutableSet(),
			customPantryIngredients = viewModel.customPantryIngredients.toImmutableSet(),
			keyIngredients = viewModel.keyIngredients.toImmutableSet(),
			ingredientMatchPreview = viewModel.ingredientMatchPreview,
			isIngredientMatchLoading = viewModel.isIngredientMatchLoading,
			sheetState = sheetState,
			onDismiss = viewModel::onFilterSheetDismiss,
			onFiltersChange = viewModel::onFiltersChange,
			onKeyIngredientsChange = viewModel::onKeyIngredientsChange,
			onIngredientSelectionChange = viewModel::onIngredientSelectionChange,
			onCustomIngredientToggle = viewModel::onCustomIngredientToggle,
			onRemoveCustomIngredient = viewModel::onRemoveCustomIngredient,
			onAddIngredientQueryChange = viewModel::onAddIngredientQueryChange,
			onAddIngredient = viewModel::onAddIngredient,
			onClearIngredientMatchPreview = viewModel::clearIngredientMatchPreview,
			onRequestLogIn = {
				viewModel.onFilterSheetDismiss()
				onRequestLogInForFilters()
			},
			isPremium = viewModel.isPremium,
			onOpenPaywall = { feature ->
				viewModel.onPremiumFeatureBlocked(feature)
				onOpenPaywall(feature)
			},
		)
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.windowInsetsPadding(TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Top))
			.padding(PurecipesTheme.space.m),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		if (!viewModel.isSearchBarActive) {
			viewModel.searchFilterNote?.let { note ->
				Text(
					text = note,
					modifier = Modifier.testTag(SEARCH_FILTER_NOTE_TAG),
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
		RecipeSearchHeader(
			isSearchBarActive = viewModel.isSearchBarActive,
			searchQuery = viewModel.searchQuery,
			hasActiveFilters = !viewModel.activeFilters.isEmpty || viewModel.keyIngredients.isNotEmpty(),
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
			nearMissRecipes = viewModel.nearMissRecipes.toImmutableList(),
			onRecipeSelect = onRecipeSelect,
			onRetryClick = viewModel::searchNow,
			bannerAdViewModel = bannerAdViewModel,
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

@Preview(
	name = "Recipe search screen collapsed light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeSearchScreenCollapsedLightPreview() {
	Surface(modifier = Modifier.fillMaxSize()) {
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
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
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "tom",
			hasActiveFilters = true,
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
		RecipeSearchScreenContent(
			darkTheme = true,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
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
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "x",
			hasActiveFilters = false,
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
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "broken",
			hasActiveFilters = false,
			isSearching = false,
			errorMessage = "Search failed",
			totalMatches = 0,
			recipes = persistentListOf(),
		)
	}
}
