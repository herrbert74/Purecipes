package com.purecipes.feature.analytics.domain.repository

interface CrashRepository {
	fun logBreadcrumb(message: String)
	fun sendHandledException(throwable: Throwable)
	fun setCustomValue(key: String, value: String)
	fun setUserId(userId: String?)
}
