package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.InlineListBannerAdSlot
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.paging.PaginatedLazyVerticalGrid
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeShelfContent(
	title: String,
	isLoading: Boolean,
	errorMessage: String?,
	recipes: SnapshotStateList<RecipeSummary>,
	paginationState: PaginationState<Int, RecipeSummary>,
	onBack: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
		rememberTopAppBarState(),
	)
	Scaffold(
		modifier = modifier
			.fillMaxSize()
			.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
		topBar = {
			TopAppBar(
				scrollBehavior = topAppBarScrollBehavior,
				title = {
					Text(
						text = title,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
				},
				navigationIcon = {
					BackNavigationButton(onBack = onBack)
				},
			)
		},
	) { innerPadding ->
		when {
			errorMessage != null -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(text = errorMessage, textAlign = TextAlign.Center)
			}

			isLoading && recipes.isEmpty() -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			else -> PaginatedLazyVerticalGrid(
				paginationState = paginationState,
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				requestInitialPageAutomatically = false,
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				contentPadding = PaddingValues(
					start = PurecipesTheme.space.m,
					top = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					bottom = PurecipesTheme.space.m + PurecipesTheme.bottomBarPadding,
				),
				firstPageEmptyIndicator = {
					Box(
						modifier = Modifier
							.fillMaxSize()
							.padding(PurecipesTheme.space.l),
						contentAlignment = Alignment.Center,
					) {
						Text(
							text = "No recipes",
							style = PurecipesTheme.typography.bodyMedium,
							color = PurecipesTheme.colorScheme.onSurfaceVariant,
							textAlign = TextAlign.Center,
						)
					}
				},
			) {
				items(
					count = recipes.size,
					key = { index -> recipes[index].id },
				) { index ->
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
}

@Preview(name = "Home shelf", device = Devices.PIXEL_4, showBackground = true)
@Composable
private fun HomeShelfContentPreview() {
	val recipes = remember {
		mutableStateListOf(
			RecipeSummary(
				id = 1,
				title = "Sunday roast",
				cuisine = Cuisine.BRITISH,
				imageUrl = null,
				totalTime = 90,
			),
		)
	}
	val paginationState = remember {
		PaginationState<Int, RecipeSummary>(
			initialPageKey = 1,
			onRequestPage = {},
		).also { state ->
			state.appendPage(
				pageKey = 1,
				items = recipes,
				nextPageKey = 2,
				isLastPage = true,
			)
		}
	}
	PurecipesTheme {
		HomeShelfContent(
			title = "This week",
			isLoading = false,
			errorMessage = null,
			recipes = recipes,
			paginationState = paginationState,
			onBack = {},
			onRecipeSelect = {},
		)
	}
}
