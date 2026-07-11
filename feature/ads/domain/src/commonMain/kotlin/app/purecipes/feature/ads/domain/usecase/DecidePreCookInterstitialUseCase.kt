package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
class DecidePreCookInterstitialUseCase(
	private val observePremiumStatus: ObservePremiumStatusUseCase,
	private val preCookInterstitialChance: PreCookInterstitialChance,
) {

	suspend operator fun invoke(): Boolean {
		if (observePremiumStatus().first()) {
			return false
		}
		return preCookInterstitialChance()
	}
}
