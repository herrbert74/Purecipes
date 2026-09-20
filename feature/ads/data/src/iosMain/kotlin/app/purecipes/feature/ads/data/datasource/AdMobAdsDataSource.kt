package app.purecipes.feature.ads.data.datasource

import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.runtime.IosAdsNativeBridge
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class AdMobAdsDataSource : AdsDataSource {

	private var interstitialAdUnitId: String = AdMobDefaults.IOS_INTERSTITIAL_AD_UNIT_ID

	override fun initialize(appId: String?, interstitialAdUnitId: String?) {
		this.interstitialAdUnitId = interstitialAdUnitId
			?.takeIf { it.isNotBlank() }
			?: AdMobDefaults.IOS_INTERSTITIAL_AD_UNIT_ID
		IosAdsNativeBridge.initialize(
			appId = appId.orEmpty(),
			interstitialAdUnitId = this.interstitialAdUnitId,
		)
	}

	override fun showInterstitial(
		onDismissed: () -> Unit,
		onImpression: (() -> Unit)?,
		onClicked: (() -> Unit)?,
	) {
		IosAdsNativeBridge.showInterstitial(
			adUnitId = interstitialAdUnitId,
			onDismissed = onDismissed,
			onImpression = { onImpression?.invoke() },
			onClicked = { onClicked?.invoke() },
		)
	}
}
