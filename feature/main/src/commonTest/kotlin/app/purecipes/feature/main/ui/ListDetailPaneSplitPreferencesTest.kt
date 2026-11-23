package app.purecipes.feature.main.ui

import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class ListDetailPaneSplitPreferencesTest {

	@Test
	fun defaultsToBalancedDetailAnchorIndex() {
		val preferences = ListDetailPaneSplitPreferences(preferencesKey = uniqueKey())

		preferences.preferredDetailAnchorIndex(anchorCount = TEST_ANCHOR_COUNT) shouldBe DEFAULT_DETAIL_ANCHOR_INDEX
	}

	@Test
	fun persistsPreferredDetailAnchorIndexAcrossInstances() {
		val preferencesKey = uniqueKey()
		val firstInstance = ListDetailPaneSplitPreferences(preferencesKey = preferencesKey)
		val secondInstance = ListDetailPaneSplitPreferences(preferencesKey = preferencesKey)

		firstInstance.setPreferredDetailAnchorIndex(index = 0, anchorCount = TEST_ANCHOR_COUNT)

		secondInstance.preferredDetailAnchorIndex(anchorCount = TEST_ANCHOR_COUNT) shouldBe 0
	}

	@Test
	fun clampsStoredAnchorIndexToValidRange() {
		val preferencesKey = uniqueKey()
		val preferences = ListDetailPaneSplitPreferences(preferencesKey = preferencesKey)

		preferences.setPreferredDetailAnchorIndex(index = 99, anchorCount = TEST_ANCHOR_COUNT)
		preferences.preferredDetailAnchorIndex(anchorCount = TEST_ANCHOR_COUNT) shouldBe TEST_ANCHOR_COUNT - 1

		preferences.setPreferredDetailAnchorIndex(index = -3, anchorCount = TEST_ANCHOR_COUNT)
		preferences.preferredDetailAnchorIndex(anchorCount = TEST_ANCHOR_COUNT) shouldBe 0
	}

	private fun uniqueKey(): String = "layout.detail_split_anchor_index.test.${Random.nextInt()}"

	private companion object {

		const val TEST_ANCHOR_COUNT = 3
	}
}
