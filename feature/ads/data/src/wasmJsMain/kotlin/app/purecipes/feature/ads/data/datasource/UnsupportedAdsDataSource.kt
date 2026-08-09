package app.purecipes.feature.ads.data.datasource

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UnsupportedAdsDataSource : AdsDataSource {

	override fun initialize(appId: String?, interstitialAdUnitId: String?) = Unit

	override fun showInterstitial(
		onDismissed: () -> Unit,
		onImpression: (() -> Unit)?,
		onClicked: (() -> Unit)?,
	) {
		onDismissed()
	}
}
