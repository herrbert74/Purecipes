package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.RecipeCardDefaults
import app.purecipes.shared.ui.component.purecipesPlaceholder
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val HOME_FEED_SKELETON_TAG = "homeFeedSkeleton"

private const val HOME_SKELETON_SHELF_COUNT = 4
private const val HOME_SKELETON_CARDS_PER_SHELF = 3
private const val TITLE_PLACEHOLDER_WIDTH_FRACTION = 0.85f
private const val META_PLACEHOLDER_WIDTH_FRACTION = 0.4f
private const val FEATURE_REQUEST_LABEL_WIDTH_FRACTION = 0.35f
private const val FEATURE_REQUEST_TITLE_WIDTH_FRACTION = 0.7f
private const val FEATURE_REQUEST_VOTES_WIDTH_FRACTION = 0.25f
private val SHELF_LABEL_PLACEHOLDER_WIDTH = 128.dp
private val SEE_ALL_PLACEHOLDER_WIDTH = 64.dp
private val FEATURE_REQUEST_ACTION_WIDTH = 56.dp

@Composable
internal fun HomeFeedSkeleton(
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(bottom = PurecipesTheme.space.m),
) {
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.testTag(HOME_FEED_SKELETON_TAG),
		userScrollEnabled = false,
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.l),
		contentPadding = contentPadding,
	) {
		item(key = HOME_TITLE_TAG) {
			Text(
				text = "Purecipes",
				modifier = Modifier
					.padding(horizontal = PurecipesTheme.space.m)
					.padding(top = PurecipesTheme.space.m)
					.testTag(HOME_TITLE_TAG),
				style = PurecipesTheme.typography.headlineMedium,
				color = PurecipesTheme.colorScheme.onSurface,
			)
		}
		items(HOME_SKELETON_SHELF_COUNT) { index ->
			Column {
				HomeShelfSkeleton()
				if (index == 0) {
					HomeFeatureRequestSkeleton(
						modifier = Modifier
							.padding(horizontal = PurecipesTheme.space.m)
							.padding(top = PurecipesTheme.space.l),
					)
				}
			}
		}
	}
}

@Composable
private fun HomeShelfSkeleton() {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(min = ButtonDefaults.MinHeight)
				.padding(horizontal = PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Box(
				modifier = Modifier
					.width(SHELF_LABEL_PLACEHOLDER_WIDTH)
					.height(PurecipesTheme.typography.titleMedium.lineHeightDp())
					.purecipesPlaceholder(),
			)
			Box(
				modifier = Modifier
					.width(SEE_ALL_PLACEHOLDER_WIDTH)
					.height(PurecipesTheme.typography.labelLarge.lineHeightDp())
					.purecipesPlaceholder(),
			)
		}
		Row(
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			repeat(HOME_SKELETON_CARDS_PER_SHELF) {
				HomeShelfCardSkeleton()
			}
		}
	}
}

@Composable
private fun HomeShelfCardSkeleton() {
	Card(
		modifier = Modifier.width(RecipeCardDefaults.ShelfCardWidth),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surface,
		),
	) {
		val titleLineHeight = PurecipesTheme.typography.titleSmall.lineHeightDp()
		val metaLineHeight = PurecipesTheme.typography.bodySmall.lineHeightDp()
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(
						RecipeCardDefaults.ShelfImageAspectRatioWidth /
							RecipeCardDefaults.ShelfImageAspectRatioHeight,
					)
					.purecipesPlaceholder(shape = RoundedCornerShape(PurecipesTheme.space.m)),
			)
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
				Column {
					Box(
						modifier = Modifier
							.fillMaxWidth(TITLE_PLACEHOLDER_WIDTH_FRACTION)
							.height(titleLineHeight)
							.purecipesPlaceholder(),
					)
					Box(
						modifier = Modifier
							.fillMaxWidth(TITLE_PLACEHOLDER_WIDTH_FRACTION)
							.height(titleLineHeight)
							.purecipesPlaceholder(),
					)
				}
				Box(
					modifier = Modifier
						.fillMaxWidth(META_PLACEHOLDER_WIDTH_FRACTION)
						.height(metaLineHeight)
						.purecipesPlaceholder(),
				)
			}
		}
	}
}

@Composable
private fun HomeFeatureRequestSkeleton(
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
		),
	) {
		val labelLineHeight = PurecipesTheme.typography.labelMedium.lineHeightDp()
		val titleLineHeight = PurecipesTheme.typography.titleMedium.lineHeightDp()
		val bodyLineHeight = PurecipesTheme.typography.bodyMedium.lineHeightDp()
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				Box(
					modifier = Modifier
						.fillMaxWidth(FEATURE_REQUEST_LABEL_WIDTH_FRACTION)
						.height(labelLineHeight)
						.purecipesPlaceholder(),
				)
				Box(
					modifier = Modifier
						.fillMaxWidth(FEATURE_REQUEST_TITLE_WIDTH_FRACTION)
						.height(titleLineHeight)
						.purecipesPlaceholder(),
				)
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(bodyLineHeight)
						.purecipesPlaceholder(),
				)
				Box(
					modifier = Modifier
						.fillMaxWidth(FEATURE_REQUEST_VOTES_WIDTH_FRACTION)
						.height(bodyLineHeight)
						.purecipesPlaceholder(),
				)
			}
			Box(
				modifier = Modifier
					.width(FEATURE_REQUEST_ACTION_WIDTH)
					.heightIn(min = ButtonDefaults.MinHeight)
					.purecipesPlaceholder(),
			)
		}
	}
}

@Composable
private fun TextStyle.lineHeightDp(): Dp {
	return with(LocalDensity.current) { lineHeight.toDp() }
}

@Preview(name = "Home feed loading", device = Devices.PIXEL_4, showBackground = true)
@Composable
private fun HomeFeedSkeletonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			HomeFeedSkeleton()
		}
	}
}

@Preview(name = "Home feed loading dark", device = Devices.PIXEL_4, showBackground = true)
@Composable
private fun HomeFeedSkeletonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			HomeFeedSkeleton()
		}
	}
}
