package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.atom.FixedSubtitleText
import app.purecipes.shared.ui.component.atom.FixedTitleText
import app.purecipes.shared.ui.component.atom.PurecipesTextButton
import app.purecipes.shared.ui.component.atom.purecipesPlaceholder
import app.purecipes.shared.ui.component.molecule.SectionHeader
import app.purecipes.shared.ui.component.organism.RecipeCardDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val HOME_FEED_SKELETON_TAG = "homeFeedSkeleton"

private const val HOME_SKELETON_SHELF_COUNT = 4
private const val HOME_SKELETON_CARDS_PER_SHELF = 3
private const val TITLE_PLACEHOLDER_WIDTH_FRACTION = 0.85f
private const val META_PLACEHOLDER_WIDTH_FRACTION = 0.4f
private const val FEATURE_REQUEST_LABEL_WIDTH_FRACTION = 0.35f
private const val FEATURE_REQUEST_VOTES_WIDTH_FRACTION = 0.25f
private const val SHELF_LABEL_PLACEHOLDER_TEXT = "Popular recipes"
private const val SEE_ALL_PLACEHOLDER_TEXT = "See all"
private const val SHELF_CARD_TITLE_PLACEHOLDER_TEXT = "Tomato pasta with basil and olive oil"
private const val SHELF_CARD_META_PLACEHOLDER_TEXT = "25 min"
private const val FEATURE_REQUEST_LABEL_PLACEHOLDER_TEXT = "Feature request"
private const val FEATURE_REQUEST_TITLE_PLACEHOLDER_TEXT = "Meal plan for the week"
private const val FEATURE_REQUEST_BODY_PLACEHOLDER_TEXT =
	"Tell us what would make Purecipes better and other cooks can vote."
private const val FEATURE_REQUEST_VOTES_PLACEHOLDER_TEXT = "12 votes"
private const val FEATURE_REQUEST_ACTION_PLACEHOLDER_TEXT = "Vote"

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
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
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
		val featureRequestRow = homeFeedFeatureRequestRow(HOME_SKELETON_SHELF_COUNT)
		items(
			count = HOME_SKELETON_SHELF_COUNT + 1,
			key = { row -> if (row == featureRequestRow) "feature-request" else "shelf-$row" },
		) { row ->
			if (row == featureRequestRow) {
				HomeFeatureRequestSkeleton(
					modifier = Modifier.padding(horizontal = PurecipesTheme.space.m),
				)
			} else {
				HomeShelfSkeleton()
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
				.padding(horizontal = PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			FixedTitleText(
				text = SHELF_LABEL_PLACEHOLDER_TEXT,
				color = PurecipesTheme.colorScheme.onSurface,
				maxLines = 1,
				isPlaceholder = true,
			)
			PurecipesTextButton(
				text = SEE_ALL_PLACEHOLDER_TEXT,
				onClick = {},
				isPlaceholder = true,
			)
		}
		Row(
			modifier = Modifier.padding(start = PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
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
				FixedTitleText(
					text = SHELF_CARD_TITLE_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(TITLE_PLACEHOLDER_WIDTH_FRACTION),
					color = PurecipesTheme.colorScheme.onSurface,
					style = PurecipesTheme.typography.titleSmall,
					minLines = RecipeCardDefaults.ShelfTitleLines,
					maxLines = RecipeCardDefaults.ShelfTitleLines,
					isPlaceholder = true,
				)
				FixedSubtitleText(
					text = SHELF_CARD_META_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(META_PLACEHOLDER_WIDTH_FRACTION),
					color = PurecipesTheme.colorScheme.onSurface,
					style = PurecipesTheme.typography.bodySmall,
					isPlaceholder = true,
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
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				FixedSubtitleText(
					text = FEATURE_REQUEST_LABEL_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(FEATURE_REQUEST_LABEL_WIDTH_FRACTION),
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					style = PurecipesTheme.typography.labelMedium,
					isPlaceholder = true,
				)
				SectionHeader(
					title = FEATURE_REQUEST_TITLE_PLACEHOLDER_TEXT,
					subtitle = FEATURE_REQUEST_BODY_PLACEHOLDER_TEXT,
					titleStyle = PurecipesTheme.typography.titleMedium,
					subtitleStyle = PurecipesTheme.typography.bodyMedium,
					isPlaceholder = true,
				)
				FixedSubtitleText(
					text = FEATURE_REQUEST_VOTES_PLACEHOLDER_TEXT,
					modifier = Modifier.fillMaxWidth(FEATURE_REQUEST_VOTES_WIDTH_FRACTION),
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					style = PurecipesTheme.typography.bodyMedium,
					isPlaceholder = true,
				)
			}
			PurecipesTextButton(
				text = FEATURE_REQUEST_ACTION_PLACEHOLDER_TEXT,
				onClick = {},
				isPlaceholder = true,
			)
		}
	}
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
