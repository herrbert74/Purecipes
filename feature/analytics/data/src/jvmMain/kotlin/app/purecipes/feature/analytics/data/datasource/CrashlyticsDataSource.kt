package app.purecipes.feature.analytics.data.datasource

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
actual class CrashlyticsDataSource actual constructor() : CrashDataSource {

	actual override fun logBreadcrumb(message: String) = Unit

	actual override fun sendHandledException(throwable: Throwable) = Unit

	actual override fun setCustomValue(key: String, value: String) = Unit

	actual override fun setUserId(userId: String?) = Unit
}
