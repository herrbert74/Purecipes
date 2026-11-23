package app.purecipes.shared.ui.component.paging

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SelectedItemRevealDeltaTest {

	@Test
	fun returnsZeroWhenItemIsAlreadyFullyVisible() {
		calculateMinimalScrollDelta(
			itemStart = 40,
			itemSize = 200,
			viewportStart = 0,
			viewportEnd = 500,
		) shouldBe 0f
	}

	@Test
	fun scrollsForwardJustEnoughWhenItemExtendsPastViewportEnd() {
		calculateMinimalScrollDelta(
			itemStart = 400,
			itemSize = 200,
			viewportStart = 0,
			viewportEnd = 500,
		) shouldBe 100f
	}

	@Test
	fun scrollsBackwardJustEnoughWhenItemStartsBeforeViewportStart() {
		calculateMinimalScrollDelta(
			itemStart = -45,
			itemSize = 200,
			viewportStart = 0,
			viewportEnd = 500,
		) shouldBe -45f
	}

	@Test
	fun clampsForwardScrollSoOversizedItemAlignsToViewportStart() {
		calculateMinimalScrollDelta(
			itemStart = 120,
			itemSize = 600,
			viewportStart = 0,
			viewportEnd = 500,
		) shouldBe 120f
	}

	@Test
	fun returnsZeroWhenViewportOrItemSizeIsNonPositive() {
		calculateMinimalScrollDelta(
			itemStart = 100,
			itemSize = 200,
			viewportStart = 0,
			viewportEnd = 0,
		) shouldBe 0f
		calculateMinimalScrollDelta(
			itemStart = 100,
			itemSize = 0,
			viewportStart = 0,
			viewportEnd = 500,
		) shouldBe 0f
	}
}
