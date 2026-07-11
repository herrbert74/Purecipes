package app.purecipes.feature.ads.data.datasource

class FakeAdsDataSource : AdsDataSource {

	var initializeCalls = 0
	var lastAppId: String? = null
	var lastInterstitialAdUnitId: String? = null
	var showInterstitialCalls = 0

	override fun initialize(appId: String?, interstitialAdUnitId: String?) {
		initializeCalls += 1
		lastAppId = appId
		lastInterstitialAdUnitId = interstitialAdUnitId
	}

	override fun showInterstitial(onDismissed: () -> Unit) {
		showInterstitialCalls += 1
		onDismissed()
	}
}
