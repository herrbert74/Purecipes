package app.purecipes.backend.tools

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ProgressLoggerTest {

	@Test
	fun logsStartMessageIntervalAndFinalItem() {
		val lines = mutableListOf<String>()
		val logger = ProgressLogger(
			stepLabel = "Rebuilding ingredient lines",
			total = 5,
			startMessage = "Processing ingredient lines for 5 recipes...",
			interval = 2,
			logLine = lines::add,
		)

		(1..5).forEach(logger::onItemProcessed)

		lines shouldBe listOf(
			"Processing ingredient lines for 5 recipes...",
			"Rebuilding ingredient lines: 2/5 (40%)",
			"Rebuilding ingredient lines: 4/5 (80%)",
			"Rebuilding ingredient lines: 5/5 (100%)",
		)
	}
}
