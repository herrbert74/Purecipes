package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.repository.CrashRepository

class SetCrashUserIdUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(userId: String?) {
		crashRepository.setUserId(userId)
	}
}
