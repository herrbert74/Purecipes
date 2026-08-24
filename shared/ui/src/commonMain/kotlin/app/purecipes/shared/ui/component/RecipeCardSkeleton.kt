package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.paging.AdaptiveGridDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

private const val RECIPE_CARD_ASPECT_RATIO_WIDTH = 16f
private const val RECIPE_CARD_ASPECT_RATIO_HEIGHT = 9f
private const val DEFAULT_SKELETON_COUNT = 6
private const val TITLE_PLACEHOLDER_WIDTH_FRACTION = 0.75f
private const val SUBTITLE_PLACEHOLDER_WIDTH_FRACTION = 0.4f
private val CHIP_PLACEHOLDER_PRIMARY_WIDTH = 72.dp
private val CHIP_PLACEHOLDER_SECONDARY_WIDTH = 56.dp

@Composable
fun RecipeCardSkeleton(
	modifier: Modifier = Modifier,
	widthFraction: Float = RecipeCardDefaults.FullWidthFraction,
	tintIndex: Int = 0,
) {
	val colors = ContainerTint.forIndex(tintIndex).colorFamily()
	Card(
		modifier = modifier.fillMaxWidth(widthFraction),
		colors = CardDefaults.cardColors(containerColor = colors.colorContainer),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
				Box(
					modifier = Modifier
						.width(CHIP_PLACEHOLDER_PRIMARY_WIDTH)
						.height(PurecipesTheme.space.m)
						.purecipesPlaceholder(shape = RoundedCornerShape(PurecipesTheme.space.l)),
				)
				Box(
					modifier = Modifier
						.width(CHIP_PLACEHOLDER_SECONDARY_WIDTH)
						.height(PurecipesTheme.space.m)
						.purecipesPlaceholder(shape = RoundedCornerShape(PurecipesTheme.space.l)),
				)
			}
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(RECIPE_CARD_ASPECT_RATIO_WIDTH / RECIPE_CARD_ASPECT_RATIO_HEIGHT)
					.purecipesPlaceholder(shape = RoundedCornerShape(PurecipesTheme.space.m)),
			)
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
				Box(
					modifier = Modifier
						.fillMaxWidth(TITLE_PLACEHOLDER_WIDTH_FRACTION)
						.height(PurecipesTheme.space.m)
						.purecipesPlaceholder(),
				)
				Box(
					modifier = Modifier
						.fillMaxWidth(SUBTITLE_PLACEHOLDER_WIDTH_FRACTION)
						.height(PurecipesTheme.space.s)
						.purecipesPlaceholder(),
				)
			}
		}
	}
}

@Composable
fun RecipeCardSkeletonGrid(
	modifier: Modifier = Modifier,
	itemCount: Int = DEFAULT_SKELETON_COUNT,
) {
	LazyVerticalGrid(
		columns = GridCells.Adaptive(AdaptiveGridDefaults.MinItemWidth),
		modifier = modifier.fillMaxSize(),
		contentPadding = PaddingValues(PurecipesTheme.space.m),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		userScrollEnabled = false,
	) {
		items(itemCount) { index ->
			RecipeCardSkeleton(tintIndex = index)
		}
	}
}

@Preview
@Composable
private fun RecipeCardSkeletonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			RecipeCardSkeleton()
		}
	}
}

@Preview
@Composable
private fun RecipeCardSkeletonGridLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			RecipeCardSkeletonGrid()
		}
	}
}

@Preview
@Composable
private fun RecipeCardSkeletonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			RecipeCardSkeleton(tintIndex = 1)
		}
	}
}
