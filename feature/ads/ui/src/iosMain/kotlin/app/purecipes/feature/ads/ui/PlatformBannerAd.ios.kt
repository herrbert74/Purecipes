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
	widthDp: Int,
	heightDp: Int,
	onImpression: (() -> Unit)?,
	onClick: (() -> Unit)?,
) {
	UIKitView(
		factory = {
			IosAdsNativeBridge.createBannerView(
				adUnitId = adUnitId,
				widthDp = widthDp,
				heightDp = heightDp,
				onImpression = { onImpression?.invoke() },
				onClick = { onClick?.invoke() },
			)
		},
		modifier = modifier.height(heightDp.dp),
	)
}
