package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionLookupIndexTest {

	@Test
	fun findFoodDoesNotUseStringPrefixMatches() {
		val index = lookupIndex(
			food(
				id = 1,
				displayName = "Butterbur, raw",
				normalizedName = "butterbur raw",
			),
			food(
				id = 2,
				displayName = "Vegetable oil-butter spread",
				normalizedName = "vegetable oil butter spread",
			),
		)

		index.findFood("butter").shouldBeNull()
		index.findFood("vegetable oil").shouldBeNull()
	}

	@Test
	fun findFoodMatchesWholeTokensAndStripsPreparationWords() {
		val index = lookupIndex(
			food(
				id = 10,
				displayName = "Oil, olive, extra virgin",
				normalizedName = "oil olive extra virgin",
			),
			food(
				id = 11,
				displayName = "Onions, yellow, raw",
				normalizedName = "onions yellow raw",
			),
		)

		val oliveOil = index.findFood("olive oil")
		oliveOil.shouldNotBeNull()
		oliveOil.foodId shouldBe 10
		oliveOil.matchSource shouldBe "tokens"

		val choppedOnions = index.findFood("chopped onions")
		choppedOnions.shouldNotBeNull()
		choppedOnions.foodId shouldBe 11
	}

	@Test
	fun findFoodPrefersAliasOverTokenScore() {
		val index = NutritionLookupIndex(
			foodById = mapOf(
				1 to food(id = 1, displayName = "Sugars, granulated", normalizedName = "sugars granulated"),
				2 to food(id = 2, displayName = "Sugar, brown", normalizedName = "sugar brown"),
			),
			foodIdByNormalizedAlias = mapOf("sugar" to 1),
			measuresByFoodId = emptyMap(),
		)

		val match = index.findFood("sugar")
		match.shouldNotBeNull()
		match.foodId shouldBe 1
		match.matchSource shouldBe "alias"
	}

	@Test
	fun findFoodUsesSeedAliasWhenTokenScoringWouldRejectSoup() {
		val broth = food(
			id = 20,
			displayName = "Soup, chicken broth, ready-to-serve",
			normalizedName = "soup chicken broth ready to serve",
		)
		val index = NutritionLookupIndex(
			foodById = mapOf(20 to broth),
			foodIdByNormalizedAlias = NutritionSeedAliasIndex.merge(listOf(broth), emptyMap()),
			measuresByFoodId = emptyMap(),
		)

		val match = index.findFood("chicken broth")
		match.shouldNotBeNull()
		match.foodId shouldBe 20
		match.matchSource shouldBe "alias"
	}

	@Test
	fun findFoodUsesUkSpellingSeedAlias() {
		val chili = food(
			id = 21,
			displayName = "Spices, pepper, red or cayenne",
			normalizedName = "spices pepper red or cayenne",
		)
		val index = NutritionLookupIndex(
			foodById = mapOf(21 to chili),
			foodIdByNormalizedAlias = NutritionSeedAliasIndex.merge(listOf(chili), emptyMap()),
			measuresByFoodId = emptyMap(),
		)

		val match = index.findFood("Chilli Flakes")
		match.shouldNotBeNull()
		match.foodId shouldBe 21
		match.matchSource shouldBe "alias"
	}

	@Test
	fun findFoodPrefersFoundationOverBrandedTokenMatch() {
		val foundation = food(
			id = 30,
			displayName = "Cheese, mozzarella, whole milk",
			normalizedName = "cheese mozzarella whole milk",
			sourceName = FDC_FOUNDATION_SOURCE_NAME,
		)
		val branded = food(
			id = 31,
			displayName = "MOZZARELLA CHEESE",
			normalizedName = "mozzarella cheese",
			sourceName = FDC_BRANDED_SOURCE_NAME,
		)
		val index = lookupIndex(foundation, branded)

		val match = index.findFood("mozzarella")
		match.shouldNotBeNull()
		match.foodId shouldBe 30
	}

	private fun lookupIndex(vararg foods: NutritionFoodRecord): NutritionLookupIndex =
		NutritionLookupIndex(
			foodById = foods.associateBy { food -> food.id },
			foodIdByNormalizedAlias = emptyMap(),
			measuresByFoodId = emptyMap(),
		)

	private fun food(
		id: Int,
		displayName: String,
		normalizedName: String,
		sourceName: String = FDC_FOUNDATION_SOURCE_NAME,
	): NutritionFoodRecord =
		NutritionFoodRecord(
			id = id,
			displayName = displayName,
			normalizedName = normalizedName,
			sourceName = sourceName,
			nutrients = FdcNutrientsPer100g(
				calories = BigDecimal.ZERO,
				protein = null,
				carbohydrates = null,
				fat = null,
				fiber = null,
				sugar = null,
				sodium = null,
			),
		)
}
