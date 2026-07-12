package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.repository.CrashRepository

class FakeCrashRepository : CrashRepository {

	val breadcrumbs = mutableListOf<String>()
	val handledExceptions = mutableListOf<Throwable>()
	val customValues = linkedMapOf<String, String>()
	var lastUserId: String? = null
		private set

	override fun logBreadcrumb(message: String) {
		breadcrumbs += message
	}

	override fun sendHandledException(throwable: Throwable) {
		handledExceptions += throwable
	}

	override fun setCustomValue(key: String, value: String) {
		customValues[key] = value
	}

	override fun setUserId(userId: String?) {
		lastUserId = userId
	}
}
