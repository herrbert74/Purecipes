package app.purecipes.feature.analytics.data.datasource

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
actual class CrashlyticsDataSource actual constructor() : CrashDataSource {

	actual override fun logBreadcrumb(message: String) {
		runCatching { CrashlyticsKotlin.logMessage(message) }.getOrNull()
	}

	actual override fun sendHandledException(throwable: Throwable) {
		runCatching { CrashlyticsKotlin.sendHandledException(throwable) }.getOrNull()
	}

	actual override fun setCustomValue(key: String, value: String) {
		runCatching { CrashlyticsKotlin.setCustomValue(key, value) }.getOrNull()
	}

	actual override fun setUserId(userId: String?) {
		runCatching { CrashlyticsKotlin.setUserId(userId.orEmpty()) }.getOrNull()
	}
}
