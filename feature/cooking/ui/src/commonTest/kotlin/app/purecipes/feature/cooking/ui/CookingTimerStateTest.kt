package app.purecipes.feature.cooking.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CookingTimerStateTest {

	@Test
	fun displayTimePadsSeconds() {
		CookingTimerState(
			label = "5 min",
			totalSeconds = 300,
			remainingSeconds = 65,
		).displayTime shouldBe "1:05"
	}

	@Test
	fun fromDurationCopiesTotals() {
		val duration = CookingStepHighlight.Duration(
			startIndex = 0,
			endIndex = 8,
			text = "5 min",
			totalSeconds = 300,
		)
		val timer = CookingTimerState.fromDuration(duration)
		timer.label shouldBe "5 min"
		timer.totalSeconds shouldBe 300
		timer.remainingSeconds shouldBe 300
		timer.isComplete shouldBe false
	}

	@Test
	fun isCompleteWhenRemainingIsZero() {
		CookingTimerState(
			label = "5 min",
			totalSeconds = 300,
			remainingSeconds = 0,
		).isComplete shouldBe true
	}
}
