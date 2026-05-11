package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository

class SendHandledExceptionUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(throwable: Throwable) {
		crashRepository.sendHandledException(throwable)
	}
}
