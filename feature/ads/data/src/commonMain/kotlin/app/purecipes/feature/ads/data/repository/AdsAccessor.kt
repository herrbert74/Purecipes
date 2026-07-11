package app.purecipes.feature.ads.data.repository

import app.purecipes.feature.ads.data.datasource.AdsDataSource
import app.purecipes.feature.ads.domain.repository.AdsRepository
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class AdsAccessor(
	private val adsDataSource: AdsDataSource,
	private val purecipesConfig: PurecipesConfig,
) : AdsRepository {

	override fun initialize() {
		adsDataSource.initialize(
			appId = purecipesConfig.adMobAppId(),
			interstitialAdUnitId = purecipesConfig.adMobInterstitialAdUnitId(),
		)
	}

	override fun showInterstitial(onDismissed: () -> Unit) {
		adsDataSource.showInterstitial(onDismissed)
	}
}
