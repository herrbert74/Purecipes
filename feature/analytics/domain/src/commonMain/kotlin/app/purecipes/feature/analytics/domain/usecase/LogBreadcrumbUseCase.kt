package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository

class LogBreadcrumbUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(message: String) {
		crashRepository.logBreadcrumb(message)
	}
}
