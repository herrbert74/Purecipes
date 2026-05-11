package app.purecipes.feature.analytics.data.datasource

internal actual class CrashlyticsDataSource actual constructor() : CrashDataSource {

	actual override fun logBreadcrumb(message: String) = Unit

	actual override fun sendHandledException(throwable: Throwable) = Unit

	actual override fun setCustomValue(key: String, value: String) = Unit

	actual override fun setUserId(userId: String?) = Unit
}
