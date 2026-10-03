package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class FdcNeededFoodAccumulatorTest {

	@Test
	fun brandedImportSkipsFlavorWordsOnSparklingWater() {
		val accumulator = FdcNeededFoodAccumulator(setOf("lemon zest", "cold water"))
		accumulator.consider(
			brandedFood(
				fdcId = 1L,
				description = "LEMON ZEST SPARKLING NATURAL MINERAL WATER, LEMON ZEST",
			),
		)
		accumulator.consider(
			brandedFood(
				fdcId = 2L,
				description = "COLD WATER LOBSTER TAILS",
			),
		)

		accumulator.neededNameMatches() shouldBe emptyMap()
	}

	private fun brandedFood(fdcId: Long, description: String): FdcFoundationFood =
		FdcFoundationFood(
			sourceName = FDC_BRANDED_SOURCE_NAME,
			fdcId = fdcId,
			description = description,
			nutrients = listOf(
				FdcNutrientAmount(
					nutrientId = FdcNutrientIds.ENERGY_KCAL,
					amount = BigDecimal.ZERO,
				),
			),
			portions = emptyList(),
		)
}
