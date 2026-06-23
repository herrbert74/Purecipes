package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientCatalogue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientLookupTest {

	@Test
	fun `resolveCatalogueIngredient returns exact catalogue name`() {
		IngredientLookup.resolveCatalogueIngredient(
			query = "Zucchini",
			catalogueItems = IngredientCatalogue.allItems,
		) shouldBe "Zucchini"
	}

	@Test
	fun `resolveCatalogueIngredient resolves alias to catalogue name`() {
		IngredientLookup.resolveCatalogueIngredient(
			query = "courgette",
			catalogueItems = IngredientCatalogue.allItems,
		) shouldBe "Zucchini"
	}

	@Test
	fun `resolveCatalogueIngredient returns null for unknown ingredient`() {
		IngredientLookup.resolveCatalogueIngredient(
			query = "gochujang",
			catalogueItems = IngredientCatalogue.allItems,
		) shouldBe null
	}

	@Test
	fun `classifyIngredientMatches returns exact match for catalogue name`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "tarragon",
			vocabulary = listOf("Tarragon", "Parsley"),
		)

		matches.exactMatches.map { it.ingredient } shouldContainExactly listOf("Tarragon")
		matches.likelyMatches.shouldBeEmpty()
	}

	@Test
	fun `classifyIngredientMatches returns likely match for typo`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "tarragone",
			vocabulary = listOf("Tarragon", "Parsley"),
		)

		matches.exactMatches.shouldBeEmpty()
		matches.likelyMatches.map { it.ingredient } shouldContainExactly listOf("Tarragon")
		matches.likelyMatches.single().confidence.shouldBeGreaterThan(0.8)
	}

	@Test
	fun `classifyIngredientMatches treats known alias typo as exact`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "corainder",
			vocabulary = listOf("Coriander", "Parsley"),
		)

		matches.exactMatches.map { it.ingredient } shouldContainExactly listOf("Coriander")
		matches.likelyMatches.shouldBeEmpty()
	}

	@Test
	fun `classifyIngredientMatches can return both exact and likely tiers`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "taragon",
			vocabulary = listOf("Taragon", "Tarragon"),
		)

		matches.exactMatches.map { it.ingredient } shouldContainExactly listOf("Taragon")
		matches.likelyMatches.map { it.ingredient } shouldContainExactly listOf("Tarragon")
	}

	@Test
	fun `findLikelyIngredientMatches excludes exact matches`() {
		IngredientLookup.findLikelyIngredientMatches(
			query = "tarragon",
			vocabulary = listOf("Tarragon"),
		).shouldBeEmpty()
	}

	@Test
	fun `classifyIngredientMatches ignores short queries for likely tier`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "ta",
			vocabulary = listOf("Tarragon"),
		)

		matches.exactMatches.shouldBeEmpty()
		matches.likelyMatches.shouldBeEmpty()
	}

	@Test
	fun `classifyIngredientMatches deduplicates vocabulary`() {
		val matches = IngredientLookup.classifyIngredientMatches(
			query = "tarragon",
			vocabulary = listOf("Tarragon", "Tarragon"),
		)

		matches.exactMatches.map { it.ingredient } shouldContainExactly listOf("Tarragon")
	}
}
