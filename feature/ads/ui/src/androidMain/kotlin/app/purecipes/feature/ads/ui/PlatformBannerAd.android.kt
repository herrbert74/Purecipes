package app.purecipes.feature.ads.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
internal actual fun PlatformBannerAd(
	adUnitId: String,
	modifier: Modifier,
) {
	AndroidView(
		modifier = modifier,
		factory = { context ->
			AdView(context).apply {
				setAdSize(AdSize.BANNER)
				this.adUnitId = adUnitId
				loadAd(AdRequest.Builder().build())
			}
		},
	)
}
