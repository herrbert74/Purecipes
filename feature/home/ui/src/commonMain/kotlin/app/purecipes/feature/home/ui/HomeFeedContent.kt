package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.shelfKey
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

internal const val HOME_FEED_LIST_TAG = "homeFeedList"
internal const val HOME_TITLE_TAG = "homeTitle"
internal const val HOME_FEATURE_REQUEST_AFTER_SHELF_COUNT = 4

internal fun homeFeedFeatureRequestRow(shelfCount: Int): Int =
	minOf(HOME_FEATURE_REQUEST_AFTER_SHELF_COUNT, shelfCount)

internal fun homeFeedShelfIndex(row: Int, shelfCount: Int): Int? {
	val featureRequestRow = homeFeedFeatureRequestRow(shelfCount)
	return when {
		row == featureRequestRow -> null
		row < featureRequestRow -> row
		else -> row - 1
	}
}

@Composable
internal fun HomeFeedContent(
	isLoading: Boolean,
	errorMessage: String?,
	homeFeed: HomeFeed,
	shelfPagination: ImmutableMap<String, PaginationState<Int, RecipeSummary>>,
	onRecipeSelect: (recipeId: Int, transitionKey: String?) -> Unit,
	onSeeAll: (HomeShelf) -> Unit,
	onFeatureRequestClick: () -> Unit,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
	listState: LazyListState = rememberLazyListState(),
	shelfListStates: ImmutableMap<String, LazyListState> = persistentMapOf(),
) {
	val contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.ime)
	val listContentPadding = contentWindowInsets.asPaddingValues() + PaddingValues(
		bottom = PurecipesTheme.space.m + PurecipesTheme.bottomBarPadding,
	)
	val listModifier = modifier
		.fillMaxSize()
		.consumeWindowInsets(contentWindowInsets)
	val insetContentModifier = modifier
		.consumeWindowInsets(contentWindowInsets)
		.padding(contentWindowInsets.asPaddingValues())
	when {
		isLoading && homeFeed.shelves.isEmpty() -> HomeFeedSkeleton(
			modifier = listModifier,
			contentPadding = listContentPadding,
		)

		errorMessage != null && homeFeed.shelves.isEmpty() -> EmptyStateContent(
			icon = AppIcons.Warning,
			iconContentDescription = "Error",
			title = errorMessage,
			description = "Check your connection, then try again.",
			modifier = insetContentModifier,
			action = {
				PurecipesButton(
					text = "Retry",
					onClick = onRetry,
				)
			},
		)

		else -> LazyColumn(
			state = listState,
			modifier = listModifier.testTag(HOME_FEED_LIST_TAG),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			contentPadding = listContentPadding,
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
			val shelves = homeFeed.shelves
			items(
				count = shelves.size + 1,
				key = { row -> homeFeedRowKey(row, shelves) },
			) { row ->
				val shelfIndex = homeFeedShelfIndex(row, shelves.size)
				if (shelfIndex == null) {
					HomeFeatureRequestCard(
						featureRequest = homeFeed.featureRequest,
						onOpen = onFeatureRequestClick,
						modifier = Modifier.padding(horizontal = PurecipesTheme.space.m),
					)
				} else {
					val shelf = shelves[shelfIndex]
					val shelfKey = shelf.shelfKey()
					val paginationState = shelfPagination[shelfKey]
					if (paginationState != null) {
						HomeShelfRow(
							shelf = shelf,
							shelfIndex = shelfIndex,
							paginationState = paginationState,
							onRecipeSelect = onRecipeSelect,
							onSeeAll = { onSeeAll(shelf) },
							bannerAdViewModel = bannerAdViewModel,
							listState = shelfListStates[shelfKey] ?: rememberLazyListState(),
						)
					}
				}
			}
		}
	}
}

private fun homeFeedRowKey(row: Int, shelves: List<HomeShelf>): Any {
	val shelfIndex = homeFeedShelfIndex(row, shelves.size) ?: return "feature-request"
	return shelves[shelfIndex].shelfKey()
}

@Preview(showBackground = true)
@Composable
private fun HomeFeedContentPreview() {
	PurecipesPreviewScaffold {
		val recipes = listOf(
			RecipeSummary(
				id = 1,
				title = "Tomato pasta",
				cuisine = null,
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
		HomeFeedContent(
			isLoading = false,
			errorMessage = null,
			homeFeed = HomeFeed(
				shelves = listOf(
					HomeShelf(
						id = HomeShelfId.NEW,
						title = "New",
						recipes = recipes,
					),
				),
				featureRequest = FeatureRequest(
					id = 4,
					title = "Save a weekly plan",
					description = "",
					status = FeatureRequestStatus.OPEN,
					voteCount = 3,
					commentCount = 0,
					createdAtEpochMillis = 0L,
					votedByCurrentUser = false,
				),
			),
			shelfPagination = mapOf(HomeShelfId.NEW.name to paginationState).toImmutableMap(),
			onRecipeSelect = { _, _ -> },
			onSeeAll = {},
			onFeatureRequestClick = {},
			onRetry = {},
		)
	}
}
