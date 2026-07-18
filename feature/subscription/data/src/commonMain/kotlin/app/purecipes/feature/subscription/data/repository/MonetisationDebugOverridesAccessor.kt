package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.data.datasource.MonetisationDebugOverridesDataSource
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class MonetisationDebugOverridesAccessor(
	private val dataSource: MonetisationDebugOverridesDataSource,
) : MonetisationDebugOverridesRepository {

	override fun observe(): Flow<MonetisationDebugOverrides> = dataSource.observe()

	override fun setPremiumStatusOverride(override: PremiumStatusOverride) {
		dataSource.setPremiumStatusOverride(override)
	}

	override fun setAdsDisplayOverride(override: AdsDisplayOverride) {
		dataSource.setAdsDisplayOverride(override)
	}
}
