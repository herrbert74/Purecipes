package app.purecipes.shared.testfixtures.uitest

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AccessibilityCheckSuppressionTest {

	@Test
	fun matchesCheckClassWhenMessageFilterIsAbsent() {
		val suppression = AccessibilityCheckSuppression(checkClassName = "TouchTargetSizeCheck")

		suppression.matches("TouchTargetSizeCheck", "Item has a small touch target") shouldBe true
		suppression.matches("SpeakableTextPresentCheck", "Item has a small touch target") shouldBe false
	}

	@Test
	fun matchesOnlyWhenMessageContainsTheExpectedText() {
		val suppression = AccessibilityCheckSuppression(
			checkClassName = "TextContrastCheck",
			messageContains = "Image view",
		)

		suppression.matches("TextContrastCheck", "Image view contrast is too low") shouldBe true
		suppression.matches("TextContrastCheck", "Text contrast is too low") shouldBe false
	}

	@Test
	fun matchesOnlyWhenHierarchyContainsTheExpectedText() {
		val suppression = AccessibilityCheckSuppression(
			checkClassName = "TouchTargetSizeCheck",
			hierarchyContains = "filterBottomSheetRecipeFiltersTab",
		)

		suppression.matches(
			actualCheckClassName = "TouchTargetSizeCheck",
			message = "This item's height is 24dp",
			hierarchyDescription = "testTag=filterBottomSheetRecipeFiltersTab bounds=Rect(540, 574 - 1080, 637)",
		) shouldBe true
		suppression.matches(
			actualCheckClassName = "TouchTargetSizeCheck",
			message = "This item's height is 24dp",
			hierarchyDescription = "testTag=filterAddIngredientButton",
		) shouldBe false
	}
}
