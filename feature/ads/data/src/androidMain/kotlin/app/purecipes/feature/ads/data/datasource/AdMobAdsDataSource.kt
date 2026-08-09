package app.purecipes.feature.ads.data.datasource

import app.purecipes.feature.ads.data.runtime.AdsAndroidRuntime
import app.purecipes.feature.ads.domain.AdMobDefaults
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class AdMobAdsDataSource : AdsDataSource {

	private var interstitialAdUnitId: String = AdMobDefaults.INTERSTITIAL_AD_UNIT_ID
	private var interstitialAd: InterstitialAd? = null
	private var isInitialized = false

	override fun initialize(appId: String?, interstitialAdUnitId: String?) {
		this.interstitialAdUnitId = interstitialAdUnitId
			?.takeIf { it.isNotBlank() }
			?: AdMobDefaults.INTERSTITIAL_AD_UNIT_ID
		if (isInitialized) {
			preloadInterstitial()
			return
		}
		val context = AdsAndroidRuntime.applicationContextOrNull() ?: return
		MobileAds.initialize(context) {
			isInitialized = true
			preloadInterstitial()
		}
	}

	override fun showInterstitial(
		onDismissed: () -> Unit,
		onImpression: (() -> Unit)?,
		onClicked: (() -> Unit)?,
	) {
		val activity = AdsAndroidRuntime.currentActivity()
		val loadedAd = interstitialAd
		if (activity == null || loadedAd == null) {
			preloadInterstitial()
			onDismissed()
			return
		}
		loadedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
			override fun onAdImpression() {
				onImpression?.invoke()
			}

			override fun onAdClicked() {
				onClicked?.invoke()
			}

			override fun onAdDismissedFullScreenContent() {
				interstitialAd = null
				preloadInterstitial()
				onDismissed()
			}

			override fun onAdFailedToShowFullScreenContent(adError: AdError) {
				interstitialAd = null
				preloadInterstitial()
				onDismissed()
			}
		}
		interstitialAd = null
		loadedAd.show(activity)
	}

	private fun preloadInterstitial() {
		val context = AdsAndroidRuntime.applicationContextOrNull() ?: return
		InterstitialAd.load(
			context,
			interstitialAdUnitId,
			AdRequest.Builder().build(),
			object : InterstitialAdLoadCallback() {
				override fun onAdLoaded(ad: InterstitialAd) {
					interstitialAd = ad
				}

				override fun onAdFailedToLoad(adError: LoadAdError) {
					interstitialAd = null
				}
			},
		)
	}
}
