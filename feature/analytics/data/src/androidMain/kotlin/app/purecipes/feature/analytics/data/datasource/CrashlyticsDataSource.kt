package app.purecipes.feature.analytics.data.datasource

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin

internal actual class CrashlyticsDataSource actual constructor() : CrashDataSource {

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
