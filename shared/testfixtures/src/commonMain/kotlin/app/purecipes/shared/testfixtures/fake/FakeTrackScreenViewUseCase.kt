package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import app.purecipes.feature.analytics.domain.repository.CrashRepository
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashCustomValueUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase

fun fakeTrackScreenViewUseCase(
	analyticsRepository: AnalyticsRepository,
	crashRepository: CrashRepository = FakeCrashRepository(),
): TrackScreenViewUseCase = TrackScreenViewUseCase(
	analyticsRepository = analyticsRepository,
	logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
	setCrashCustomValue = SetCrashCustomValueUseCase(crashRepository),
)
