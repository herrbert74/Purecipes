package app.purecipes.feature.ads.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

const val SHELF_CARD_BANNER_AD_TAG = "shelfCardBannerAd"

private const val SHELF_CARD_AD_WIDTH_DP = 160
private const val SHELF_CARD_AD_HEIGHT_DP = 192

@Composable
fun ShelfCardBannerAd(
	viewModel: BannerAdViewModel?,
	modifier: Modifier = Modifier,
) {
	if (viewModel == null && !LocalInspectionMode.current) {
		return
	}
	Card(
		modifier = modifier
			.width(SHELF_CARD_AD_WIDTH_DP.dp)
			.height(SHELF_CARD_AD_HEIGHT_DP.dp)
			.testTag(SHELF_CARD_BANNER_AD_TAG),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
		),
	) {
		if (LocalInspectionMode.current || viewModel == null) {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = "Ad",
					style = PurecipesTheme.typography.labelMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		} else {
			PlatformBannerAd(
				adUnitId = viewModel.bannerAdUnitId,
				modifier = Modifier.fillMaxSize(),
				widthDp = SHELF_CARD_AD_WIDTH_DP,
				heightDp = SHELF_CARD_AD_HEIGHT_DP,
				onImpression = viewModel::onAdImpression,
				onClick = viewModel::onAdClicked,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun ShelfCardBannerAdPreview() {
	PurecipesPreviewScaffold {
		ShelfCardBannerAd(viewModel = null)
	}
}
