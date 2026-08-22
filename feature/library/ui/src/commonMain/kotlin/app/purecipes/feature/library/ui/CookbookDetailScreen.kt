package app.purecipes.feature.library.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.library.ui.cookbooks.CookbookDetailContent
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun CookbookDetailScreen(
	cookbookId: Int,
	name: String,
	onBack: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: CookbookDetailViewModel = assistedMetroViewModel<
		CookbookDetailViewModel,
		CookbookDetailViewModel.Factory,
	>(
		key = "$cookbookId-$name",
	) {
		create(
			cookbookId = cookbookId,
			initialName = name,
			sessionKey = sessionKey,
		)
	},
) {
	val showBackNavigation = rememberShowCookbookDetailBackNavigation()

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}

	LaunchedEffect(cookbookId) {
		viewModel.loadCookbookCover()
	}

	CookbookDetailContent(
		title = viewModel.title,
		errorMessage = viewModel.errorMessage,
		recipes = viewModel.recipes,
		paginationState = viewModel.paginationState,
		totalMatches = viewModel.totalMatches,
		coverUrl = viewModel.coverUrl,
		onBack = onBack,
		onShare = viewModel::shareCookbook,
		onRecipeSelect = onRecipeSelect,
		onRemoveRecipe = viewModel::removeRecipe,
		showBackNavigation = showBackNavigation,
		modifier = modifier,
		bannerAdViewModel = bannerAdViewModel,
	)
}
