package app.purecipes.shared.domain.ingredient

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientNameMatchingTest {

	@Test
	fun `matches sweetcorn with corn alias`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "200g sweetcorn",
			ingredientNames = listOf("Corn"),
		) shouldBe true
	}

	@Test
	fun `matches spring onion with green onion alias`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "2 spring onions",
			ingredientNames = listOf("Green Onion"),
		) shouldBe true
	}

	@Test
	fun `matches descriptive plural banana line`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "3 medium very ripe bananas (about 13 oz.), peeled, flesh mashed",
			ingredientNames = listOf("Banana"),
		) shouldBe true
	}

	@Test
	fun `matches descriptive plural carrot line`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "2 large carrots, peeled, sliced 1/2 inch thick on a deep diagonal",
			ingredientNames = listOf("Carrot"),
		) shouldBe true
	}

	@Test
	fun `matches typo corainder with coriander alias`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "2 tbsp. corainder",
			ingredientNames = listOf("Coriander"),
		) shouldBe true
	}

	@Test
	fun `matches bayleaves with bay leaf alias`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "2 small bayleaves",
			ingredientNames = listOf("Bay Leaf"),
		) shouldBe true
	}

	@Test
	fun `matches creme fraiche accent variants`() {
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = "120ml creme fraiche",
			ingredientNames = listOf("Crème Fraîche"),
		) shouldBe true
	}

	@Test
	fun `matches aubergine line with eggplant pantry name`() {
		IngredientNameMatching.isCoveredByAvailableIngredients(
			ingredientLine = "1 large aubergine, diced",
			availableIngredients = listOf("Eggplant"),
		) shouldBe true
	}

	@Test
	fun `matches courgette line with zucchini pantry name`() {
		IngredientNameMatching.isCoveredByAvailableIngredients(
			ingredientLine = "2 courgettes, sliced",
			availableIngredients = listOf("Zucchini"),
		) shouldBe true
	}

	@Test
	fun `matches cilantro line with coriander pantry name`() {
		IngredientNameMatching.isCoveredByAvailableIngredients(
			ingredientLine = "1/2 cup chopped cilantro",
			availableIngredients = listOf("Coriander"),
		) shouldBe true
	}

	@Test
	fun `catalogueAliasSiblings returns prawns for shrimp`() {
		IngredientNameMatching.catalogueAliasSiblings(
			ingredientName = "Shrimp",
			catalogueItems = setOf("Prawns", "Shrimp", "Salmon"),
		) shouldBe setOf("Prawns")
	}

	@Test
	fun `catalogueAliasSiblings returns empty when catalogue lacks alias names`() {
		IngredientNameMatching.catalogueAliasSiblings(
			ingredientName = "Aubergine",
			catalogueItems = setOf("Aubergine", "Zucchini", "Tomato"),
		) shouldBe emptySet()
	}

	@Test
	fun `catalogueAliasSiblingsIndex matches per item lookup`() {
		val catalogueItems = setOf("Prawns", "Shrimp", "Salmon", "Aubergine", "Zucchini", "Tomato")
		val index = IngredientNameMatching.catalogueAliasSiblingsIndex(catalogueItems)

		index.keys shouldBe catalogueItems
		catalogueItems.forEach { ingredientName ->
			index.getValue(ingredientName) shouldBe IngredientNameMatching.catalogueAliasSiblings(
				ingredientName = ingredientName,
				catalogueItems = catalogueItems,
			)
		}
	}
}
