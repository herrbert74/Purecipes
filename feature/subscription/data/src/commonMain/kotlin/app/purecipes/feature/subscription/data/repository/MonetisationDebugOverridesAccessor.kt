package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.data.datasource.MonetisationDebugOverridesDataSource
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class MonetisationDebugOverridesAccessor(
	private val dataSource: MonetisationDebugOverridesDataSource,
	private val purecipesConfig: PurecipesConfig,
) : MonetisationDebugOverridesRepository {

	override fun observe(): Flow<MonetisationDebugOverrides> {
		return if (purecipesConfig.showMonetisationDebugOverrides()) {
			dataSource.observe()
		} else {
			flowOf(MonetisationDebugOverrides())
		}
	}

	override fun setPremiumStatusOverride(override: PremiumStatusOverride) {
		if (purecipesConfig.showMonetisationDebugOverrides()) {
			dataSource.setPremiumStatusOverride(override)
		}
	}

	override fun setAdsDisplayOverride(override: AdsDisplayOverride) {
		if (purecipesConfig.showMonetisationDebugOverrides()) {
			dataSource.setAdsDisplayOverride(override)
		}
	}
}
