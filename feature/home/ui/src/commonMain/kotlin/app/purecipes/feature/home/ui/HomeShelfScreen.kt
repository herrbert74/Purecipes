package app.purecipes.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.shared.domain.model.HomeShelfId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
fun HomeShelfScreen(
	title: String,
	shelfId: HomeShelfId,
	onBack: () -> Unit,
	onRecipeSelect: (recipeId: Int, transitionKey: String?) -> Unit,
	modifier: Modifier = Modifier,
	collectionSlug: String? = null,
	habitCuisine: String? = null,
	sessionKey: String? = null,
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: HomeShelfViewModel = assistedMetroViewModel<
		HomeShelfViewModel,
		HomeShelfViewModel.Factory,
		>(
		key = collectionSlug ?: habitCuisine ?: shelfId.name,
	) {
		create(
			shelfId = shelfId,
			collectionSlug = collectionSlug,
			habitCuisine = habitCuisine,
			title = title,
			sessionKey = sessionKey,
		)
	},
) {
	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
	}
	HomeShelfContent(
		title = viewModel.title,
		isLoading = viewModel.isLoading,
		errorMessage = viewModel.errorMessage,
		recipes = viewModel.recipes,
		paginationState = viewModel.paginationState,
		onBack = onBack,
		onRecipeSelect = onRecipeSelect,
		modifier = modifier,
		bannerAdViewModel = bannerAdViewModel,
	)
}
