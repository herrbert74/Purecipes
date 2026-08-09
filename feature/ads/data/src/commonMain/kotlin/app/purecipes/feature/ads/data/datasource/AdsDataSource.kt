package app.purecipes.feature.ads.data.datasource

interface AdsDataSource {

	fun initialize(appId: String?, interstitialAdUnitId: String?)

	fun showInterstitial(
		onDismissed: () -> Unit,
		onImpression: (() -> Unit)? = null,
		onClicked: (() -> Unit)? = null,
	)
}
