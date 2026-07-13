package app.purecipes.feature.analytics.domain.model

import app.purecipes.shared.domain.model.MeasurementSystem

object AnalyticsMeasurementSystem {

	const val METRIC = "metric"
	const val IMPERIAL = "imperial"
	const val ORIGINAL = "original"
}

fun MeasurementSystem.toAnalyticsMeasurementSystem(): String {
	return when (this) {
		MeasurementSystem.METRIC -> AnalyticsMeasurementSystem.METRIC
		MeasurementSystem.IMPERIAL -> AnalyticsMeasurementSystem.IMPERIAL
		MeasurementSystem.MIXED -> AnalyticsMeasurementSystem.ORIGINAL
	}
}
