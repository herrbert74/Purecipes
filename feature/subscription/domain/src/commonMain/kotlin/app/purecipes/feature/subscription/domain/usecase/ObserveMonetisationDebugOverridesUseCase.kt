package app.purecipes.feature.subscription.domain.usecase

import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.repository.MonetisationDebugOverridesRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveMonetisationDebugOverridesUseCase(
	private val repository: MonetisationDebugOverridesRepository,
) {

	operator fun invoke(): Flow<MonetisationDebugOverrides> = repository.observe()
}
