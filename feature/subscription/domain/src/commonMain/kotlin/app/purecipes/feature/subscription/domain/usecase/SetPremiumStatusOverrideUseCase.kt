package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import dev.zacsweers.metro.Inject

@Inject
class SetPremiumStatusOverrideUseCase(
	private val repository: MonetisationDebugOverridesRepository,
) {

	operator fun invoke(override: PremiumStatusOverride) {
		repository.setPremiumStatusOverride(override)
	}
}
