package app.purecipes.backend.tools

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PinnedIngredientIndexTest {

	private val samplePinnedRows = listOf(
		IngredientNameSnapshotRow(
			lookupName = "kale",
			status = "pinned",
			pantryItem = "Kale",
			fdcSource = "fdc_foundation",
			fdcId = "168421",
			fdcDescription = "Kale, raw",
			matchSource = "pinned",
			lineCount = 12,
		),
		IngredientNameSnapshotRow(
			lookupName = "sirloin steak",
			status = "pinned",
			pantryItem = "Beef",
			fdcSource = "fdc_foundation",
			fdcId = "170281",
			fdcDescription = "Beef, top sirloin steak",
			matchSource = "pinned",
			lineCount = 8,
		),
		IngredientNameSnapshotRow(
			lookupName = "greek yoghurt",
			status = "pinned",
			pantryItem = "Greek Yoghurt",
			fdcSource = "fdc_foundation",
			fdcId = "170903",
			fdcDescription = "Yogurt, Greek, plain, whole milk",
			matchSource = "pinned",
			lineCount = 15,
		),
		IngredientNameSnapshotRow(
			lookupName = "olive",
			status = "pinned",
			pantryItem = "Olive",
			fdcSource = "fdc_foundation",
			fdcId = "110300",
			fdcDescription = "Olives, pickled",
			matchSource = "pinned",
			lineCount = 5,
		),
		IngredientNameSnapshotRow(
			lookupName = "auto cabbage",
			status = "auto",
			pantryItem = "Cabbage",
			fdcSource = "",
			fdcId = "",
			fdcDescription = "",
			matchSource = "",
			lineCount = 1,
		),
	)

	@Test
	fun indexFiltersToOnlyPinnedRows() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		index.size shouldBe 4
	}

	@Test
	fun exactMatchReturnsHighestScore() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		val match = index.findBestNeighbor("kale").shouldNotBeNull()
		match.row.lookupName shouldBe "kale"
		match.similarity shouldBe 1.0
		match.method shouldBe "pinned_lexical"
	}

	@Test
	fun variationMatchesClosestPinnedNeighbor() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		val match = index.findBestNeighbor("curly kale").shouldNotBeNull()
		match.row.lookupName shouldBe "kale"
		match.row.pantryItem shouldBe "Kale"
		match.row.fdcId shouldBe "168421"
		match.similarity shouldBeGreaterThan 0.60
	}

	@Test
	fun synonymExpandsAndMatchesNeighbor() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		val match = index.findBestNeighbor("greek yogurt").shouldNotBeNull()
		match.row.lookupName shouldBe "greek yoghurt"
		match.row.pantryItem shouldBe "Greek Yoghurt"
		match.similarity shouldBeGreaterThan 0.70
	}

	@Test
	fun headNounMismatchPenalizesFalseCandidates() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		val match = index.findBestNeighbor("olive oil", minScore = 0.60)
		match?.row?.lookupName shouldBe null
	}

	@Test
	fun mockEmbedderUsesKnnMethodAndScores() {
		val mockEmbedder = IngredientTextEmbedder { texts ->
			Array(texts.size) { FloatArray(512) { 0.5f } }
		}
		val index = PinnedIngredientIndex(samplePinnedRows, mockEmbedder)
		val match = index.findBestNeighbor("curly kale")
		match?.row?.lookupName shouldBe "kale"
		match?.method shouldBe "pinned_knn"
	}

	@Test
	fun emptyQueryReturnsEmpty() {
		val index = PinnedIngredientIndex(samplePinnedRows)
		index.findNearestNeighbors("").shouldBeEmpty()
		index.findNearestNeighbors("   ").shouldBeEmpty()
	}
}
