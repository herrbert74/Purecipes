package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.MeasurementSystem
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientUnitTokensTest {

	@Test
	fun `isKnownUnit recognizes full metric unit names`() {
		IngredientUnitTokens.isKnownUnit("gram") shouldBe true
		IngredientUnitTokens.isKnownUnit("Grams") shouldBe true
		IngredientUnitTokens.isKnownUnit("liter") shouldBe true
		IngredientUnitTokens.isKnownUnit("litre") shouldBe true
	}

	@Test
	fun `isKnownUnit rejects ingredient names`() {
		IngredientUnitTokens.isKnownUnit("flour") shouldBe false
		IngredientUnitTokens.isKnownUnit("tomatoes") shouldBe false
	}

	@Test
	fun `canonicalUnit normalizes full metric names`() {
		IngredientUnitTokens.canonicalUnit("gram") shouldBe "g"
		IngredientUnitTokens.canonicalUnit("liters") shouldBe "l"
		IngredientUnitTokens.canonicalUnit("tablespoons") shouldBe "tbsp"
	}

	@Test
	fun `metric suggestions include metric weights volumes and count units`() {
		val suggestions = IngredientUnitTokens.suggestedUnits(MeasurementSystem.METRIC)
		suggestions shouldContain "mg"
		suggestions shouldContain "g"
		suggestions shouldContain "kg"
		suggestions shouldContain "ml"
		suggestions shouldContain "l"
		suggestions shouldContain "pinch"
		suggestions shouldNotContain "oz"
		suggestions shouldNotContain "cup"
		suggestions shouldNotContain "tbsp"
	}

	@Test
	fun `imperial suggestions include imperial weights volumes and count units`() {
		val suggestions = IngredientUnitTokens.suggestedUnits(MeasurementSystem.IMPERIAL)
		suggestions shouldContain "tsp"
		suggestions shouldContain "tbsp"
		suggestions shouldContain "cup"
		suggestions shouldContain "oz"
		suggestions shouldContain "lb"
		suggestions shouldContain "pinch"
		suggestions shouldNotContain "g"
		suggestions shouldNotContain "ml"
	}
}
