package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.ads.domain.repository.AdsRepository
import dev.zacsweers.metro.Inject

@Inject
class InitializeAdsUseCase(
	private val repository: AdsRepository,
) {

	operator fun invoke() {
		repository.initialize()
	}
}
