package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import dev.zacsweers.metro.Inject

@Inject
class RefreshConsentUseCase(
	private val consentRepository: ConsentRepository,
) {
	operator fun invoke() {
		consentRepository.refreshConsent()
	}
}
