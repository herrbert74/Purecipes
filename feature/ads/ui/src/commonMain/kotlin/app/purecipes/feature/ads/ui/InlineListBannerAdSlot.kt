package app.purecipes.feature.ads.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.ads.domain.InlineAdPlacement
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun InlineListBannerAdSlot(
	contentIndex: Int,
	contentCount: Int,
	viewModel: BannerAdViewModel?,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val showAd = viewModel != null &&
		viewModel.shouldShowAds &&
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(contentIndex, contentCount)
	Column(
		modifier = modifier,
		verticalArrangement = if (showAd) {
			Arrangement.spacedBy(PurecipesTheme.space.m)
		} else {
			Arrangement.Top
		},
	) {
		if (showAd) {
			BannerAd(viewModel = viewModel)
		}
		content()
	}
}
