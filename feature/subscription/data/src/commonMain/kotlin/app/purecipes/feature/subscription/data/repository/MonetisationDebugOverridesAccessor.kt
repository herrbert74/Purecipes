package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class MonetisationDebugOverridesAccessor : MonetisationDebugOverridesRepository {

	private val overrides = MutableStateFlow(MonetisationDebugOverrides())

	override fun observe(): Flow<MonetisationDebugOverrides> = overrides.asStateFlow()

	override fun setPremiumStatusOverride(override: PremiumStatusOverride) {
		overrides.update { current -> current.copy(premiumStatus = override) }
	}

	override fun setAdsDisplayOverride(override: AdsDisplayOverride) {
		overrides.update { current -> current.copy(adsDisplay = override) }
	}
}
