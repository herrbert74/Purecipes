package app.purecipes.feature.analytics.data.datasource

internal interface CrashDataSource {
	fun logBreadcrumb(message: String)
	fun sendHandledException(throwable: Throwable)
	fun setCustomValue(key: String, value: String)
	fun setUserId(userId: String?)
}
