package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import app.purecipes.feature.subscription.domain.repository.SubscriptionRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Inject
class ObservePremiumStatusUseCase(
	private val repository: SubscriptionRepository,
	private val monetisationDebugOverrides: MonetisationDebugOverridesRepository,
) {

	operator fun invoke(): Flow<Boolean> {
		return monetisationDebugOverrides.observe().flatMapLatest { overrides ->
			when (overrides.premiumStatus) {
				PremiumStatusOverride.AUTO ->
					repository.observeSubscriptionState().map { state -> state.isPremium }

				PremiumStatusOverride.FORCE_FREE -> flowOf(false)
				PremiumStatusOverride.FORCE_PREMIUM -> flowOf(true)
			}
		}
	}
}
