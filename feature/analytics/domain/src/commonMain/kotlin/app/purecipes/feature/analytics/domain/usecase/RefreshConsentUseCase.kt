package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.ConsentRepository

class RefreshConsentUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke() {
		consentRepository.refreshConsent()
	}
}
