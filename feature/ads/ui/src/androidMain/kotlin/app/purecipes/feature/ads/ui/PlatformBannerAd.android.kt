package app.purecipes.feature.ads.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
internal actual fun PlatformBannerAd(
	adUnitId: String,
	modifier: Modifier,
	widthDp: Int,
	heightDp: Int,
	onImpression: (() -> Unit)?,
	onClick: (() -> Unit)?,
) {
	val adSize = if (widthDp == STANDARD_BANNER_WIDTH_DP && heightDp == STANDARD_BANNER_HEIGHT_DP) {
		AdSize.BANNER
	} else {
		AdSize(widthDp, heightDp)
	}
	AndroidView(
		modifier = modifier,
		factory = { context ->
			AdView(context).apply {
				setAdSize(adSize)
				this.adUnitId = adUnitId
				adListener = object : AdListener() {
					override fun onAdImpression() {
						onImpression?.invoke()
					}

					override fun onAdClicked() {
						onClick?.invoke()
					}
				}
				loadAd(AdRequest.Builder().build())
			}
		},
	)
}
