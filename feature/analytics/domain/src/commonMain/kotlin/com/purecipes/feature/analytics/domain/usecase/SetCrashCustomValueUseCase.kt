package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.repository.CrashRepository

class SetCrashCustomValueUseCase(private val crashRepository: CrashRepository) {
	operator fun invoke(key: String, value: String) {
		crashRepository.setCustomValue(key, value)
	}
}
