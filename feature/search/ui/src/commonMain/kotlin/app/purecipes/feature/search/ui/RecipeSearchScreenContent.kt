package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.purecipes.feature.search.ui.result.SearchResultsContent
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.molecule.browseTiles
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun RecipeSearchScreenContent(
	darkTheme: Boolean,
	isSearchExpanded: Boolean,
	searchQuery: String,
	hasActiveFilters: Boolean,
	isSearching: Boolean,
	errorMessage: String?,
	totalMatches: Int,
	recipes: ImmutableList<RecipeSummary>,
	modifier: Modifier = Modifier,
	searchFilterNote: String? = null,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Column(
			modifier = modifier
				.fillMaxSize()
				.windowInsetsPadding(TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Top)),
		) {
			Box(
				modifier = Modifier.padding(
					start = PurecipesTheme.space.m,
					top = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
				),
			) {
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
			}
			val recipeList = remember(recipes) {
				mutableStateListOf<RecipeSummary>().apply { addAll(recipes) }
			}
			val paginationState = remember(recipes) {
				PaginationState<Int, RecipeSummary>(
					initialPageKey = 1,
					onRequestPage = { },
				).apply {
					if (recipes.isNotEmpty()) {
						appendPage(1, recipes, nextPageKey = 2, isLastPage = true)
					}
				}
			}
			SearchResultsContent(
				isSearching = isSearching,
				errorMessage = errorMessage,
				totalMatches = totalMatches,
				paginationState = paginationState,
				recipes = recipeList,
				onRecipeSelect = { _, _ -> },
				searchInstructions = if (isSearchExpanded) RECIPE_SEARCH_HELPER else null,
				searchFilterNote = searchFilterNote,
				browseTiles = browseTiles(SearchFilters.default()),
				modifier = Modifier
					.weight(1f)
					.padding(top = PurecipesTheme.space.s),
			)
		}
	}
}
