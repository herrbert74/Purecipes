package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientCatalogue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CompiledIngredientMatcherTest {

	@Test
	fun compiledMatcherMatchesSingleMultiWordPluralAliasAndDiacriticsIdentically() {
		val pantry = listOf(
			"Tomato",
			"Olive oil",
			"Jalapeño",
			"Cilantro",
			"Bell pepper",
			"Chicken breast",
		)
		val matcher = IngredientNameMatching.compileMatcher(pantry)
		val lines = listOf(
			"2 ripe tomatoes, diced",
			"1 tbsp extra-virgin olive oils",
			"1 fresh jalapeno, sliced",
			"1/4 cup fresh coriander leaves",
			"1 red bell peppers",
			"2 boneless chicken breasts",
			"1 cup heavy cream",
			"1/2 tsp smoked paprika",
			"   ",
		)

		lines.forEach { line ->
			matcher.matchesIngredientLine(line) shouldBe
				IngredientNameMatching.matchesAnyIngredient(line, pantry)
			matcher.isCoveredByAvailable(line) shouldBe
				IngredientNameMatching.isCoveredByAvailableIngredients(line, pantry)
		}
	}

	@Test
	fun compiledMatcherHandlesFullCataloguePantrySelection() {
		val allIngredients = IngredientCatalogue.allItems
		val matcher = IngredientNameMatching.compileMatcher(allIngredients)
		val sampleLines = listOf(
			"2 tbsp olive oil",
			"1 large onion, finely chopped",
			"3 cloves garlic, minced",
			"400g canned chopped tomatoes",
			"200g spaghetti",
			"100g grated parmesan cheese",
			"1 tsp mystery unicorn dust",
		)

		sampleLines.forEach { line ->
			matcher.isCoveredByAvailable(line) shouldBe
				IngredientNameMatching.isCoveredByAvailableIngredients(line, allIngredients)
		}
	}
}
