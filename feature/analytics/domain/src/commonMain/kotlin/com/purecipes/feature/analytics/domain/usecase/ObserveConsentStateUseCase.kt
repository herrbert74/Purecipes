package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.repository.ConsentRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveConsentStateUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke(): StateFlow<ConsentState> {
		return consentRepository.observeConsentState()
	}
}