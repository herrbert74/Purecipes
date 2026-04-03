package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue

internal fun Map<String, AnalyticsValue>.toAnalyticsJson(): String {
	return entries.joinToString(prefix = "{", postfix = "}", separator = ",") { (key, value) ->
		"\"${key.escapeJson()}\":${value.toAnalyticsJsonValue()}"
	}
}

private fun AnalyticsValue.toAnalyticsJsonValue(): String {
	return when (this) {
		is AnalyticsValue.BooleanValue -> value.toString()
		is AnalyticsValue.NumberValue -> value.toString()
		is AnalyticsValue.TextValue -> "\"${value.escapeJson()}\""
	}
}

private fun String.escapeJson(): String {
	return buildString {
		for (character in this@escapeJson) {
			when (character) {
				'\\' -> append("\\\\")
				'\"' -> append("\\\"")
				'\n' -> append("\\n")
				'\r' -> append("\\r")
				'\t' -> append("\\t")
				else -> append(character)
			}
		}
	}
}