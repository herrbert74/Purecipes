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
	fun `horizontal shelf inserts a card ad after five recipes then every twenty four`() {
		val firstPage = List(12) { index -> index }
		inlineShelfLabels(firstPage) shouldBe listOf(
			0, 1, 2, 3, 4, "ad", 5, 6, 7, 8, 9, 10, 11,
		)

		val secondInterval = List(30) { index -> index }
		val labels = inlineShelfLabels(secondInterval)
		labels.filter { label -> label == "ad" }.size shouldBe 2
		labels[5] shouldBe "ad"
		labels.indexOfLast { label -> label == "ad" } shouldBe 30
	}

	@Test
	fun `shelf ads stay out when ads are off or five recipes are showing`() {
		inlineFeedEntries(
			items = List(5) { index -> index },
			includeAds = true,
			shouldInsertAd = InlineAdPlacement::shouldInsertShelfAdBeforeContentIndex,
		).filterIsInstance<InlineFeedEntry.Ad>() shouldBe emptyList()
		inlineFeedEntries(
			items = listOf(1, 2, 3),
			includeAds = false,
			shouldInsertAd = InlineAdPlacement::shouldInsertShelfAdBeforeContentIndex,
		) shouldBe listOf(
			InlineFeedEntry.Content(1),
			InlineFeedEntry.Content(2),
			InlineFeedEntry.Content(3),
		)
	}

	@Test
	fun `ad list positions are three fifteen and twenty seven`() {
		InlineAdPlacement.isAdListPosition(3) shouldBe true
		InlineAdPlacement.isAdListPosition(15) shouldBe true
		InlineAdPlacement.isAdListPosition(27) shouldBe true
		InlineAdPlacement.isAdListPosition(4) shouldBe false
		InlineAdPlacement.isAdListPosition(14) shouldBe false
	}

	private fun inlineShelfLabels(recipes: List<Int>): List<Any> = inlineFeedEntries(
		items = recipes,
		includeAds = true,
		shouldInsertAd = InlineAdPlacement::shouldInsertShelfAdBeforeContentIndex,
	).map { entry ->
		when (entry) {
			is InlineFeedEntry.Content -> entry.item
			InlineFeedEntry.Ad -> "ad"
		}
	}
}
