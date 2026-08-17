package app.purecipes.feature.ads.data.datasource

class FakeAdsDataSource : AdsDataSource {

	var initializeCalls = 0
	var lastAppId: String? = null
	var lastInterstitialAdUnitId: String? = null
	var showInterstitialCalls = 0
	var lastOnImpression: (() -> Unit)? = null
	var lastOnClicked: (() -> Unit)? = null

	override fun initialize(appId: String?, interstitialAdUnitId: String?) {
		initializeCalls += 1
		lastAppId = appId
		lastInterstitialAdUnitId = interstitialAdUnitId
	}

	override fun showInterstitial(
		onDismissed: () -> Unit,
		onImpression: (() -> Unit)?,
		onClicked: (() -> Unit)?,
	) {
		showInterstitialCalls += 1
		lastOnImpression = onImpression
		lastOnClicked = onClicked
		onDismissed()
	}
}
