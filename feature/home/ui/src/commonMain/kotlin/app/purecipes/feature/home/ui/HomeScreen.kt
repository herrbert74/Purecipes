package app.purecipes.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
	val statusBarColor = PurecipesTheme.colorScheme.surface
	Box(modifier = modifier.fillMaxSize()) {
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
			modifier = Modifier.fillMaxSize(),
		)
		Spacer(
			modifier = Modifier
				.fillMaxWidth()
				.height(
					with(LocalDensity.current) {
						(WindowInsets.statusBars.getTop(this) * STATUS_BAR_SCRIM_HEIGHT_FACTOR).toDp()
					},
				)
				.background(
					brush = Brush.verticalGradient(
						colors = listOf(
							statusBarColor,
							statusBarColor.copy(alpha = STATUS_BAR_SCRIM_MID_ALPHA),
							Color.Transparent,
						),
					),
				),
		)
	}
}

private const val STATUS_BAR_SCRIM_HEIGHT_FACTOR = 1.2f
private const val STATUS_BAR_SCRIM_MID_ALPHA = 0.8f
