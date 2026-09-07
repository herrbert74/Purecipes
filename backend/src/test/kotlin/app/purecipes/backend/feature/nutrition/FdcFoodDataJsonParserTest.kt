package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File
import java.math.BigDecimal
import kotlin.test.Test

class FdcFoodDataJsonParserTest {

	@Test
	fun parseReadsFoundationFoodsDataset() {
		val sampleFile = File("src/test/resources/nutrition/foundation_food_sample.json")
		val parseResult = FdcFoodDataJsonParser.parse(sampleFile)

		parseResult.dataset shouldBe FdcFoodDataset.FOUNDATION
		parseResult.foods shouldHaveSize 2
		val sugar = parseResult.foods.first { it.description == "Sugars, granulated" }
		sugar.sourceName shouldBe FDC_FOUNDATION_SOURCE_NAME
		sugar.nutrientsPer100g() shouldNotBe null
		sugar.portions.map { it.measureName } shouldBe listOf("cup", "tsp")
	}

	@Test
	fun parseReadsSrLegacyFoodsDataset() {
		val sampleFile = File("src/test/resources/nutrition/sr_legacy_food_sample.json")
		val parseResult = FdcFoodDataJsonParser.parse(sampleFile)

		parseResult.dataset shouldBe FdcFoodDataset.SR_LEGACY
		parseResult.foods.single().sourceName shouldBe FDC_SR_LEGACY_SOURCE_NAME
		val pasta = parseResult.foods.single()
		pasta.portions.map { portion -> portion.measureName } shouldBe listOf("cup", "tbsp")
		pasta.portions.map { portion -> portion.gramsPerMeasure } shouldBe listOf(
			BigDecimal("100.0000"),
			BigDecimal("15.0000"),
		)
	}
}
