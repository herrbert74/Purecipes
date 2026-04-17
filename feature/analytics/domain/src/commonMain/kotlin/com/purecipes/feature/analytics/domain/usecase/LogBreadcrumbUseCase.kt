package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.repository.CrashRepository

class LogBreadcrumbUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(message: String) {
		crashRepository.logBreadcrumb(message)
	}
}
