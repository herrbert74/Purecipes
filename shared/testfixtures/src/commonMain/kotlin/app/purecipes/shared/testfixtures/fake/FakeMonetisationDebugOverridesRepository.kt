package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeMonetisationDebugOverridesRepository(
	initialOverrides: MonetisationDebugOverrides = MonetisationDebugOverrides(),
) : MonetisationDebugOverridesRepository {

	private val overrides = MutableStateFlow(initialOverrides)

	override fun observe(): Flow<MonetisationDebugOverrides> = overrides.asStateFlow()

	override fun setPremiumStatusOverride(override: PremiumStatusOverride) {
		overrides.update { current -> current.copy(premiumStatus = override) }
	}

	override fun setAdsDisplayOverride(override: AdsDisplayOverride) {
		overrides.update { current -> current.copy(adsDisplay = override) }
	}
}
