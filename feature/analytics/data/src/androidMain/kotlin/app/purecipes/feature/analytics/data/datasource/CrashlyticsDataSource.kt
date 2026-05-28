package app.purecipes.feature.analytics.data.datasource

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
actual class CrashlyticsDataSource actual constructor() : CrashDataSource {

	actual override fun logBreadcrumb(message: String) {
		CrashlyticsKotlin.logMessage(message)
	}

	actual override fun sendHandledException(throwable: Throwable) {
		CrashlyticsKotlin.sendHandledException(throwable)
	}

	actual override fun setCustomValue(key: String, value: String) {
		CrashlyticsKotlin.setCustomValue(key, value)
	}

	actual override fun setUserId(userId: String?) {
		CrashlyticsKotlin.setUserId(userId.orEmpty())
	}
}
