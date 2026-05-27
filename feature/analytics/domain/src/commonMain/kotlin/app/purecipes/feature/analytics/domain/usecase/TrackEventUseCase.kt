package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import dev.zacsweers.metro.Inject

@Inject
class TrackEventUseCase(
	private val analyticsRepository: AnalyticsRepository,
) {
	operator fun invoke(event: AnalyticsEvent) {
		analyticsRepository.trackEvent(event)
	}
}
