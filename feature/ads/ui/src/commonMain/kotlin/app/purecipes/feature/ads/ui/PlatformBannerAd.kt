package app.purecipes.feature.ads.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlatformBannerAd(
	adUnitId: String,
	modifier: Modifier = Modifier,
)
