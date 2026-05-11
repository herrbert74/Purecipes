package app.purecipes.backend.feature.search

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientVocabularyTest {

	@Test
	fun `matches sweetcorn with corn alias`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "200g sweetcorn",
			ingredientNames = listOf("Corn"),
		) shouldBe true
	}

	@Test
	fun `matches spring onion with green onion alias`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "2 spring onions",
			ingredientNames = listOf("Green Onion"),
		) shouldBe true
	}

	@Test
	fun `matches descriptive plural banana line`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "3 medium very ripe bananas (about 13 oz.), peeled, flesh mashed",
			ingredientNames = listOf("Banana"),
		) shouldBe true
	}

	@Test
	fun `matches descriptive plural carrot line`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "2 large carrots, peeled, sliced 1/2 inch thick on a deep diagonal",
			ingredientNames = listOf("Carrot"),
		) shouldBe true
	}

	@Test
	fun `marks section heading lines as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("For the topping and salsa") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Dough:") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Spicy BBQ aioli:") shouldBe true
	}

	@Test
	fun `matches typo corainder with coriander alias`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "2 tbsp. corainder",
			ingredientNames = listOf("Coriander"),
		) shouldBe true
	}

	@Test
	fun `matches bayleaves with bay leaf alias`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "2 small bayleaves",
			ingredientNames = listOf("Bay Leaf"),
		) shouldBe true
	}

	@Test
	fun `matches creme fraiche accent variants`() {
		IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = "120ml creme fraiche",
			ingredientNames = listOf("Crème Fraîche"),
		) shouldBe true
	}
}
