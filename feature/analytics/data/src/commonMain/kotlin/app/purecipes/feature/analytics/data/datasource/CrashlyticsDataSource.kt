package app.purecipes.feature.analytics.data.datasource

internal expect class CrashlyticsDataSource() : CrashDataSource {
	override fun logBreadcrumb(message: String)
	override fun sendHandledException(throwable: Throwable)
	override fun setCustomValue(key: String, value: String)
	override fun setUserId(userId: String?)
}
