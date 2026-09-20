package app.purecipes.feature.ads.ui

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import app.purecipes.feature.ads.domain.runtime.IosAdsNativeBridge

@Composable
internal actual fun PlatformBannerAd(
	adUnitId: String,
	modifier: Modifier,
	onImpression: (() -> Unit)?,
	onClick: (() -> Unit)?,
) {
	UIKitView(
		factory = {
			IosAdsNativeBridge.createBannerView(
				adUnitId = adUnitId,
				onImpression = { onImpression?.invoke() },
				onClick = { onClick?.invoke() },
			)
		},
		modifier = modifier.height(50.dp),
	)
}
