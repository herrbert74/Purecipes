package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientLineParserTest {

	@Test
	fun `parses optional prefix`() {
		IngredientLineParser.parse("optional: parsley, to garnish") shouldBe RecipeIngredient(
			text = "parsley, to garnish",
			requirement = IngredientRequirement.OPTIONAL,
		)
	}

	@Test
	fun `parses optional parenthetical`() {
		IngredientLineParser.parse("1 tbsp honey (optional)") shouldBe RecipeIngredient(
			text = "1 tbsp honey",
			requirement = IngredientRequirement.OPTIONAL,
		)
	}

	@Test
	fun `parses to serve suffix as optional`() {
		IngredientLineParser.parse("olive oil, plus extra to serve") shouldBe RecipeIngredient(
			text = "olive oil, plus extra to serve",
			requirement = IngredientRequirement.OPTIONAL,
		)
	}

	@Test
	fun `required ingredient stays required`() {
		IngredientLineParser.parse("2 chicken thighs") shouldBe RecipeIngredient(
			text = "2 chicken thighs",
			requirement = IngredientRequirement.REQUIRED,
		)
	}

	@Test
	fun `toEditableLine prefixes optional ingredients`() {
		IngredientLineParser.toEditableLine(
			RecipeIngredient(
				text = "parsley",
				requirement = IngredientRequirement.OPTIONAL,
			),
		) shouldBe "optional: parsley"
	}
}
