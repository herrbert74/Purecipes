package app.purecipes.feature.ads.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal const val STANDARD_BANNER_WIDTH_DP = 320

internal const val STANDARD_BANNER_HEIGHT_DP = 50

@Composable
internal expect fun PlatformBannerAd(
	adUnitId: String,
	modifier: Modifier = Modifier,
	widthDp: Int = STANDARD_BANNER_WIDTH_DP,
	heightDp: Int = STANDARD_BANNER_HEIGHT_DP,
	onImpression: (() -> Unit)? = null,
	onClick: (() -> Unit)? = null,
)
