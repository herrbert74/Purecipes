package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val BROWSE_TILE_TAG_PREFIX = "browseTile:"

private const val BROWSE_TILE_ASPECT_RATIO = 1f
private val BROWSE_TILE_WIDTH = 88.dp
private val BROWSE_TILE_ILLUSTRATION_SIZE = 28.sp

@Immutable
data class BrowseTileItem(
	val id: String,
	val title: String,
	val illustration: String,
	val tint: ContainerTint,
	val selected: Boolean = false,
)

@Composable
fun BrowseTile(
	item: BrowseTileItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val colors = item.tint.colorFamily()
	Surface(
		onClick = onClick,
		modifier = modifier
			.width(BROWSE_TILE_WIDTH)
			.aspectRatio(BROWSE_TILE_ASPECT_RATIO)
			.testTag("$BROWSE_TILE_TAG_PREFIX${item.id}"),
		shape = RoundedCornerShape(PurecipesTheme.space.m),
		color = if (item.selected) {
			colors.color
		} else {
			colors.colorContainer
		},
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.s),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(
				space = PurecipesTheme.space.xs,
				alignment = Alignment.CenterVertically,
			),
		) {
			Text(
				text = item.illustration,
				fontSize = BROWSE_TILE_ILLUSTRATION_SIZE,
				textAlign = TextAlign.Center,
			)
			Text(
				text = item.title,
				style = PurecipesTheme.typography.labelLarge,
				color = if (item.selected) {
					colors.onColor
				} else {
					colors.onColorContainer
				},
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Composable
fun BrowseTileGrid(
	tiles: ImmutableList<BrowseTileItem>,
	onTileClick: (BrowseTileItem) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Text(
			text = "Browse",
			style = PurecipesTheme.typography.titleMedium,
		)
		LazyRow(
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			items(
				count = tiles.size,
				key = { index -> tiles[index].id },
			) { index ->
				val tile = tiles[index]
				BrowseTile(
					item = tile,
					onClick = { onTileClick(tile) },
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun BrowseTileGridPreview() {
	PurecipesPreviewScaffold {
		BrowseTileGrid(
			tiles = persistentListOf(
				BrowseTileItem(
					id = "breakfast",
					title = "Breakfast",
					illustration = "🍳",
					tint = ContainerTint.Primary,
				),
				BrowseTileItem(
					id = "lunch",
					title = "Lunch",
					illustration = "🥗",
					tint = ContainerTint.Secondary,
					selected = true,
				),
				BrowseTileItem(
					id = "dinner",
					title = "Dinner",
					illustration = "🍝",
					tint = ContainerTint.Tertiary,
				),
				BrowseTileItem(
					id = "dessert",
					title = "Dessert",
					illustration = "🍰",
					tint = ContainerTint.Primary,
				),
			),
			onTileClick = {},
		)
	}
}
