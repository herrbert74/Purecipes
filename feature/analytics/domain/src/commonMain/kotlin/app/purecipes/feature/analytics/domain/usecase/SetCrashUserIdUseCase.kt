package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository

class SetCrashUserIdUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(userId: String?) {
		crashRepository.setUserId(userId)
	}
}
