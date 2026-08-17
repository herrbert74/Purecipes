package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.feature.search.ui.result.SearchResultsContent
import app.purecipes.shared.domain.model.RecipeSummary
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
				.windowInsetsPadding(TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Top))
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			if (!isSearchExpanded) {
				searchFilterNote?.let { note ->
					Text(
						text = note,
						modifier = Modifier.testTag(SEARCH_FILTER_NOTE_TAG),
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
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
				onRecipeSelect = {},
				modifier = Modifier.weight(1f),
			)
		}
	}
}
