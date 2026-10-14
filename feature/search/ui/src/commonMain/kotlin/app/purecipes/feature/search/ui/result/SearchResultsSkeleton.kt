package app.purecipes.feature.search.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.BrowseTileGrid
import app.purecipes.shared.ui.component.BrowseTileItem
import app.purecipes.shared.ui.component.RecipeCardSkeleton
import app.purecipes.shared.ui.component.browseTiles
import app.purecipes.shared.ui.component.paging.AdaptiveGridDefaults
import app.purecipes.shared.ui.component.purecipesPlaceholder
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

private const val SEARCH_SKELETON_CARD_COUNT = 6
private const val COUNT_PLACEHOLDER_WIDTH_FRACTION = 0.45f

@Composable
internal fun SearchResultsSkeleton(
	browseTiles: ImmutableList<BrowseTileItem>,
	onBrowseTileClick: (BrowseTileItem) -> Unit,
	modifier: Modifier = Modifier,
) {
	val countLineHeight = PurecipesTheme.typography.labelMedium.lineHeightDp()
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
			Box(
				modifier = Modifier
					.fillMaxWidth(COUNT_PLACEHOLDER_WIDTH_FRACTION)
					.height(countLineHeight)
					.purecipesPlaceholder(),
			)
		}
		items(SEARCH_SKELETON_CARD_COUNT) { index ->
			RecipeCardSkeleton(tintIndex = index)
		}
	}
}

@Composable
private fun TextStyle.lineHeightDp(): Dp {
	return with(LocalDensity.current) { lineHeight.toDp() }
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
