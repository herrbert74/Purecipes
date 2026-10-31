package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ServingsParserTest {

	@Test
	fun parseReturnsNullForNullOrEmpty() {
		ServingsParser.parse(null) shouldBe null
		ServingsParser.parse("") shouldBe null
		ServingsParser.parse("   ") shouldBe null
	}

	@Test
	fun parseExtractsStandardServingFormats() {
		ServingsParser.parse("4 servings")?.count shouldBe 4.0
		ServingsParser.parse("6 serves")?.count shouldBe 6.0
		ServingsParser.parse("2 portions")?.count shouldBe 2.0
		ServingsParser.parse("10 people")?.count shouldBe 10.0
		ServingsParser.parse("1 serving")?.count shouldBe 1.0
	}

	@Test
	fun parseExtractsItemAndPieceYields() {
		ServingsParser.parse("22 items")?.count shouldBe 22.0
		ServingsParser.parse("24 pieces")?.count shouldBe 24.0
		ServingsParser.parse("10 pancakes")?.count shouldBe 10.0
		ServingsParser.parse("1 pie")?.count shouldBe 1.0
		ServingsParser.parse("1 loaf")?.count shouldBe 1.0
		ServingsParser.parse("9 loaves")?.count shouldBe 9.0
		ServingsParser.parse("1 batch")?.count shouldBe 1.0
	}

	@Test
	fun parsePreservesOriginalDescription() {
		ServingsParser.parse("  4 servings  ")?.description shouldBe "4 servings"
	}

	@Test
	fun parseReturnsNullForVolumeOrNonCountYields() {
		ServingsParser.parse("3 cups") shouldBe null
		ServingsParser.parse("0 cups") shouldBe null
		ServingsParser.parse("0 servings") shouldBe null
	}
}
