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
	fun matchCatalogueNameFindsEggs() {
		val match = FdcFoodMatcher.matchCatalogueName("Eggs", foods)
		match shouldNotBe null
		match?.description shouldBe "Eggs, Grade A, Large, egg whole"
	}
}
