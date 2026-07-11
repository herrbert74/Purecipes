package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import dev.zacsweers.metro.Inject

@Inject
class SetAdsDisplayOverrideUseCase(
	private val repository: MonetisationDebugOverridesRepository,
) {

	operator fun invoke(override: AdsDisplayOverride) {
		repository.setAdsDisplayOverride(override)
	}
}
