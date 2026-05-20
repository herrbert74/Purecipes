package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FdcFoodMatchingSupportTest {

	@Test
	fun mergeForMatchingPrefersFoundationOverSrLegacyForSameFdcId() {
		val foundationFood = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 1L,
			description = "Foundation copy",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val legacyFood = foundationFood.copy(
			sourceName = FDC_SR_LEGACY_SOURCE_NAME,
			description = "Legacy copy",
		)

		val merged = FdcFoodMatchingSupport.mergeForMatching(
			storedFoods = listOf(legacyFood),
			importedFoods = listOf(foundationFood),
		)

		merged.single().sourceName shouldBe FDC_FOUNDATION_SOURCE_NAME
	}
}
