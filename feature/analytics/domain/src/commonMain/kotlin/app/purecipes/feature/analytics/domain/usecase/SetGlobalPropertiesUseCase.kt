package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import dev.zacsweers.metro.Inject

@Inject
class SetGlobalPropertiesUseCase(
	private val analyticsRepository: AnalyticsRepository,
) {

	operator fun invoke(properties: Map<String, AnalyticsValue>) {
		analyticsRepository.setGlobalProperties(properties)
	}
}
