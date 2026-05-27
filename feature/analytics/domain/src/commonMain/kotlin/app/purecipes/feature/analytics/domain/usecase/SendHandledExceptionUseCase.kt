package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository
import dev.zacsweers.metro.Inject

@Inject
class SendHandledExceptionUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(throwable: Throwable) {
		crashRepository.sendHandledException(throwable)
	}
}
