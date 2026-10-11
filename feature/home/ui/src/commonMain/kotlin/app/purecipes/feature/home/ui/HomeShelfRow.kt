package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.ads.domain.InlineAdPlacement
import app.purecipes.feature.ads.domain.InlineFeedEntry
import app.purecipes.feature.ads.domain.inlineFeedEntries
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.ShelfCardBannerAd
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.ContainerTint
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.RecipeCardDefaults
import app.purecipes.shared.ui.component.colorFamily
import app.purecipes.shared.ui.component.paging.PaginatedLazyRow
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val HOME_SHELF_SEE_ALL_TAG_PREFIX = "homeShelfSeeAll:"

private fun shelfEntryKey(entry: InlineFeedEntry<RecipeSummary>, index: Int): Any {
	return when (entry) {
		is InlineFeedEntry.Content -> entry.item.id
		InlineFeedEntry.Ad -> "ad-$index"
	}
}

@Composable
internal fun HomeShelfRow(
	shelf: HomeShelf,
	shelfIndex: Int,
	paginationState: PaginationState<Int, RecipeSummary>,
	onRecipeSelect: (Int) -> Unit,
	onSeeAll: () -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	val labelColor = ContainerTint.forIndex(shelfIndex).colorFamily().color
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Text(
				text = shelf.title,
				style = PurecipesTheme.typography.titleMedium,
				color = labelColor,
			)
			TextButton(
				onClick = onSeeAll,
				modifier = Modifier.testTag("$HOME_SHELF_SEE_ALL_TAG_PREFIX${shelf.id.name}"),
			) {
				Text(text = "See all")
			}
		}
		PaginatedLazyRow(
			paginationState = paginationState,
			requestInitialPageAutomatically = false,
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.Top,
			contentPadding = PaddingValues(
				horizontal = PurecipesTheme.space.m,
			),
			newPageProgressIndicator = {
				CircularProgressIndicator(
					modifier = Modifier.padding(horizontal = PurecipesTheme.space.s),
				)
			},
			newPageErrorIndicator = { _ ->
				TextButton(onClick = paginationState::retryLastFailedRequest) {
					Text(text = "Retry")
				}
			},
		) {
			val entries = inlineFeedEntries(
				items = paginationState.allItems,
				includeAds = bannerAdViewModel?.shouldShowAds == true,
				shouldInsertAd = InlineAdPlacement::shouldInsertShelfAdBeforeContentIndex,
			)
			items(
				count = entries.size,
				key = { index -> shelfEntryKey(entries[index], index) },
			) { index ->
				when (val entry = entries[index]) {
					is InlineFeedEntry.Content -> RecipeCard(
						recipe = entry.item,
						onClick = { onRecipeSelect(entry.item.id) },
						layout = RecipeCardDefaults.Layout.SHELF,
					)

					InlineFeedEntry.Ad -> ShelfCardBannerAd(viewModel = bannerAdViewModel)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun HomeShelfRowPreview() {
	PurecipesPreviewScaffold {
		val recipes = listOf(
			RecipeSummary(
				id = 1,
				title = "Tomato pasta with basil",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 20,
			),
		)
		val paginationState = remember {
			PaginationState<Int, RecipeSummary>(initialPageKey = 1) {}.apply {
				appendPage(
					pageKey = 1,
					items = recipes,
					nextPageKey = 2,
					isLastPage = true,
				)
			}
		}
		HomeShelfRow(
			shelf = HomeShelf(
				id = HomeShelfId.NEW,
				title = "New",
				recipes = recipes,
			),
			shelfIndex = 0,
			paginationState = paginationState,
			onRecipeSelect = {},
			onSeeAll = {},
		)
	}
}
