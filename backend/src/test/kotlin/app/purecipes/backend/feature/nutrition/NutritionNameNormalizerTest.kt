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
		NutritionNameNormalizer.forLookup("lime, juiced") shouldBe "lime"
		NutritionNameNormalizer.forLookup("neutral oil such as canola") shouldBe "neutral oil"
		NutritionNameNormalizer.forLookup("double-concentrated tomato paste") shouldBe "double tomato paste"
		NutritionNameNormalizer.forLookup("chickpeas, drained") shouldBe "chickpeas"
	}

	@Test
	fun forLookupStripsMeasureLeftoversAndLeafStemWords() {
		NutritionNameNormalizer.forLookup("yellow onion about 8 ounces") shouldBe "yellow onion"
		NutritionNameNormalizer.forLookup("unsalted butter ounces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("parsley leaves") shouldBe "parsley"
		NutritionNameNormalizer.forLookup("cilantro leaves and tender stems") shouldBe "cilantro"
		NutritionNameNormalizer.forLookup("bay leaves") shouldBe "bay"
	}

	@Test
	fun forLookupStripsFillerAndInstructionLeftovers() {
		NutritionNameNormalizer.forLookup("diamond crystal kosher salt more taste") shouldBe
			"diamond crystal kosher salt"
		NutritionNameNormalizer.forLookup("black pepper more taste") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup("kosher salt taste") shouldBe "kosher salt"
		NutritionNameNormalizer.forLookup("unsalted butter cut into pieces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("chilled unsalted butter cut into pieces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("eggs separated") shouldBe "eggs"
		NutritionNameNormalizer.forLookup("olive oil for serving") shouldBe "olive oil"
		NutritionNameNormalizer.forLookup("parsley optional") shouldBe "parsley"
		NutritionNameNormalizer.forLookup("flour for dusting surface") shouldBe "flour"
		NutritionNameNormalizer.forLookup("oil for frying") shouldBe "oil"
		NutritionNameNormalizer.forLookup("honey for drizzling") shouldBe "honey"
		NutritionNameNormalizer.forLookup("breadcrumbs for dredging") shouldBe "breadcrumbs"
		NutritionNameNormalizer.forLookup("cilantro for garnish") shouldBe "cilantro"
		NutritionNameNormalizer.forLookup("tomatoes rinsed deseeded") shouldBe "tomatoes"
		NutritionNameNormalizer.forLookup("cold water") shouldBe "water"
	}

	@Test
	fun forLookupKeepsFoodMeaningfulTokens() {
		NutritionNameNormalizer.forLookup("flat leaf parsley") shouldBe "flat leaf parsley"
		NutritionNameNormalizer.forLookup("bay leaf") shouldBe "bay leaf"
		NutritionNameNormalizer.forLookup("black pepper") shouldBe "black pepper"
	}
}
