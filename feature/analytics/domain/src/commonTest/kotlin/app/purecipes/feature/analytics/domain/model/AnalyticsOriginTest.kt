package app.purecipes.feature.analytics.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsOriginTest {

	@Test
	fun `fromValue resolves known origins including deep link and share`() {
		AnalyticsOrigin.fromValue("search") shouldBe AnalyticsOrigin.SEARCH
		AnalyticsOrigin.fromValue("recipe_details") shouldBe AnalyticsOrigin.RECIPE_DETAILS
		AnalyticsOrigin.fromValue("deep_link") shouldBe AnalyticsOrigin.DEEP_LINK
		AnalyticsOrigin.fromValue("share") shouldBe AnalyticsOrigin.SHARE
		AnalyticsOrigin.fromValue("unknown") shouldBe null
	}
}
