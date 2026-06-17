package app.purecipes.backend.feature.search

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientVocabularyTest {

	@Test
	fun `marks section heading lines as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("For the topping and salsa") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Dough:") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Spicy BBQ aioli:") shouldBe true
	}

	@Test
	fun `marks optional prefix lines as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("optional parsley, to garnish") shouldBe true
	}

	@Test
	fun `treats default pantry staples as covered`() {
		IngredientVocabulary.isCoveredByAvailableIngredients(
			ingredientLine = "salt and pepper",
			availableIngredients = emptyList(),
		) shouldBe true
	}
}
