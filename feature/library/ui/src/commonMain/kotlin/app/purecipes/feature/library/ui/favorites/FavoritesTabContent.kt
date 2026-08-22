package app.purecipes.feature.library.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.InlineListBannerAdSlot
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.paging.PaginatedLazyVerticalGrid
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun FavoritesTabContent(
	errorMessage: String?,
	paginationState: PaginationState<Int, RecipeSummary>,
	recipes: SnapshotStateList<RecipeSummary>,
	totalMatches: Int,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	when {
		errorMessage != null -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.l),
			contentAlignment = Alignment.Center,
		) {
			ErrorText(text = errorMessage, textAlign = TextAlign.Center)
		}

		recipes.isEmpty() && totalMatches == 0 -> EmptyStateContent(
			icon = Icons.Filled.Favorite,
			iconContentDescription = "Favorites",
			title = "No favorites yet",
			description = "Add recipes from the details screen and they will appear here.",
			modifier = modifier,
		)

		else -> PaginatedLazyVerticalGrid(
			paginationState = paginationState,
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			contentPadding = PaddingValues(PurecipesTheme.space.m),
		) {
			item(span = { GridItemSpan(maxLineSpan) }) {
				Text(
					text = "$totalMatches saved recipes",
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
					)
				}
			}
		}
	}
}
