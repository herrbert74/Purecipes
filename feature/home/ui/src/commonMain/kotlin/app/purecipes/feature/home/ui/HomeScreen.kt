package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit = {},
	onOpenSearch: (SearchFilters, HomeShelfId?) -> Unit = { _, _ -> },
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
	val homeListState = rememberLazyListState()
	HomeFeedContent(
		isLoading = viewModel.isHomeLoading,
		errorMessage = viewModel.homeErrorMessage,
		homeFeed = viewModel.homeFeed,
		shelfPagination = viewModel.shelfPagination,
		onRecipeSelect = onRecipeSelect,
		onSeeAll = { shelf ->
			onOpenSearch(shelf.filters, shelf.id)
		},
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
