package app.purecipes.feature.home.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HomeFeedRowsTest {

	@Test
	fun featureRequestFollowsFourShelvesWhenTheFeedIsLonger() {
		homeFeedFeatureRequestRow(shelfCount = 6) shouldBe HOME_FEATURE_REQUEST_AFTER_SHELF_COUNT
		homeFeedShelfIndex(row = 3, shelfCount = 6) shouldBe 3
		homeFeedShelfIndex(row = 4, shelfCount = 6) shouldBe null
		homeFeedShelfIndex(row = 5, shelfCount = 6) shouldBe 4
	}

	@Test
	fun featureRequestFollowsTheLastShelfWhenTheFeedIsShorter() {
		homeFeedFeatureRequestRow(shelfCount = 2) shouldBe 2
		homeFeedShelfIndex(row = 0, shelfCount = 2) shouldBe 0
		homeFeedShelfIndex(row = 1, shelfCount = 2) shouldBe 1
		homeFeedShelfIndex(row = 2, shelfCount = 2) shouldBe null
	}

	@Test
	fun featureRequestIsFirstWhenThereAreNoShelves() {
		homeFeedFeatureRequestRow(shelfCount = 0) shouldBe 0
		homeFeedShelfIndex(row = 0, shelfCount = 0) shouldBe null
	}
}
