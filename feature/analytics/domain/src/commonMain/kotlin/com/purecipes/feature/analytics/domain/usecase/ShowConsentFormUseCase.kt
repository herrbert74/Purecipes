package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.repository.ConsentRepository

class ShowConsentFormUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke() {
		consentRepository.showConsentForm()
	}
}
