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
	fun findFoodMatchesWaterAndLemonZestToGenericFoods() {
		val water = food(
			id = 40,
			displayName = "Water, tap",
			normalizedName = "water tap",
			sourceName = FDC_SURVEY_SOURCE_NAME,
		)
		val creamOfWheat = food(
			id = 41,
			displayName = "Cream of wheat, regular or quick, made with water, no added fat",
			normalizedName = "cream of wheat regular or quick made with water no added fat",
			sourceName = FDC_SURVEY_SOURCE_NAME,
		)
		val lemonPeel = food(
			id = 42,
			displayName = "Lemon peel, raw",
			normalizedName = "lemon peel raw",
			sourceName = FDC_SR_LEGACY_SOURCE_NAME,
		)
		val sparkling = food(
			id = 43,
			displayName = "LEMON ZEST SPARKLING NATURAL MINERAL WATER, LEMON ZEST",
			normalizedName = "lemon zest sparkling natural mineral water lemon zest",
			sourceName = FDC_BRANDED_SOURCE_NAME,
		)
		val lobster = food(
			id = 44,
			displayName = "COLD WATER LOBSTER TAILS",
			normalizedName = "cold water lobster tails",
			sourceName = FDC_BRANDED_SOURCE_NAME,
		)
		val foods = listOf(water, creamOfWheat, lemonPeel, sparkling, lobster)
		val index = NutritionLookupIndex(
			foodById = foods.associateBy { food -> food.id },
			foodIdByNormalizedAlias = NutritionSeedAliasIndex.merge(
				foods,
				mapOf(
					"lemon zest" to sparkling.id,
					"cold water" to lobster.id,
				),
			),
			measuresByFoodId = emptyMap(),
		)

		val waterMatch = index.findFood("water")
		waterMatch.shouldNotBeNull()
		waterMatch.foodId shouldBe water.id
		waterMatch.matchSource shouldBe "alias"

		val coldWater = index.findFood("cold water")
		coldWater.shouldNotBeNull()
		coldWater.foodId shouldBe water.id

		val boilingWater = index.findFood("boiling water")
		boilingWater.shouldNotBeNull()
		boilingWater.foodId shouldBe water.id

		val lemonZest = index.findFood("lemon zest")
		lemonZest.shouldNotBeNull()
		lemonZest.foodId shouldBe lemonPeel.id
		lemonZest.matchSource shouldBe "alias"

		val gratedZest = index.findFood("finely grated lemon zest")
		gratedZest.shouldNotBeNull()
		gratedZest.foodId shouldBe lemonPeel.id

		val zestOfLemon = index.findFood("zest of 1 lemon")
		zestOfLemon.shouldNotBeNull()
		zestOfLemon.foodId shouldBe lemonPeel.id
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

	@Test
	fun findFoodOverridesEagerBrandedAndIncidentalMatches() {
		val butter = food(201, "Butter, without salt", "butter without salt", FDC_SR_LEGACY_SOURCE_NAME)
		val whiteWine = food(
			202,
			"Alcoholic beverage, wine, table, white",
			"alcoholic beverage wine table white",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val spinach = food(203, "Spinach, raw", "spinach raw", FDC_SR_LEGACY_SOURCE_NAME)
		val rice = food(
			204,
			"Rice, white, long-grain, regular, raw, unenriched",
			"rice white long grain regular raw unenriched",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val cookedRice = food(
			205,
			"Rice, white, long-grain, regular, unenriched, cooked without salt",
			"rice white long grain regular unenriched cooked without salt",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val darkChocolate = food(
			206,
			"Chocolate, dark, 70-85% cacao solids",
			"chocolate dark 70 85 cacao solids",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val peanuts = food(207, "Peanuts, all types, raw", "peanuts all types raw", FDC_SR_LEGACY_SOURCE_NAME)
		val roastedPeanuts = food(
			208,
			"Peanuts, all types, dry-roasted, with salt",
			"peanuts all types dry roasted with salt",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val sriracha = food(
			209,
			"Sauce, hot chile, sriracha",
			"sauce hot chile sriracha",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val whiskey = food(
			210,
			"Alcoholic beverage, distilled, whiskey, 86 proof",
			"alcoholic beverage distilled whiskey 86 proof",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val bayLeaf = food(211, "Spices, bay leaf", "spices bay leaf", FDC_SR_LEGACY_SOURCE_NAME)
		val lemongrass = food(
			212,
			"Lemon grass (citronella), raw",
			"lemon grass citronella raw",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val dill = food(213, "Dill weed, fresh", "dill weed fresh", FDC_SR_LEGACY_SOURCE_NAME)
		val tarragon = food(214, "Spices, tarragon, dried", "spices tarragon dried", FDC_SR_LEGACY_SOURCE_NAME)
		val salt = food(215, "Salt, table", "salt table", FDC_SR_LEGACY_SOURCE_NAME)
		val orangePeel = food(216, "Orange peel, raw", "orange peel raw", FDC_SR_LEGACY_SOURCE_NAME)
		val lemonPeel = food(217, "Lemon peel, raw", "lemon peel raw", FDC_SR_LEGACY_SOURCE_NAME)
		val soySauce = food(
			218,
			"Soy sauce made from soy and wheat (shoyu)",
			"soy sauce made from soy and wheat shoyu",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val salmon = food(
			219,
			"Fish, salmon, Atlantic, farmed, raw",
			"fish salmon atlantic farmed raw",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val sourdough = food(
			220,
			"Bread, french or vienna (includes sourdough)",
			"bread french or vienna includes sourdough",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val sausage = food(
			221,
			"Pork sausage, link/patty, unprepared",
			"pork sausage link patty unprepared",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val hotSauce = food(
			222,
			"Sauce, ready-to-serve, pepper or hot",
			"sauce ready to serve pepper or hot",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val chickenBreast = food(
			223,
			"Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			"chicken broiler or fryers breast skinless boneless meat only raw",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val apple = food(
			224,
			"Apples, raw, with skin (Includes foods for USDA's Food Distribution Program)",
			"apples raw with skin includes foods for usda s food distribution program",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val sherry = food(
			225,
			"Alcoholic beverage, wine, dessert, dry",
			"alcoholic beverage wine dessert dry",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val cayenne = food(
			226,
			"Spices, pepper, red or cayenne",
			"spices pepper red or cayenne",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val garlic = food(
			227,
			"Garlic, raw",
			"garlic raw",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val mustard = food(
			301,
			"DIJON MUSTARD WITH WHITE WINE, DIJON WITH WHITE WINE",
			"dijon mustard with white wine dijon with white wine",
			FDC_BRANDED_SOURCE_NAME,
		)
		val sweetSoy = food(
			302,
			"SWEET SOY SAUCE FOR RICE SEASONING, SWEET SOY",
			"sweet soy sauce for rice seasoning sweet soy",
			FDC_BRANDED_SOURCE_NAME,
		)
		val foods = listOf(
			butter,
			whiteWine,
			spinach,
			rice,
			cookedRice,
			darkChocolate,
			peanuts,
			roastedPeanuts,
			sriracha,
			whiskey,
			bayLeaf,
			lemongrass,
			dill,
			tarragon,
			salt,
			orangePeel,
			lemonPeel,
			soySauce,
			salmon,
			sourdough,
			sausage,
			hotSauce,
			chickenBreast,
			apple,
			sherry,
			cayenne,
			garlic,
			mustard,
			sweetSoy,
			food(
				303,
				"Potato, roasted, from fresh, peel not eaten, made with butter",
				"potato roasted from fresh peel not eaten made with butter",
				FDC_SURVEY_SOURCE_NAME,
			),
			food(
				304,
				"Ravioli, cheese and spinach filled, with tomato sauce",
				"ravioli cheese and spinach filled with tomato sauce",
				FDC_SURVEY_SOURCE_NAME,
			),
			food(
				305,
				"Fish, salmon, baked or broiled, coated",
				"fish salmon baked or broiled coated",
				FDC_SURVEY_SOURCE_NAME,
			),
			food(
				306,
				"Chicken, broiler or fryers, breast, skinless, boneless, meat only, " +
					"with added solution, cooked, braised",
				"chicken broiler or fryers breast skinless boneless meat only with added solution cooked braised",
				FDC_SR_LEGACY_SOURCE_NAME,
			),
			food(
				307,
				"BABY SPINACH & ROASTED GARLIC TORTELLONI, BABY SPINACH & ROASTED GARLIC",
				"baby spinach roasted garlic tortelloni baby spinach roasted garlic",
				FDC_BRANDED_SOURCE_NAME,
			),
		)
		val index = NutritionLookupIndex(
			foodById = foods.associateBy { food -> food.id },
			foodIdByNormalizedAlias = NutritionSeedAliasIndex.merge(
				foods,
				mapOf(
					"white wine" to mustard.id,
					"soy sauce" to sweetSoy.id,
					"spinach" to 304,
					"butter" to 303,
				),
			),
			measuresByFoodId = emptyMap(),
		)
		val expectations = listOf(
			"butter" to butter.id,
			"white wine" to whiteWine.id,
			"spinach" to spinach.id,
			"baby spinach" to spinach.id,
			"rice" to rice.id,
			"cooked rice" to cookedRice.id,
			"dark chocolate" to darkChocolate.id,
			"peanut" to peanuts.id,
			"salted roasted peanut" to roastedPeanuts.id,
			"sriracha" to sriracha.id,
			"bourbon" to whiskey.id,
			"dried bay" to bayLeaf.id,
			"lemongrass" to lemongrass.id,
			"dill" to dill.id,
			"tarragon" to tarragon.id,
			"seasoned salt" to salt.id,
			"orange zest" to orangePeel.id,
			"lime zest" to lemonPeel.id,
			"soy sauce" to soySauce.id,
			"salmon" to salmon.id,
			"sourdough" to sourdough.id,
			"sausage" to sausage.id,
			"hot sauce" to hotSauce.id,
			"chicken breast" to chickenBreast.id,
			"boneless, skinless chicken breasts" to chickenBreast.id,
			"apple" to apple.id,
			"sherry" to sherry.id,
			"aleppo pepper" to cayenne.id,
			"garlic, roasted" to garlic.id,
			"roasted garlic" to garlic.id,
		)
		expectations.forEach { (query, expectedId) ->
			"$query=${index.findFood(query)?.foodId}" shouldBe "$query=$expectedId"
		}
	}

	@Test
	fun findFoodMatchesPaneerAndLambShoulderToPlainCuts() {
		val paneer = food(
			401,
			"Cheese, paneer",
			"cheese paneer",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val paneerTikka = food(
			402,
			"PANEER TIKKA MASALA, PANEER TIKKA",
			"paneer tikka masala paneer tikka",
			FDC_BRANDED_SOURCE_NAME,
		)
		val lambChop = food(
			403,
			"Lamb, chop",
			"lamb chop",
			FDC_SR_LEGACY_SOURCE_NAME,
		)
		val lambRice = food(
			404,
			"Lamb or mutton, rice, and vegetables including carrots, broccoli, " +
				"and/or dark-green leafy; tomato-based sauce",
			"lamb or mutton rice and vegetables including carrots broccoli and or dark green leafy " +
				"tomato based sauce",
			FDC_SURVEY_SOURCE_NAME,
		)
		val foods = listOf(paneer, paneerTikka, lambChop, lambRice)
		val index = NutritionLookupIndex(
			foodById = foods.associateBy { food -> food.id },
			foodIdByNormalizedAlias = NutritionSeedAliasIndex.merge(foods, emptyMap()),
			measuresByFoodId = emptyMap(),
		)

		index.findFood("Paneer")?.foodId shouldBe paneer.id
		index.findFood("hard paneer")?.foodId shouldBe paneer.id
		index.findFood("Lamb")?.foodId shouldBe lambChop.id
		index.findFood("Lamb Shoulder")?.foodId shouldBe lambChop.id
		index.findFood("boneless lamb shoulder, cut into 1/4-inch-thick strips")?.foodId shouldBe lambChop.id
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
