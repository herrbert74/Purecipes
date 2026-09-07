package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NutritionNameNormalizerTest {

	@Test
	fun forLookupStripsPreparationWordsAndInstructionClauses() {
		NutritionNameNormalizer.forLookup("freshly ground black pepper") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup(
			"Diamond Crystal kosher salt; for table salt, use half as much by volume",
		) shouldBe "diamond crystal kosher salt"
		NutritionNameNormalizer.forLookup("olive oil, divided") shouldBe "olive oil"
		NutritionNameNormalizer.forLookup("unsalted butter, melted") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("unsalted butter, room temperature") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("homemade chicken stock") shouldBe "chicken stock"
		NutritionNameNormalizer.forLookup("eggs, beaten") shouldBe "eggs"
	}
}
