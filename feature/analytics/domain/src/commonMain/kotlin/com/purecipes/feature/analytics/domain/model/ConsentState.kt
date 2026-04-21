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

fun ConsentState.toDisplayText(): String {
	return when (this) {
		ConsentState.UNKNOWN -> "Consent status is not available yet."
		ConsentState.REQUIRED -> "Consent is required before analytics can run."
		ConsentState.OBTAINED -> "Consent has been granted for analytics."
		ConsentState.DENIED -> "Consent has been denied for analytics."
		ConsentState.NOT_REQUIRED -> "Consent is not required for analytics on this device."
	}
}
