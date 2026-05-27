package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
class ObserveConsentStateUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke(): StateFlow<ConsentState> {
		return consentRepository.observeConsentState()
	}
}
