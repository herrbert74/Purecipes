package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
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
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val peas = food(
			id = 72,
			displayName = "Peas, green, frozen, unprepared (Includes foods for USDA's Food Distribution Program)",
			normalizedName = "peas green frozen unprepared",
		)
		val blueberries = food(
			id = 73,
			displayName = "Blueberries, frozen, unsweetened (Includes foods for USDA's Food Distribution Program)",
			normalizedName = "blueberries frozen unsweetened",
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
		val cranberries = food(
			id = 84,
			displayName = "Cranberries, raw",
			normalizedName = "cranberries raw",
		)
		val foods = listOf(
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
			cranberries,
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = foods,
			storedAliases = emptyMap(),
		)
		val index = NutritionLookupIndex(
			foodById = foods.associateBy { food -> food.id },
			foodIdByNormalizedAlias = aliases,
			measuresByFoodId = emptyMap(),
		)

		aliases["boneless skinless chicken breast halves"] shouldBe 71
		aliases["frozen peas"] shouldBe 72
		aliases["frozen blueberries"] shouldBe 73
		aliases["lacinato kale"] shouldBe 74
		aliases["long grain brown rice"] shouldBe 75
		aliases["high protein all purpose flour"] shouldBe 76
		aliases["passata pomodoro"] shouldBe 77
		aliases["store bought passata pomodoro"] shouldBe 77
		aliases["frozen cranberries"] shouldBe 84
		aliases["zest juice lemon"] shouldBe 78
		aliases["lime juice from limes"] shouldBe 79
		aliases["pumpkin puree"] shouldBe 80
		aliases["garlic whole"] shouldBe 81
		aliases["garlic"] shouldBe 81
		aliases["bacon"] shouldBe 82
		aliases["slices bacon"] shouldBe 82
		aliases["filo pastry"] shouldBe 83

		listOf(
			"frozen peas",
			"frozen blueberries",
			"long grain brown rice",
			"passata pomodoro",
			"store-bought passata di pomodoro",
			"frozen cranberries",
		).forEach { query ->
			val lookupKey = NutritionNameNormalizer.forLookup(query)
			val foodId = aliases[lookupKey]
			foodId.shouldNotBeNull()
			val match = index.findFood(query)
			match.shouldNotBeNull()
			match.foodId shouldBe foodId
			match.matchSource shouldBe "alias"
			foods.any { food -> food.id == foodId } shouldBe true
		}
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
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
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
	fun mergeAppliesClearStillUnmatchedLeftoverAliases() {
		val dessertWine = food(
			id = 201,
			displayName = "Alcoholic beverage, wine, dessert, sweet",
			normalizedName = "alcoholic beverage wine dessert sweet",
		)
		val chickenBroth = food(
			id = 202,
			displayName = "Soup, chicken broth, ready-to-serve",
			normalizedName = "soup chicken broth ready to serve",
		)
		val chiliPowder = food(
			id = 203,
			displayName = "Spices, chili powder",
			normalizedName = "spices chili powder",
		)
		val iceberg = food(
			id = 204,
			displayName = "Lettuce, iceberg (includes crisphead types), raw",
			normalizedName = "lettuce iceberg includes crisphead types raw",
		)
		val potato = food(
			id = 205,
			displayName = "Potatoes, flesh and skin, raw",
			normalizedName = "potatoes flesh and skin raw",
		)
		val coconutMilk = food(
			id = 206,
			displayName = "Nuts, coconut milk, canned (liquid expressed from grated meat and water)",
			normalizedName = "nuts coconut milk canned",
		)
		val romano = food(
			id = 207,
			displayName = "Cheese, romano",
			normalizedName = "cheese romano",
		)
		val chickenBreast = food(
			id = 208,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val vanilla = food(
			id = 209,
			displayName = "Vanilla extract",
			normalizedName = "vanilla extract",
		)
		val vegetableBroth = food(
			id = 210,
			displayName = "Soup, vegetable broth, ready to serve",
			normalizedName = "soup vegetable broth ready to serve",
		)
		val instantCoffee = food(
			id = 211,
			displayName = "Beverages, coffee, instant, regular, powder",
			normalizedName = "beverages coffee instant regular powder",
		)
		val foods = listOf(
			dessertWine,
			chickenBroth,
			chiliPowder,
			iceberg,
			potato,
			coconutMilk,
			romano,
			chickenBreast,
			vanilla,
			vegetableBroth,
			instantCoffee,
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = foods,
			storedAliases = emptyMap(),
		)

		aliases["aperol"] shouldBe 201
		aliases["beef bouillon"] shouldBe 202
		aliases["berbere spice blend"] shouldBe 203
		aliases["butter lettuce"] shouldBe 204
		aliases["yukon gold potatoes"] shouldBe 205
		aliases["tinned coconut milk"] shouldBe 206
		aliases["unsweetened full fat coconut milk"] shouldBe 206
		aliases["pecorino"] shouldBe 207
		aliases["boneless skinless chicken breasts thin"] shouldBe 208
		aliases["vanilla bean split scraped"] shouldBe 209
		aliases["vegetable bouillon power"] shouldBe 210
		aliases["instant espresso"] shouldBe 211
		aliases["prague powder"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave2ClearUnmatchedAliases() {
		val daikon = food(
			id = 301,
			displayName = "Radishes, oriental, raw",
			normalizedName = "radishes oriental raw",
		)
		val chickenBreast = food(
			id = 302,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val mozzarella = food(
			id = 303,
			displayName = "Cheese, mozzarella, whole milk",
			normalizedName = "cheese mozzarella whole milk",
		)
		val saffron = food(
			id = 304,
			displayName = "Spices, saffron",
			normalizedName = "spices saffron",
		)
		val oats = food(
			id = 305,
			displayName = "Cereals, oats, regular and quick, not fortified, dry",
			normalizedName = "cereals oats regular and quick not fortified dry",
		)
		val banana = food(
			id = 306,
			displayName = "Bananas, raw",
			normalizedName = "bananas raw",
		)
		val soyMilk = food(
			id = 307,
			displayName = "Soymilk, original and vanilla, unfortified",
			normalizedName = "soymilk original and vanilla unfortified",
		)
		val cod = food(
			id = 308,
			displayName = "Fish, cod, Atlantic, raw",
			normalizedName = "fish cod atlantic raw",
		)
		val creamCheese = food(
			id = 309,
			displayName = "Cheese, cream",
			normalizedName = "cheese cream",
		)
		val peanutButter = food(
			id = 310,
			displayName = "Peanut butter, smooth style, without salt",
			normalizedName = "peanut butter smooth style without salt",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				daikon,
				chickenBreast,
				mozzarella,
				saffron,
				oats,
				banana,
				soyMilk,
				cod,
				creamCheese,
				peanutButter,
			),
			storedAliases = emptyMap(),
		)

		aliases["daikon radish"] shouldBe 301
		aliases["boneless skinless chicken breast cutlets"] shouldBe 302
		aliases["split chicken breasts"] shouldBe 302
		aliases["burrata"] shouldBe 303
		aliases["saffron threads"] shouldBe 304
		aliases["uncooked old fashioned rolled oats"] shouldBe 305
		aliases["very ripe bananas"] shouldBe 306
		aliases["soya milk"] shouldBe 307
		aliases["cod fillet"] shouldBe 308
		aliases["philadelphia cream cheese"] shouldBe 309
		aliases["creamy peanut butter spread"] shouldBe 310
		aliases["prague powder"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave3ReportLeftoverAliases() {
		val hotSauce = food(
			id = 401,
			displayName = "Sauce, ready-to-serve, pepper or hot",
			normalizedName = "sauce ready to serve pepper or hot",
		)
		val porkRibs = food(
			id = 402,
			displayName = "Pork, fresh, spareribs, separable lean and fat, raw",
			normalizedName = "pork fresh spareribs separable lean and fat raw",
		)
		val sugar = food(
			id = 403,
			displayName = "Sugars, granulated",
			normalizedName = "sugars granulated",
		)
		val flour = food(
			id = 404,
			displayName = "Flour, wheat, all-purpose, enriched, unbleached",
			normalizedName = "flour wheat all purpose enriched unbleached",
		)
		val cayenne = food(
			id = 405,
			displayName = "Spices, pepper, red or cayenne",
			normalizedName = "spices pepper red or cayenne",
		)
		val springOnion = food(
			id = 406,
			displayName = "Onions, spring or scallions (includes tops and bulb), raw",
			normalizedName = "onions spring or scallions includes tops and bulb raw",
		)
		val avocado = food(
			id = 407,
			displayName = "Avocados, raw, all commercial varieties",
			normalizedName = "avocados raw all commercial varieties",
		)
		val cream = food(
			id = 408,
			displayName = "Cream, heavy",
			normalizedName = "cream heavy",
		)
		val canola = food(
			id = 409,
			displayName = "Oil, canola",
			normalizedName = "oil canola",
		)
		val vanilla = food(
			id = 410,
			displayName = "Vanilla extract",
			normalizedName = "vanilla extract",
		)
		val soySauce = food(
			id = 411,
			displayName = "Soy sauce made from soy and wheat (shoyu)",
			normalizedName = "soy sauce made from soy and wheat shoyu",
		)
		val chickpea = food(
			id = 412,
			displayName =
				"Chickpeas (garbanzo beans, bengal gram), mature seeds, cooked, boiled, without salt",
			normalizedName = "chickpeas garbanzo beans bengal gram mature seeds cooked boiled without salt",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				hotSauce,
				porkRibs,
				sugar,
				flour,
				cayenne,
				springOnion,
				avocado,
				cream,
				canola,
				vanilla,
				soySauce,
				chickpea,
			),
			storedAliases = emptyMap(),
		)

		aliases["tabasco"] shouldBe 401
		aliases["louis ribs"] shouldBe 402
		aliases["orange sugar"] shouldBe 403
		aliases["protein all purpose flour"] shouldBe 404
		aliases["red pepper flakes heavier extra"] shouldBe 405
		aliases["scallions whites"] shouldBe 406
		aliases["spring onions whites"] shouldBe 406
		aliases["spring onions"] shouldBe 406
		aliases["avocado flesh"] shouldBe 407
		aliases["avocados pit"] shouldBe 407
		aliases["almond cream"] shouldBe 408
		aliases["annatto oil"] shouldBe 409
		aliases["vanilla bean"] shouldBe 410
		aliases["vanilla bean seeds"] shouldBe 410
		aliases["reduced salt soy sauce"] shouldBe 411
		aliases["chick peas"] shouldBe 412
		aliases["prague powder"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave4ReportLeftoverAliases() {
		val fries = food(
			id = 501,
			displayName =
				"Potatoes, french fried, all types, salt not added in processing, frozen, as purchased",
			normalizedName = "potatoes french fried all types salt not added in processing frozen as purchased",
		)
		val chickenBreast = food(
			id = 502,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken broiler or fryers breast skinless boneless meat only raw",
		)
		val cilantro = food(
			id = 503,
			displayName = "Coriander (cilantro) leaves, raw",
			normalizedName = "coriander cilantro leaves raw",
		)
		val kale = food(
			id = 504,
			displayName = "Kale, raw",
			normalizedName = "kale raw",
		)
		val chard = food(
			id = 505,
			displayName = "Chard, swiss, raw",
			normalizedName = "chard swiss raw",
		)
		val grapes = food(
			id = 506,
			displayName = "Grapes, red or green (European type, such as Thompson seedless), raw",
			normalizedName = "grapes red or green european type such as thompson seedless raw",
		)
		val pate = food(
			id = 507,
			displayName = "Pate, chicken liver, canned",
			normalizedName = "pate chicken liver canned",
		)
		val jelly = food(
			id = 508,
			displayName = "Jelly",
			normalizedName = "jelly",
		)
		val halloumi = food(
			id = 509,
			displayName = "HALLOUMI THE MEDITERRANEAN GRILLING CHEESE, HALLOUMI",
			normalizedName = "halloumi the mediterranean grilling cheese halloumi",
		)
		val bokChoy = food(
			id = 510,
			displayName = "Cabbage, chinese (pak-choi), raw",
			normalizedName = "cabbage chinese pak choi raw",
		)
		val stewMeat = food(
			id = 511,
			displayName = "Beef, stew meat",
			normalizedName = "beef stew meat",
		)
		val potato = food(
			id = 512,
			displayName = "Potatoes, flesh and skin, raw",
			normalizedName = "potatoes flesh and skin raw",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				fries,
				chickenBreast,
				cilantro,
				kale,
				chard,
				grapes,
				pate,
				jelly,
				halloumi,
				bokChoy,
				stewMeat,
				potato,
			),
			storedAliases = emptyMap(),
		)

		aliases["british chips"] shouldBe 501
		aliases["chicken cutlets"] shouldBe 502
		aliases["bunch cilantro"] shouldBe 503
		aliases["bunch lacinato kale"] shouldBe 504
		aliases["bunch swiss chard"] shouldBe 505
		aliases["champagne grapes"] shouldBe 506
		aliases["chicken pate"] shouldBe 507
		aliases["chilli jam"] shouldBe 508
		aliases["block halloumi"] shouldBe 509
		aliases["baby pak choi"] shouldBe 510
		aliases["beef stewing meat"] shouldBe 511
		aliases["yukon gold"] shouldBe 512
		aliases["prague powder"].shouldBeNull()
		aliases["edible gold dust"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
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
