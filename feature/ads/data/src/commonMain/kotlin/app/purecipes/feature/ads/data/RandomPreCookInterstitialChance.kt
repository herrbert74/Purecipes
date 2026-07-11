package app.purecipes.feature.ads.data

import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.random.Random

@Inject
@ContributesBinding(AppScope::class)
class RandomPreCookInterstitialChance : PreCookInterstitialChance {

	override fun invoke(): Boolean {
		return Random.nextDouble() < AdMobDefaults.PRE_COOK_INTERSTITIAL_PROBABILITY
	}
}
