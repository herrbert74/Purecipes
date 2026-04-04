package com.purecipes.feature.analytics.domain.model

enum class ConsentState {
	UNKNOWN,
	REQUIRED,
	OBTAINED,
	DENIED,
	NOT_REQUIRED,
}

fun ConsentState.allowsAnalytics(): Boolean {
	return this == ConsentState.OBTAINED || this == ConsentState.NOT_REQUIRED
}
