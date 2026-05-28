package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.CrashDataSource
import app.purecipes.feature.analytics.domain.repository.CrashRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class CrashAccessor(
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
