package app.purecipes.feature.ads.domain.runtime

import platform.UIKit.UIView

object IosAdsNativeBridge {

	private var initializeHandler: ((String, String) -> Unit)? = null
	private var showInterstitialHandler: ((String, () -> Unit, () -> Unit, () -> Unit) -> Unit)? = null
	private var createBannerHandler: ((String, () -> Unit, () -> Unit) -> UIView)? = null

	fun registerHandlers(
		initialize: (String, String) -> Unit,
		showInterstitial: (String, () -> Unit, () -> Unit, () -> Unit) -> Unit,
		createBanner: (String, () -> Unit, () -> Unit) -> UIView,
	) {
		initializeHandler = initialize
		showInterstitialHandler = showInterstitial
		createBannerHandler = createBanner
	}

	fun initialize(appId: String, interstitialAdUnitId: String) {
		initializeHandler?.invoke(appId, interstitialAdUnitId)
	}

	fun showInterstitial(
		adUnitId: String,
		onDismissed: () -> Unit,
		onImpression: () -> Unit,
		onClicked: () -> Unit,
	) {
		val handler = showInterstitialHandler
		if (handler == null) {
			onDismissed()
			return
		}
		handler(adUnitId, onDismissed, onImpression, onClicked)
	}

	fun createBannerView(
		adUnitId: String,
		onImpression: () -> Unit,
		onClick: () -> Unit,
	): UIView {
		return createBannerHandler?.invoke(adUnitId, onImpression, onClick) ?: UIView()
	}
}
