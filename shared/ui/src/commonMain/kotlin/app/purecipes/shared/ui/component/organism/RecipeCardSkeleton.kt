package app.purecipes.shared.ui.component.organism

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.atom.ContainerTint
import app.purecipes.shared.ui.component.atom.FixedSubtitleText
import app.purecipes.shared.ui.component.atom.FixedTitleText
import app.purecipes.shared.ui.component.atom.MetadataPillChip
import app.purecipes.shared.ui.component.atom.colorFamily
import app.purecipes.shared.ui.component.atom.purecipesPlaceholder
import app.purecipes.shared.ui.component.paging.AdaptiveGridDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

private const val DEFAULT_SKELETON_COUNT = 6
private const val TITLE_PLACEHOLDER_WIDTH_FRACTION = 0.75f
private const val SUBTITLE_PLACEHOLDER_WIDTH_FRACTION = 0.4f
private const val PRIMARY_CHIP_PLACEHOLDER_TEXT = "Italian"
private const val SECONDARY_CHIP_PLACEHOLDER_TEXT = "Metric"
private const val TITLE_PLACEHOLDER_TEXT = "Recipe title placeholder"
private const val SUBTITLE_PLACEHOLDER_TEXT = "25 min"

@Composable
fun RecipeCardSkeleton(
	modifier: Modifier = Modifier,
	widthFraction: Float = RecipeCardDefaults.FullWidthFraction,
	tintIndex: Int = 0,
) {
	val tint = ContainerTint.forIndex(tintIndex)
	val colors = tint.colorFamily()
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
				MetadataPillChip(
					text = PRIMARY_CHIP_PLACEHOLDER_TEXT,
					tint = tint,
					filled = true,
					isPlaceholder = true,
				)
				MetadataPillChip(
					text = SECONDARY_CHIP_PLACEHOLDER_TEXT,
					tint = ContainerTint.Secondary,
					filled = true,
					isPlaceholder = true,
				)
			}
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(
						RecipeCardDefaults.GridImageAspectRatioWidth /
							RecipeCardDefaults.GridImageAspectRatioHeight,
					)
					.purecipesPlaceholder(shape = RoundedCornerShape(PurecipesTheme.space.m)),
			)
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
				FixedTitleText(
					text = TITLE_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(TITLE_PLACEHOLDER_WIDTH_FRACTION),
					color = colors.onColorContainer,
					maxLines = 1,
					isPlaceholder = true,
				)
				FixedSubtitleText(
					text = SUBTITLE_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(SUBTITLE_PLACEHOLDER_WIDTH_FRACTION),
					color = colors.onColorContainer,
					isPlaceholder = true,
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
