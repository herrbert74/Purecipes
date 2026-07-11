package app.purecipes.feature.ads.domain

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class InlineAdPlacementTest {

	@Test
	fun `no ads when fewer than two content items`() {
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(0, 0) shouldBe false
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(0, 1) shouldBe false
	}

	@Test
	fun `no ads in the first two list places`() {
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(0, 3) shouldBe false
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(1, 3) shouldBe false
	}

	@Test
	fun `inserts ad at the third fifteenth and twenty seventh list places`() {
		val contentCount = 27
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(2, contentCount) shouldBe true
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(13, contentCount) shouldBe true
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(24, contentCount) shouldBe true
	}

	@Test
	fun `does not insert ads between interval list places`() {
		val contentCount = 20
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(3, contentCount) shouldBe false
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(12, contentCount) shouldBe false
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(14, contentCount) shouldBe false
	}

	@Test
	fun `two items never get an ad because third list place is never reached`() {
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(0, 2) shouldBe false
		InlineAdPlacement.shouldInsertAdBeforeContentIndex(1, 2) shouldBe false
	}

	@Test
	fun `ad list positions are three fifteen and twenty seven`() {
		InlineAdPlacement.isAdListPosition(3) shouldBe true
		InlineAdPlacement.isAdListPosition(15) shouldBe true
		InlineAdPlacement.isAdListPosition(27) shouldBe true
		InlineAdPlacement.isAdListPosition(4) shouldBe false
		InlineAdPlacement.isAdListPosition(14) shouldBe false
	}
}
