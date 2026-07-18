package app.purecipes.feature.subscription.data.datasource

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import kotlinx.coroutines.flow.Flow

interface MonetisationDebugOverridesDataSource {

	fun observe(): Flow<MonetisationDebugOverrides>

	fun setPremiumStatusOverride(override: PremiumStatusOverride)

	fun setAdsDisplayOverride(override: AdsDisplayOverride)
}
