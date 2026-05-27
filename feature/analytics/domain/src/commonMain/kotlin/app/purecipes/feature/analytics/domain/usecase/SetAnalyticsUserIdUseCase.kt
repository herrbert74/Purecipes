package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import dev.zacsweers.metro.Inject

@Inject
class SetAnalyticsUserIdUseCase(
	private val analyticsRepository: AnalyticsRepository,
) {
	operator fun invoke(userId: String?) {
		analyticsRepository.setUserId(userId)
	}
}
