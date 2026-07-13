package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import dev.zacsweers.metro.Inject

@Inject
class TrackScreenViewUseCase(
	private val analyticsRepository: AnalyticsRepository,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val setCrashCustomValue: SetCrashCustomValueUseCase,
) {

	operator fun invoke(screenName: String, origin: AnalyticsOrigin? = null) {
		logBreadcrumb(CrashBreadcrumb.screen(screenName))
		setCrashCustomValue(AnalyticsGlobalProperty.CURRENT_SCREEN, screenName)
		analyticsRepository.setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.CURRENT_SCREEN to AnalyticsValue.TextValue(screenName),
			),
		)
		val properties = buildMap {
			if (origin != null) {
				put("origin", AnalyticsValue.TextValue(origin.value))
			}
		}
		analyticsRepository.trackScreenView(screenName, properties)
	}
}
