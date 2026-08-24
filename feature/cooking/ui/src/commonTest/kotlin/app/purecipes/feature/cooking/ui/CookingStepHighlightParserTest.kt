package app.purecipes.feature.cooking.ui

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class CookingStepHighlightParserTest {

	@Test
	fun parseSingleMinuteDuration() {
		val highlights = CookingStepHighlightParser.parse("Simmer for 10 minutes until thick.")
		highlights.shouldHaveSize(1)
		val duration = highlights.single().shouldBeInstanceOf<CookingStepHighlight.Duration>()
		duration.text shouldBe "10 minutes"
		duration.totalSeconds shouldBe 600
	}

	@Test
	fun parseRangeDurationUsesUpperBound() {
		val highlights = CookingStepHighlightParser.parse("Bake for 10-15 minutes.")
		val duration = highlights.single().shouldBeInstanceOf<CookingStepHighlight.Duration>()
		duration.text shouldBe "10-15 minutes"
		duration.totalSeconds shouldBe 900
	}

	@Test
	fun parseTemperatureAndDuration() {
		val highlights = CookingStepHighlightParser.parse("Bake at 180°C for 25 min.")
		highlights.shouldHaveSize(2)
		highlights[0].shouldBeInstanceOf<CookingStepHighlight.Temperature>().text shouldBe "180°C"
		highlights[1].shouldBeInstanceOf<CookingStepHighlight.Duration>().totalSeconds shouldBe 25 * 60
	}

	@Test
	fun parseSecondsAndHours() {
		CookingStepHighlightParser.parse("Wait 30 seconds.")
			.single()
			.shouldBeInstanceOf<CookingStepHighlight.Duration>()
			.totalSeconds shouldBe 30
		CookingStepHighlightParser.parse("Proof for 1 hour.")
			.single()
			.shouldBeInstanceOf<CookingStepHighlight.Duration>()
			.totalSeconds shouldBe 3600
	}

	@Test
	fun parseReturnsEmptyWhenNoHighlights() {
		CookingStepHighlightParser.parse("Season to taste and serve.").shouldHaveSize(0)
	}
}
