package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.File
import kotlin.test.Test

class FdcFoodMatcherTest {

	private val foods = FdcFoodDataJsonParser.parse(
		File("src/test/resources/nutrition/foundation_food_sample.json"),
	).foods

	@Test
	fun matchCatalogueNameUsesPreferredDescriptionOverride() {
		val match = FdcFoodMatcher.matchCatalogueName("Sugar", foods)
		match?.description shouldBe "Sugars, granulated"
	}

	@Test
	fun matchAliasResolvesCasterSugar() {
		val match = FdcFoodMatcher.matchAlias("caster sugar", foods)
		match?.description shouldBe "Sugars, granulated"
	}

	@Test
	fun matchAliasResolvesCornflourWhenPreferredFoodIsPresent() {
		val cornstarch = FdcFoundationFood(
			sourceName = FDC_SR_LEGACY_SOURCE_NAME,
			fdcId = 20L,
			description = "Cornstarch",
			nutrients = emptyList(),
			portions = emptyList(),
		)

		FdcFoodMatcher.matchAlias("cornflour", listOf(cornstarch) + foods)?.description shouldBe "Cornstarch"
	}

	@Test
	fun matchCatalogueNameFindsEggs() {
		val match = FdcFoodMatcher.matchCatalogueName("Eggs", foods)
		match shouldNotBe null
		match?.description shouldBe "Eggs, Grade A, Large, egg whole"
	}

	@Test
	fun matchSearchTermDoesNotChoosePrefixFalsePositives() {
		val butter = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 10L,
			description = "Butter, stick, unsalted",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val butterbur = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 11L,
			description = "Butterbur, raw",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val oilSpread = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 12L,
			description = "Vegetable oil-butter spread, stick, salted",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val chickenBreast = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 13L,
			description = "Chicken, breast, boneless, skinless, raw",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val chickenSoup = FdcFoundationFood(
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
			fdcId = 14L,
			description = "Soup, chicken broth, ready-to-serve",
			nutrients = emptyList(),
			portions = emptyList(),
		)

		FdcFoodMatcher.matchCatalogueName("Butter", listOf(butterbur)) shouldBe null
		FdcFoodMatcher.matchCatalogueName("Butter", listOf(butter, butterbur))?.description shouldBe
			"Butter, stick, unsalted"
		FdcFoodMatcher.matchCatalogueName("Vegetable Oil", listOf(oilSpread)) shouldBe null
		FdcFoodMatcher.matchCatalogueName(
			"Chicken Breast",
			listOf(chickenBreast, chickenSoup),
		)?.description shouldBe "Chicken, breast, boneless, skinless, raw"
	}

	@Test
	fun matchSearchTermPrefersHigherScoreOverLongerCookedName() {
		val raw = FdcFoundationFood(
			sourceName = FDC_SR_LEGACY_SOURCE_NAME,
			fdcId = 15L,
			description = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			nutrients = emptyList(),
			portions = emptyList(),
		)
		val braised = FdcFoundationFood(
			sourceName = FDC_SR_LEGACY_SOURCE_NAME,
			fdcId = 16L,
			description = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, " +
				"with added solution, cooked, braised",
			nutrients = emptyList(),
			portions = emptyList(),
		)

		FdcFoodMatcher.matchCatalogueName(
			"boneless, skinless chicken breasts",
			listOf(braised, raw),
		)?.description shouldBe raw.description
	}
}
