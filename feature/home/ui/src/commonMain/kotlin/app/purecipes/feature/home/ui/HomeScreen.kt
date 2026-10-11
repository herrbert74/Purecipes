package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit = {},
	onOpenShelf: (HomeShelf) -> Unit = {},
	onOpenFeatureRequests: () -> Unit = {},
	sessionKey: String? = null,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: HomeViewModel = assistedMetroViewModel<HomeViewModel, HomeViewModel.Factory> {
		create(sessionKey = sessionKey)
	},
) {
	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}
	val homeListState = remember(viewModel) {
		LazyListState(
			firstVisibleItemIndex = viewModel.homeListFirstVisibleItemIndex,
			firstVisibleItemScrollOffset = viewModel.homeListFirstVisibleItemScrollOffset,
		)
	}
	LaunchedEffect(homeListState) {
		snapshotFlow {
			homeListState.firstVisibleItemIndex to homeListState.firstVisibleItemScrollOffset
		}.collect { (index, offset) ->
			viewModel.onHomeListScrolled(index, offset)
		}
	}
	HomeFeedContent(
		isLoading = viewModel.isHomeLoading,
		errorMessage = viewModel.homeErrorMessage,
		homeFeed = viewModel.homeFeed,
		shelfPagination = viewModel.shelfPagination,
		onRecipeSelect = onRecipeSelect,
		onSeeAll = onOpenShelf,
		onFeatureRequestClick = onOpenFeatureRequests,
		onRetry = viewModel.reloadHomeFeed,
		bannerAdViewModel = bannerAdViewModel,
		listState = homeListState,
		modifier = modifier
			.fillMaxSize()
			.windowInsetsPadding(TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Top))
			.padding(top = PurecipesTheme.space.m),
	)
}
