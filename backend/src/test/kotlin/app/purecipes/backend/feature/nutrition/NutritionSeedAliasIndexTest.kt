package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionSeedAliasIndexTest {

	@Test
	fun mergeAppliesHandwrittenAliasesWithoutStoredRows() {
		val cornstarch = food(
			id = 3,
			displayName = "Cornstarch",
			normalizedName = "cornstarch",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(cornstarch),
			storedAliases = emptyMap(),
		)

		aliases["cornflour"] shouldBe 3
		aliases["cornstarch"] shouldBe 3
	}

	@Test
	fun mergeSkipsAliasesWhosePreferredFoodIsMissing() {
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				food(
					id = 1,
					displayName = "Sugars, granulated",
					normalizedName = "sugars granulated",
				),
			),
			storedAliases = emptyMap(),
		)

		aliases["cornflour"].shouldBeNull()
		aliases["caster sugar"] shouldBe 1
	}

	@Test
	fun mergePointsEggWhiteAtFoundationFood() {
		val foundationWhite = food(
			id = 11,
			displayName = "Eggs, Grade A, Large, egg white",
			normalizedName = "eggs grade a large egg white",
		)
		val legacyWhite = food(
			id = 12,
			displayName = "Egg, white, raw, fresh",
			normalizedName = "egg white raw fresh",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(legacyWhite, foundationWhite),
			storedAliases = emptyMap(),
		)

		aliases["egg white"] shouldBe 11
		aliases["egg whites"] shouldBe 11
	}

	@Test
	fun mergeAppliesFrequentUnmatchedNameAliases() {
		val vanilla = food(
			id = 21,
			displayName = "Vanilla extract",
			normalizedName = "vanilla extract",
		)
		val brownSugar = food(
			id = 22,
			displayName = "Sugars, brown",
			normalizedName = "sugars brown",
		)
		val cloveSpice = food(
			id = 23,
			displayName = "Spices, cloves, ground",
			normalizedName = "spices cloves ground",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(vanilla, brownSugar, cloveSpice),
			storedAliases = emptyMap(),
		)

		aliases["vanilla bean paste"] shouldBe 21
		aliases["dark brown sugar"] shouldBe 22
		aliases["ground clove"] shouldBe 23
		aliases["ground cloves"] shouldBe 23
	}

	@Test
	fun mergeAppliesCurrentLeftoverUnmatchedAliases() {
		val chickenBreast = food(
			id = 71,
			displayName = "Chicken, breast, boneless, skinless, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val peas = food(
			id = 72,
			displayName = "Peas, green, raw",
			normalizedName = "peas green raw",
		)
		val blueberries = food(
			id = 73,
			displayName = "Blueberries, raw",
			normalizedName = "blueberries raw",
		)
		val kale = food(
			id = 74,
			displayName = "Kale, raw",
			normalizedName = "kale raw",
		)
		val brownRice = food(
			id = 75,
			displayName = "Rice, brown, long-grain, raw (Includes foods for USDA's Food Distribution Program)",
			normalizedName = "rice brown long grain raw includes foods for usdas food distribution program",
		)
		val flour = food(
			id = 76,
			displayName = "Flour, wheat, all-purpose, enriched, unbleached",
			normalizedName = "flour wheat all purpose enriched unbleached",
		)
		val passata = food(
			id = 77,
			displayName = "Tomatoes, crushed, canned",
			normalizedName = "tomatoes crushed canned",
		)
		val lemonJuice = food(
			id = 78,
			displayName = "Lemon juice, raw",
			normalizedName = "lemon juice raw",
		)
		val limeJuice = food(
			id = 79,
			displayName = "Lime juice, raw",
			normalizedName = "lime juice raw",
		)
		val pumpkin = food(
			id = 80,
			displayName = "Pumpkin, canned, without salt",
			normalizedName = "pumpkin canned without salt",
		)
		val garlic = food(
			id = 81,
			displayName = "Garlic, raw",
			normalizedName = "garlic raw",
		)
		val bacon = food(
			id = 82,
			displayName = "Pork, cured, bacon, unprepared",
			normalizedName = "pork cured bacon unprepared",
		)
		val filo = food(
			id = 83,
			displayName = "Puff pastry, frozen, ready-to-bake",
			normalizedName = "puff pastry frozen ready to bake",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				chickenBreast,
				peas,
				blueberries,
				kale,
				brownRice,
				flour,
				passata,
				lemonJuice,
				limeJuice,
				pumpkin,
				garlic,
				bacon,
				filo,
			),
			storedAliases = emptyMap(),
		)

		aliases["boneless skinless chicken breast halves"] shouldBe 71
		aliases["frozen peas"] shouldBe 72
		aliases["frozen blueberries"] shouldBe 73
		aliases["lacinato kale"] shouldBe 74
		aliases["long grain brown rice"] shouldBe 75
		aliases["high protein all purpose flour"] shouldBe 76
		aliases["passata pomodoro"] shouldBe 77
		aliases["store bought passata pomodoro"] shouldBe 77
		aliases["zest juice lemon"] shouldBe 78
		aliases["lime juice from limes"] shouldBe 79
		aliases["pumpkin puree"] shouldBe 80
		aliases["garlic whole"] shouldBe 81
		aliases["garlic"] shouldBe 81
		aliases["bacon"] shouldBe 82
		aliases["slices bacon"] shouldBe 82
		aliases["filo pastry"] shouldBe 83
	}

	@Test
	fun mergeAppliesHighConfidenceUnmatchedReportAliases() {
		val celery = food(
			id = 91,
			displayName = "Celery, raw",
			normalizedName = "celery raw",
		)
		val greenChile = food(
			id = 92,
			displayName = "Peppers, hot chili, green, raw",
			normalizedName = "peppers hot chili green raw",
		)
		val whiteBread = food(
			id = 93,
			displayName = "Bread, white, commercially prepared (includes soft bread crumbs)",
			normalizedName = "bread white commercially prepared includes soft bread crumbs",
		)
		val chickenBreast = food(
			id = 94,
			displayName = "Chicken, breast, boneless, skinless, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val flour = food(
			id = 95,
			displayName = "Flour, wheat, all-purpose, enriched, unbleached",
			normalizedName = "flour wheat all purpose enriched unbleached",
		)
		val oliveOil = food(
			id = 96,
			displayName = "Oil, olive, extra virgin",
			normalizedName = "oil olive extra virgin",
		)
		val salt = food(
			id = 97,
			displayName = "Salt, table",
			normalizedName = "salt table",
		)
		val almond = food(
			id = 98,
			displayName = "Nuts, almonds",
			normalizedName = "nuts almonds",
		)
		val mayo = food(
			id = 99,
			displayName = "Mayonnaise, regular",
			normalizedName = "mayonnaise regular",
		)
		val yoghurt = food(
			id = 100,
			displayName = "Yogurt, plain, whole milk",
			normalizedName = "yogurt plain whole milk",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				celery,
				greenChile,
				whiteBread,
				chickenBreast,
				flour,
				oliveOil,
				salt,
				almond,
				mayo,
				yoghurt,
			),
			storedAliases = emptyMap(),
		)

		aliases["celery sticks"] shouldBe 91
		aliases["mild green chiles"] shouldBe 92
		aliases["green chiles"] shouldBe 92
		aliases["white sandwich bread"] shouldBe 93
		aliases["white sandwich bread crusts"] shouldBe 93
		aliases["skinless boneless chicken breast pounded"] shouldBe 94
		aliases["all purpose flour"] shouldBe 95
		aliases["soft wheat all purpose flour"] shouldBe 95
		aliases["extra virgin olive oil turns the pan"] shouldBe 96
		aliases["salt sprinkling"] shouldBe 97
		aliases["slivered almonds"] shouldBe 98
		aliases["mayonnaise"] shouldBe 99
		aliases["yoghurt"] shouldBe 100
	}

	@Test
	fun mergeAppliesProduceCountAliases() {
		val onion = food(
			id = 31,
			displayName = "Onions, raw",
			normalizedName = "onions raw",
		)
		val avocado = food(
			id = 32,
			displayName = "Avocados, raw, all commercial varieties",
			normalizedName = "avocados raw all commercial varieties",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(onion, avocado),
			storedAliases = emptyMap(),
		)

		aliases["onions"] shouldBe 31
		aliases["white onion"] shouldBe 31
		aliases["avocado"] shouldBe 32
	}

	@Test
	fun mergeAppliesFrequentPantryAliases() {
		val garlic = food(
			id = 41,
			displayName = "Garlic, raw",
			normalizedName = "garlic raw",
		)
		val paste = food(
			id = 42,
			displayName = "Tomato products, canned, paste, without salt added " +
				"(Includes foods for USDA's Food Distribution Program)",
			normalizedName = "tomato products canned paste",
		)
		val cider = food(
			id = 43,
			displayName = "Vinegar, cider",
			normalizedName = "vinegar cider",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(garlic, paste, cider),
			storedAliases = emptyMap(),
		)

		aliases["garlic cloves"] shouldBe 41
		aliases["tomato paste"] shouldBe 42
		aliases["apple cider vinegar"] shouldBe 43
	}

	@Test
	fun mergeAppliesLeftoverPantryAliases() {
		val italianBread = food(
			id = 51,
			displayName = "Bread, Italian",
			normalizedName = "bread italian",
		)
		val oatMilk = food(
			id = 52,
			displayName = "Oat milk, unsweetened, plain, refrigerated",
			normalizedName = "oat milk unsweetened plain refrigerated",
		)
		val canola = food(
			id = 53,
			displayName = "Oil, canola",
			normalizedName = "oil canola",
		)
		val shrimp = food(
			id = 54,
			displayName = "Crustaceans, shrimp, raw",
			normalizedName = "crustaceans shrimp raw",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(italianBread, oatMilk, canola, shrimp),
			storedAliases = emptyMap(),
		)

		aliases["ciabatta"] shouldBe 51
		aliases["oat milk"] shouldBe 52
		aliases["neutral oil"] shouldBe 53
		aliases["canola oil"] shouldBe 53
		aliases["king prawns"] shouldBe 54
		aliases["prawns"] shouldBe 54
	}

	@Test
	fun mergeAppliesHighFrequencyUnmatchedPantryAliases() {
		val parsley = food(
			id = 61,
			displayName = "Parsley, fresh",
			normalizedName = "parsley fresh",
		)
		val celery = food(
			id = 62,
			displayName = "Celery, raw",
			normalizedName = "celery raw",
		)
		val cabbage = food(
			id = 63,
			displayName = "Cabbage, green, raw",
			normalizedName = "cabbage green raw",
		)
		val redWine = food(
			id = 64,
			displayName = "Alcoholic beverage, wine, table, red",
			normalizedName = "alcoholic beverage wine table red",
		)
		val sourCream = food(
			id = 65,
			displayName = "Cream, sour, full fat",
			normalizedName = "cream sour full fat",
		)
		val gruyere = food(
			id = 66,
			displayName = "Cheese, gruyere",
			normalizedName = "cheese gruyere",
		)
		val peanutOil = food(
			id = 67,
			displayName = "Oil, peanut",
			normalizedName = "oil peanut",
		)
		val yeast = food(
			id = 68,
			displayName = "Leavening agents, yeast, baker's, active dry",
			normalizedName = "leavening agents yeast bakers active dry",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				parsley,
				celery,
				cabbage,
				redWine,
				sourCream,
				gruyere,
				peanutOil,
				yeast,
			),
			storedAliases = emptyMap(),
		)

		aliases["flat leaf parsley"] shouldBe 61
		aliases["celery stalks"] shouldBe 62
		aliases["white cabbage"] shouldBe 63
		aliases["dry red wine"] shouldBe 64
		aliases["fra che"] shouldBe 65
		aliases["creme fraiche"] shouldBe 65
		aliases["gruy"] shouldBe 66
		aliases["groundnut oil"] shouldBe 67
		aliases["instant yeast"] shouldBe 68
		aliases["lime wedges"].shouldBeNull()
	}

	@Test
	fun mergePrefersSeedAliasesOverStoredRows() {
		val unsalted = food(
			id = 8,
			displayName = "Butter, without salt",
			normalizedName = "butter without salt",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(unsalted),
			storedAliases = mapOf("unsalted butter" to 99),
		)

		aliases["unsalted butter"] shouldBe 8
	}

	private fun food(id: Int, displayName: String, normalizedName: String): NutritionFoodRecord =
		NutritionFoodRecord(
			id = id,
			displayName = displayName,
			normalizedName = normalizedName,
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
