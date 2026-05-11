package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.CrashDataSource
import app.purecipes.feature.analytics.domain.repository.CrashRepository

internal class CrashAccessor(
	private val crashDataSource: CrashDataSource,
) : CrashRepository {

	override fun logBreadcrumb(message: String) {
		crashDataSource.logBreadcrumb(message)
	}

	override fun sendHandledException(throwable: Throwable) {
		crashDataSource.sendHandledException(throwable)
	}

	override fun setCustomValue(key: String, value: String) {
		crashDataSource.setCustomValue(key, value)
	}

	override fun setUserId(userId: String?) {
		crashDataSource.setUserId(userId)
	}
}
