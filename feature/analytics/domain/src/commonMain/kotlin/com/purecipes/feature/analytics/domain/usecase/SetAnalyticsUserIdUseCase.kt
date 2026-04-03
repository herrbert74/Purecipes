package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.repository.AnalyticsRepository

class SetAnalyticsUserIdUseCase(
	private val analyticsRepository: AnalyticsRepository,
) {
	operator fun invoke(userId: String?) {
		analyticsRepository.setUserId(userId)
	}
}