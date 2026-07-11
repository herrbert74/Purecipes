package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Inject
class ObserveShouldShowAdsUseCase(
	private val observePremiumStatus: ObservePremiumStatusUseCase,
	private val monetisationDebugOverrides: MonetisationDebugOverridesRepository,
) {

	operator fun invoke(): Flow<Boolean> {
		return monetisationDebugOverrides.observe().flatMapLatest { overrides ->
			when (overrides.adsDisplay) {
				AdsDisplayOverride.AUTO -> observePremiumStatus().map { isPremium -> !isPremium }
				AdsDisplayOverride.FORCE_ON -> flowOf(true)
				AdsDisplayOverride.FORCE_OFF -> flowOf(false)
			}
		}
	}
}
