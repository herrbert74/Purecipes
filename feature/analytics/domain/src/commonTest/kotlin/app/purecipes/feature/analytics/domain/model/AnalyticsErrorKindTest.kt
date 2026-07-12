package app.purecipes.feature.analytics.domain.model

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.shared.domain.model.MeasurementSystem
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsErrorKindTest {

	@Test
	fun `maps failure types to analytics error kinds`() {
		Failure.ServerError("boom").toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.SERVER_ERROR
		Failure.UnknownApiError.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.UNKNOWN_API_ERROR
		Failure.IoFailure.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.IO_FAILURE
		Failure.UnknownHostFailure.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.UNKNOWN_HOST
		Failure.UnexpectedFailure.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.UNEXPECTED
		Failure.NotModified.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.NOT_MODIFIED
		Failure.UserNotLoggedIn.toAnalyticsErrorKind() shouldBe AnalyticsErrorKind.USER_NOT_LOGGED_IN
	}
}

class AnalyticsMeasurementSystemTest {

	@Test
	fun `maps measurement systems to analytics values`() {
		MeasurementSystem.METRIC.toAnalyticsMeasurementSystem() shouldBe AnalyticsMeasurementSystem.METRIC
		MeasurementSystem.IMPERIAL.toAnalyticsMeasurementSystem() shouldBe AnalyticsMeasurementSystem.IMPERIAL
		MeasurementSystem.MIXED.toAnalyticsMeasurementSystem() shouldBe AnalyticsMeasurementSystem.ORIGINAL
	}
}
