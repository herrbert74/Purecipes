package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientLineParserTest {

	@Test
	fun `parses optional prefix`() {
		IngredientLineParser.parseLine("optional: parsley, to garnish") shouldBe listOf(
			RecipeIngredient(
				text = "parsley, to garnish",
				requirement = IngredientRequirement.OPTIONAL,
			),
		)
	}

	@Test
	fun `parses optional parenthetical`() {
		IngredientLineParser.parseLine("1 tbsp honey (optional)") shouldBe listOf(
			RecipeIngredient(
				text = "1 tbsp honey",
				requirement = IngredientRequirement.OPTIONAL,
			),
		)
	}

	@Test
	fun `parses to serve suffix as optional`() {
		IngredientLineParser.parseLine("olive oil, plus extra to serve") shouldBe listOf(
			RecipeIngredient(
				text = "olive oil, plus extra to serve",
				requirement = IngredientRequirement.OPTIONAL,
			),
		)
	}

	@Test
	fun `required ingredient stays required`() {
		IngredientLineParser.parseLine("2 chicken thighs") shouldBe listOf(
			RecipeIngredient(
				text = "2 chicken thighs",
				requirement = IngredientRequirement.REQUIRED,
			),
		)
	}

	@Test
	fun `parses alternative ingredients on one line`() {
		IngredientLineParser.parseLine("parsley or tarragon") shouldBe listOf(
			RecipeIngredient(text = "parsley", requirement = IngredientRequirement.ALTERNATIVE),
			RecipeIngredient(text = "tarragon", requirement = IngredientRequirement.ALTERNATIVE),
		)
	}

	@Test
	fun `parses alternative ingredients with shared quantity prefix`() {
		IngredientLineParser.parseLine("2 tbsp parsley or fresh tarragon") shouldBe listOf(
			RecipeIngredient(text = "2 tbsp parsley", requirement = IngredientRequirement.ALTERNATIVE),
			RecipeIngredient(text = "2 tbsp fresh tarragon", requirement = IngredientRequirement.ALTERNATIVE),
		)
	}

	@Test
	fun `parses alternative ingredients with shared of prefix`() {
		IngredientLineParser.parseLine("small bunch of parsley or tarragon (about 30g)") shouldBe listOf(
			RecipeIngredient(
				text = "small bunch of parsley (about 30g)",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
			RecipeIngredient(
				text = "small bunch of tarragon (about 30g)",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
		)
	}

	@Test
	fun `parseLines assigns alternative group keys per line`() {
		IngredientLineParser.parseLines(
			listOf(
				"parsley or tarragon",
				"2 chicken thighs",
				"basil or mint",
			),
		) shouldBe listOf(
			RecipeIngredient(
				text = "parsley",
				requirement = IngredientRequirement.ALTERNATIVE,
				alternativeGroupKey = 1,
			),
			RecipeIngredient(
				text = "tarragon",
				requirement = IngredientRequirement.ALTERNATIVE,
				alternativeGroupKey = 1,
			),
			RecipeIngredient(
				text = "2 chicken thighs",
				requirement = IngredientRequirement.REQUIRED,
			),
			RecipeIngredient(
				text = "basil",
				requirement = IngredientRequirement.ALTERNATIVE,
				alternativeGroupKey = 2,
			),
			RecipeIngredient(
				text = "mint",
				requirement = IngredientRequirement.ALTERNATIVE,
				alternativeGroupKey = 2,
			),
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

	@Test
	fun `toEditableLines joins alternative ingredients`() {
		IngredientLineParser.toEditableLines(
			listOf(
				RecipeIngredient(
					text = "parsley",
					requirement = IngredientRequirement.ALTERNATIVE,
					alternativeGroupKey = 1,
				),
				RecipeIngredient(
					text = "tarragon",
					requirement = IngredientRequirement.ALTERNATIVE,
					alternativeGroupKey = 1,
				),
				RecipeIngredient(text = "salt"),
			),
		) shouldBe listOf(
			"parsley or tarragon",
			"salt",
		)
	}

	@Test
	fun `keeps packaging and informal measurement alternatives on a single line`() {
		IngredientLineParser.parseLine("400-500 g can or jar butter beans drained") shouldBe listOf(
			RecipeIngredient(
				text = "400-500 g can or jar butter beans drained",
				requirement = IngredientRequirement.REQUIRED,
			),
		)
		IngredientLineParser.parseLine("Splash or two sesame oil") shouldBe listOf(
			RecipeIngredient(
				text = "Splash or two sesame oil",
				requirement = IngredientRequirement.REQUIRED,
			),
		)
	}

	@Test
	fun `splits ingredient alternative after homemade or store-bought`() {
		IngredientLineParser.parseLine(
			"2 quarts homemade or store-bought low-sodium chicken stock or vegetable stock",
		) shouldBe listOf(
			RecipeIngredient(
				text = "2 quarts homemade or store-bought low-sodium chicken stock",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
			RecipeIngredient(
				text = "2 quarts vegetable stock",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
		)
	}

	@Test
	fun `does not copy non-unit noun when expanding quantity prefix`() {
		IngredientLineParser.parseLine("1 kaiser or bulkie roll (see notes)") shouldBe listOf(
			RecipeIngredient(
				text = "1 kaiser (see notes)",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
			RecipeIngredient(
				text = "1 bulkie roll (see notes)",
				requirement = IngredientRequirement.ALTERNATIVE,
			),
		)
	}
}
