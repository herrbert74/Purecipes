package app.purecipes.shared.testfixtures.uitest

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ClippedSpeakableTextSuppressionTest {

	@Test
	fun suppressesSpeakableTextWhenVisibleHeightIsBelowThreshold() {
		shouldSuppressClippedSpeakableText(
			checkClassName = "SpeakableTextPresentCheck",
			visibleHeightPx = CLIPPED_SPEAKABLE_TEXT_MAX_HEIGHT_PX - 1,
		) shouldBe true
	}

	@Test
	fun keepsSpeakableTextWhenVisibleHeightMeetsThreshold() {
		shouldSuppressClippedSpeakableText(
			checkClassName = "SpeakableTextPresentCheck",
			visibleHeightPx = CLIPPED_SPEAKABLE_TEXT_MAX_HEIGHT_PX,
		) shouldBe false
	}

	@Test
	fun ignoresOtherChecksAndMissingBounds() {
		shouldSuppressClippedSpeakableText(
			checkClassName = "TouchTargetSizeCheck",
			visibleHeightPx = 1,
		) shouldBe false
		shouldSuppressClippedSpeakableText(
			checkClassName = "SpeakableTextPresentCheck",
			visibleHeightPx = null,
		) shouldBe false
	}
}
