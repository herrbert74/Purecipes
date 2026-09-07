package app.purecipes.shared.ui.component

import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionSummary
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NutritionCoverageCopyTest {

	@Test
	fun coverageCopyDescribesCompleteEstimates() {
		NutritionSummary(
			matchedIngredientCount = 4,
			totalIngredientCount = 4,
			isComplete = true,
		).coverageCopy() shouldBe "Complete estimate from all 4 ingredients"
	}

	@Test
	fun coverageCopyDescribesPartialEstimates() {
		NutritionSummary(
			matchedIngredientCount = 2,
			totalIngredientCount = 5,
			isComplete = false,
		).coverageCopy() shouldBe "Partial estimate from 2 of 5 ingredients"
	}

	@Test
	fun coverageCopyDescribesScrapedValuesWithoutCounts() {
		NutritionSummary(
			calculationSource = NutritionCalculationSource.SCRAPED,
		).coverageCopy() shouldBe "Imported nutrition values"
	}

	@Test
	fun usdaAttributionIsHiddenForScrapedNutrition() {
		NutritionSummary(
			calculationSource = NutritionCalculationSource.SCRAPED,
		).shouldShowUsdaAttribution() shouldBe false
		NutritionSummary(
			calculationSource = NutritionCalculationSource.CALCULATED,
		).shouldShowUsdaAttribution() shouldBe true
	}
}
