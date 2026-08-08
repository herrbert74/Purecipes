package app.purecipes.feature.newrecipe.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientRowComposerTest {

	@Test
	fun `compose line joins amount unit and name`() {
		IngredientRowComposer.composeLine(
			IngredientRowInput(
				primary = IngredientPartInput(amount = "200", unit = "g", name = "pasta"),
			),
		) shouldBe "200 g pasta"
	}

	@Test
	fun `compose line prefixes optional and joins alternatives`() {
		IngredientRowComposer.composeLine(
			IngredientRowInput(
				primary = IngredientPartInput(name = "parsley"),
				isOptional = true,
				alternatives = listOf(IngredientPartInput(name = "tarragon")),
			),
		) shouldBe "optional: parsley or tarragon"
	}

	@Test
	fun `split part keeps food words in the name`() {
		IngredientRowComposer.splitPart("2 tomatoes") shouldBe IngredientPartInput(
			amount = "2",
			name = "tomatoes",
		)
	}

	@Test
	fun `split part extracts amount unit and name`() {
		IngredientRowComposer.splitPart("200 g pasta") shouldBe IngredientPartInput(
			amount = "200",
			unit = "g",
			name = "pasta",
		)
	}

	@Test
	fun `from editable line round trips optional alternatives`() {
		val row = IngredientRowComposer.fromEditableLine("optional: parsley or tarragon")
		row.isOptional shouldBe true
		row.primary.name shouldBe "parsley"
		row.alternatives.single().name shouldBe "tarragon"
		IngredientRowComposer.composeLine(row) shouldBe "optional: parsley or tarragon"
	}

	@Test
	fun `from paste text creates one row per line`() {
		val rows = IngredientRowComposer.fromPasteText("200 g pasta\n2 tomatoes")
		rows.size shouldBe 2
		IngredientRowComposer.toLines(rows) shouldBe listOf("200 g pasta", "2 tomatoes")
	}

	@Test
	fun `isAllowedAmountInput accepts decimals and fractions`() {
		IngredientRowComposer.isAllowedAmountInput("") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("2") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("1.5") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("1,5") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("1/2") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("1 1/2") shouldBe true
		IngredientRowComposer.isAllowedAmountInput("2 cups") shouldBe false
		IngredientRowComposer.isAllowedAmountInput("abc") shouldBe false
	}
}
