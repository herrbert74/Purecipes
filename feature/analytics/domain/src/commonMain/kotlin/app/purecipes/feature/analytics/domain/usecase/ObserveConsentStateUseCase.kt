package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveConsentStateUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke(): StateFlow<ConsentState> {
		return consentRepository.observeConsentState()
	}
}
