package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
class DecidePreCookInterstitialUseCase(
	private val observeShouldShowAds: ObserveShouldShowAdsUseCase,
	private val preCookInterstitialChance: PreCookInterstitialChance,
) {

	suspend operator fun invoke(): Boolean {
		if (!observeShouldShowAds().first()) {
			return false
		}
		return preCookInterstitialChance()
	}
}
