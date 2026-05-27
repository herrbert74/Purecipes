package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository
import dev.zacsweers.metro.Inject

@Inject
class LogBreadcrumbUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(message: String) {
		crashRepository.logBreadcrumb(message)
	}
}
