package app.purecipes.feature.ads.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag

internal const val BANNER_AD_TAG = "bannerAd"

@Composable
fun BannerAd(
	modifier: Modifier = Modifier,
	viewModel: BannerAdViewModel? = null,
) {
	if (!LocalInspectionMode.current && viewModel != null && viewModel.shouldShowAds) {
		PlatformBannerAd(
			adUnitId = viewModel.bannerAdUnitId,
			modifier = modifier
				.fillMaxWidth()
				.testTag(BANNER_AD_TAG),
		)
	}
}
