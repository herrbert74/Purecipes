package app.purecipes.feature.analytics.domain.usecase

import app.purecipes.feature.analytics.domain.repository.CrashRepository
import dev.zacsweers.metro.Inject

@Inject
class SetCrashUserIdUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(userId: String?) {
		crashRepository.setUserId(userId)
	}
}
