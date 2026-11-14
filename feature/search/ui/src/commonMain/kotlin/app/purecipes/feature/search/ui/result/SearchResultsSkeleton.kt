package app.purecipes.feature.search.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.atom.FixedSubtitleText
import app.purecipes.shared.ui.component.molecule.BrowseTileGrid
import app.purecipes.shared.ui.component.molecule.BrowseTileItem
import app.purecipes.shared.ui.component.molecule.browseTiles
import app.purecipes.shared.ui.component.organism.RecipeCardSkeleton
import app.purecipes.shared.ui.component.paging.AdaptiveGridDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

private const val SEARCH_SKELETON_CARD_COUNT = 6
private const val COUNT_PLACEHOLDER_WIDTH_FRACTION = 0.45f
private const val COUNT_PLACEHOLDER_TEXT = "12 recipes found"

@Composable
internal fun SearchResultsSkeleton(
	browseTiles: ImmutableList<BrowseTileItem>,
	onBrowseTileClick: (BrowseTileItem) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyVerticalGrid(
		columns = GridCells.Adaptive(AdaptiveGridDefaults.MinItemWidth),
		modifier = modifier.fillMaxSize(),
		userScrollEnabled = false,
		contentPadding = PaddingValues(
			start = PurecipesTheme.space.m,
			end = PurecipesTheme.space.m,
			bottom = PurecipesTheme.space.m + PurecipesTheme.bottomBarPadding,
		),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
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
			FixedSubtitleText(
				text = COUNT_PLACEHOLDER_TEXT,
				modifier = Modifier.fillMaxWidth(COUNT_PLACEHOLDER_WIDTH_FRACTION),
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				style = PurecipesTheme.typography.labelMedium,
				isPlaceholder = true,
			)
		}
		items(SEARCH_SKELETON_CARD_COUNT) { index ->
			RecipeCardSkeleton(tintIndex = index)
		}
	}
}

@Preview(name = "Search results loading", device = Devices.PIXEL_4, showBackground = true)
@Composable
private fun SearchResultsSkeletonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			SearchResultsSkeleton(
				browseTiles = browseTiles(SearchFilters.default()),
				onBrowseTileClick = {},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}

@Preview(name = "Search results loading dark", device = Devices.PIXEL_4, showBackground = true)
@Composable
private fun SearchResultsSkeletonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			SearchResultsSkeleton(
				browseTiles = browseTiles(SearchFilters.default()),
				onBrowseTileClick = {},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}
