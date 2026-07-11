package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.repository.AdsRepository
import dev.zacsweers.metro.Inject

@Inject
class ShowInterstitialAdUseCase(
	private val repository: AdsRepository,
) {

	operator fun invoke(onDismissed: () -> Unit) {
		repository.showInterstitial(onDismissed)
	}
}
