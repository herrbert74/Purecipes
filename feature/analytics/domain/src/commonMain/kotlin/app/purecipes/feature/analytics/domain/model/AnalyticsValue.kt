package app.purecipes.feature.analytics.domain.model

sealed interface AnalyticsValue {
	data class BooleanValue(val value: Boolean) : AnalyticsValue

	data class NumberValue(val value: Long) : AnalyticsValue

	data class TextValue(val value: String) : AnalyticsValue
}

internal fun Boolean.asAnalyticsValue(): AnalyticsValue = AnalyticsValue.BooleanValue(this)

internal fun Int.asAnalyticsValue(): AnalyticsValue = AnalyticsValue.NumberValue(toLong())

internal fun Long.asAnalyticsValue(): AnalyticsValue = AnalyticsValue.NumberValue(this)
