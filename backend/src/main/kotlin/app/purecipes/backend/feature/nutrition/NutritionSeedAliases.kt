package app.purecipes.backend.feature.nutrition

internal data class NutritionSeedAlias(
	val alias: String,
	val preferredDescriptions: List<String>,
) {

	constructor(alias: String, preferredDescription: String, vararg fallbacks: String) : this(
		alias = alias,
		preferredDescriptions = listOf(preferredDescription, *fallbacks),
	)
}

internal object NutritionSeedAliases {

	private const val ALMOND_DESCRIPTION = "Nuts, almonds"
	private const val AGAVE_SYRUP_DESCRIPTION = "Sweetener, syrup, agave"
	private const val ANCHO_DESCRIPTION = "Peppers, ancho, dried"
	private const val ANISE_DESCRIPTION = "Spices, anise seed"
	private const val ARUGULA_DESCRIPTION = "Arugula, raw"
	private const val ANCHOVY_DESCRIPTION = "Fish, anchovy, european, canned in oil, drained solids"
	private const val ANCHOVY_FALLBACK_DESCRIPTION = "Fish, anchovy, european, raw"
	private const val ASPARAGUS_DESCRIPTION = "Asparagus, raw"
	private const val AVOCADO_DESCRIPTION = "Avocados, raw, all commercial varieties"
	private const val BACON_DESCRIPTION = "Pork, cured, bacon, cooked, baked"
	private const val BACON_UNPREPARED_DESCRIPTION = "Pork, cured, bacon, unprepared"
	private const val BAGUETTE_DESCRIPTION = "Bread, french or vienna (includes sourdough)"
	private const val BAKING_POWDER_DESCRIPTION =
		"Leavening agents, baking powder, double-acting, sodium aluminum sulfate"
	private const val BAKING_SODA_DESCRIPTION = "Leavening agents, baking soda"
	private const val BALSAMIC_VINEGAR_DESCRIPTION = "Vinegar, balsamic"
	private const val BASIL_DESCRIPTION = "Basil, fresh"
	private const val BASIL_FALLBACK_DESCRIPTION = "Basil, raw"
	private const val BAY_LEAF_DESCRIPTION = "Bay leaf, dried"
	private const val BEANSPROUT_DESCRIPTION = "Mung beans, mature seeds, sprouted, raw"
	private const val BEEF_CHUCK_DESCRIPTION = "Beef, chuck, roast, boneless, choice, raw"
	private const val BEET_DESCRIPTION = "Beets, raw"
	private const val BEER_DESCRIPTION = "Alcoholic beverage, beer, regular, all"
	private const val BELL_PEPPER_DESCRIPTION = "Peppers, sweet, green, raw"
	private const val BLACK_PEPPER_DESCRIPTION = "Spices, pepper, black"
	private const val BLACK_BEAN_DESCRIPTION =
		"Beans, black, mature seeds, cooked, boiled, without salt"
	private const val BLOOD_SAUSAGE_DESCRIPTION = "Blood sausage"
	private const val BLUE_CHEESE_DESCRIPTION = "Cheese, blue"
	private const val BLUEBERRY_DESCRIPTION = "Blueberries, raw"
	private const val BLUEBERRY_FROZEN_DESCRIPTION =
		"Blueberries, frozen, unsweetened (Includes foods for USDA's Food Distribution Program)"
	private const val BOK_CHOY_DESCRIPTION = "Cabbage, bok choy, raw"
	private const val BROWN_RICE_DESCRIPTION =
		"Rice, brown, long-grain, raw (Includes foods for USDA's Food Distribution Program)"
	private const val BROWN_RICE_FALLBACK_DESCRIPTION = "Rice, brown, long-grain, raw"
	private const val BREAD_FLOUR_DESCRIPTION = "Flour, bread, white, enriched, unbleached"
	private const val BREAD_FLOUR_FALLBACK_DESCRIPTION = "Wheat flour, white, bread, enriched"
	private const val BREADCRUMB_DESCRIPTION = "Bread, crumbs, dry, grated, plain"
	private const val BROCCOLI_DESCRIPTION = "Broccoli, raw"
	private const val BROWN_SUGAR_DESCRIPTION = "Sugars, brown"
	private const val BURGER_BUN_DESCRIPTION = "Rolls, hamburger or hotdog, plain"
	private const val BUTTER_BEAN_DESCRIPTION =
		"Lima beans, large, mature seeds, cooked, boiled, without salt"
	private const val BUTTER_DESCRIPTION = "Butter, stick, unsalted"
	private const val BUTTER_FALLBACK_DESCRIPTION = "Butter, without salt"
	private const val BUTTERNUT_SQUASH_DESCRIPTION = "Squash, winter, butternut, raw"
	private const val CABBAGE_DESCRIPTION = "Cabbage, raw"
	private const val CABBAGE_GREEN_DESCRIPTION = "Cabbage, green, raw"
	private const val CABBAGE_RED_DESCRIPTION = "Cabbage, red, raw"
	private const val CAKE_FLOUR_DESCRIPTION = "Wheat flour, white, cake, enriched"
	private const val CAPERS_DESCRIPTION = "Capers, canned"
	private const val CANNELLINI_DESCRIPTION =
		"Beans, great northern, mature seeds, cooked, boiled, without salt"
	private const val CANOLA_OIL_DESCRIPTION = "Oil, canola"
	private const val CARDAMOM_DESCRIPTION = "Spices, cardamom"
	private const val CARROT_DESCRIPTION = "Carrots, raw"
	private const val CAULIFLOWER_DESCRIPTION = "Cauliflower, raw"
	private const val CAYENNE_DESCRIPTION = "Spices, pepper, red or cayenne"
	private const val CELERIAC_DESCRIPTION = "Celeriac, raw"
	private const val CELERY_DESCRIPTION = "Celery, raw"
	private const val CHEDDAR_DESCRIPTION = "Cheese, cheddar"
	private const val CHICKEN_BREAST_DESCRIPTION = "Chicken, breast, boneless, skinless, raw"
	private const val CHICKEN_BREAST_FALLBACK_DESCRIPTION =
		"Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw"
	private const val CHICKEN_BROTH_DESCRIPTION = "Soup, chicken broth, ready-to-serve"
	private const val CHICKEN_MINCE_DESCRIPTION = "Chicken, ground, raw"
	private const val CHICKEN_MINCE_FALLBACK_DESCRIPTION = "Chicken, ground"
	private const val CHICKEN_THIGH_DESCRIPTION = "Chicken, thigh, boneless, skinless, raw"
	private const val CHICKEN_THIGH_FALLBACK_DESCRIPTION =
		"Chicken, broilers or fryers, dark meat, thigh, meat only, raw"
	private const val CHICKPEA_DESCRIPTION =
		"Chickpeas (garbanzo beans, bengal gram), mature seeds, cooked, boiled, without salt"
	private const val CHICKPEA_FLOUR_DESCRIPTION = "Chickpea flour (besan)"
	private const val CHILI_POWDER_DESCRIPTION = "Spices, chili powder"
	private const val CHORIZO_DESCRIPTION = "Sausage, pork, chorizo, link or ground, raw"
	private const val CIDER_VINEGAR_DESCRIPTION = "Vinegar, cider"
	private const val CILANTRO_DESCRIPTION = "Coriander (cilantro) leaves, raw"
	private const val CINNAMON_DESCRIPTION = "Spices, cinnamon, ground"
	private const val CLOVE_SPICE_DESCRIPTION = "Spices, cloves, ground"
	private const val COCOA_DESCRIPTION = "Cocoa, dry powder, unsweetened"
	private const val COCONUT_MILK_DESCRIPTION =
		"Nuts, coconut milk, canned (liquid expressed from grated meat and water)"
	private const val COCONUT_YOGURT_DESCRIPTION = "Yogurt, coconut milk"
	private const val COOKING_SPRAY_DESCRIPTION = "Oil, PAM cooking spray, original"
	private const val CORIANDER_SEED_DESCRIPTION = "Spices, coriander seed"
	private const val CORN_SYRUP_DESCRIPTION = "Syrups, corn, light"
	private const val CORNSTARCH_DESCRIPTION = "Cornstarch"
	private const val CRANBERRY_DESCRIPTION = "Cranberries, raw"
	private const val CORN_COB_DESCRIPTION = "Corn, sweet, yellow, raw"
	private const val CORN_OIL_DESCRIPTION = "Oil, corn"
	private const val CORN_OIL_FALLBACK_DESCRIPTION =
		"Oil, corn, industrial and retail, all purpose salad or cooking"
	private const val CORN_TORTILLA_DESCRIPTION = "Tortillas, ready-to-bake or -fry, corn"
	private const val CREAM_DESCRIPTION = "Cream, heavy"
	private const val CREAM_FALLBACK_DESCRIPTION = "Cream, fluid, heavy whipping"
	private const val CREAM_SOUR_DESCRIPTION = "Cream, sour, full fat"
	private const val CREAM_SOUR_FALLBACK_DESCRIPTION = "Cream, sour, cultured"
	private const val CREMINI_DESCRIPTION = "Mushrooms, brown, italian, or crimini, raw"
	private const val CUCUMBER_DESCRIPTION = "Cucumber, with peel, raw"
	private const val CUMIN_DESCRIPTION = "Spices, cumin seed"
	private const val CURRY_POWDER_DESCRIPTION = "Spices, curry powder"
	private const val DARK_CHOCOLATE_DESCRIPTION = "Chocolate, dark, 70-85% cacao solids"
	private const val DARK_CHOCOLATE_FALLBACK_DESCRIPTION = "Chocolate, dark, 60-69% cacao solids"
	private const val DESSERT_WINE_DRY_DESCRIPTION = "Alcoholic beverage, wine, dessert, dry"
	private const val DESSERT_WINE_SWEET_DESCRIPTION = "Alcoholic beverage, wine, dessert, sweet"
	private const val DILL_DESCRIPTION = "Dill weed, fresh"
	private const val DRY_MILK_DESCRIPTION =
		"Milk, dry, nonfat, regular, without added vitamin A and vitamin D"
	private const val EDIBLE_PODDED_PEA_DESCRIPTION = "Peas, edible-podded, raw"
	private const val EGG_DESCRIPTION = "Eggs, Grade A, Large, egg whole"
	private const val EGG_NOODLE_DESCRIPTION = "Noodles, egg, dry, enriched"
	private const val EGG_WHITE_DESCRIPTION = "Eggs, Grade A, Large, egg white"
	private const val EGG_WHITE_FALLBACK_DESCRIPTION = "Egg, white, raw, fresh"
	private const val EGG_YOLK_DESCRIPTION = "Eggs, Grade A, Large, egg yolk"
	private const val EGGPLANT_DESCRIPTION = "Eggplant, raw"
	private const val EVAPORATED_MILK_DESCRIPTION =
		"Milk, canned, evaporated, with added vitamin A"
	private const val FENNEL_SEED_DESCRIPTION = "Spices, fennel seed"
	private const val FETA_DESCRIPTION = "Cheese, feta"
	private const val FISH_SAUCE_DESCRIPTION = "Sauce, fish, ready-to-serve"
	private const val FLATBREAD_DESCRIPTION = "Focaccia, Italian flatbread, plain"
	private const val FLOUR_DESCRIPTION = "Flour, wheat, all-purpose, enriched, unbleached"
	private const val FLOUR_TORTILLA_DESCRIPTION =
		"Tortillas, ready-to-bake or -fry, flour, refrigerated"
	private const val GARLIC_DESCRIPTION = "Garlic, raw"
	private const val GARLIC_POWDER_DESCRIPTION = "Spices, garlic powder"
	private const val GELATIN_DESCRIPTION = "Gelatins, dry powder, unsweetened"
	private const val GINGER_FRESH_DESCRIPTION = "Ginger root, raw"
	private const val GINGER_GROUND_DESCRIPTION = "Spices, ginger, ground"
	private const val GREEN_PEAS_DESCRIPTION = "Peas, green, raw"
	private const val GREEN_PEAS_FALLBACK_DESCRIPTION =
		"Peas, green, frozen, unprepared (Includes foods for USDA's Food Distribution Program)"
	private const val GRUYERE_DESCRIPTION = "Cheese, gruyere"
	private const val GRUYERE_FALLBACK_DESCRIPTION = "Cheese, Gruyere"
	private const val HALLOUMI_DESCRIPTION = "Cheese, halloumi"
	private const val HAM_DESCRIPTION =
		"Pork, cured, ham, boneless, extra lean and regular, unheated"
	private const val HONEY_DESCRIPTION = "Honey"
	private const val HOT_CHILI_GREEN_DESCRIPTION = "Peppers, hot chili, green, raw"
	private const val HOT_CHILI_RED_DESCRIPTION = "Peppers, hot chili, red, raw"
	private const val HOT_SAUCE_DESCRIPTION = "Sauce, ready-to-serve, pepper or hot"
	private const val ICEBERG_LETTUCE_DESCRIPTION =
		"Lettuce, iceberg (includes crisphead types), raw"
	private const val INSTANT_COFFEE_DESCRIPTION = "Beverages, coffee, instant, regular, powder"
	private const val INSTANT_COFFEE_FALLBACK_DESCRIPTION = "Coffee, instant, not reconstituted"
	private const val INSTANT_YEAST_DESCRIPTION = "Leavening agents, yeast, baker's, active dry"
	private const val ITALIAN_BREAD_DESCRIPTION = "Bread, Italian"
	private const val JALAPENO_DESCRIPTION = "Peppers, jalapeno, raw"
	private const val KALE_DESCRIPTION = "Kale, raw"
	private const val KETCHUP_DESCRIPTION = "Catsup"
	private const val LAMB_MINCE_DESCRIPTION = "Lamb, ground, raw"
	private const val LEEK_DESCRIPTION = "Leeks, (bulb and lower leaf-portion), raw"
	private const val LEMON_DESCRIPTION = "Lemons, raw, without peel"
	private const val LEMON_JUICE_DESCRIPTION = "Lemon juice, raw"
	private const val LEMON_PEEL_DESCRIPTION = "Lemon peel, raw"
	private const val LEMONGRASS_DESCRIPTION = "Lemon grass (citronella), raw"
	private const val LENTIL_DESCRIPTION = "Lentils, mature seeds, cooked, boiled, without salt"
	private const val LIME_DESCRIPTION = "Limes, raw"
	private const val LIME_JUICE_DESCRIPTION = "Lime juice, raw"
	private const val MANGO_DESCRIPTION = "Mangos, raw"
	private const val MAPLE_SYRUP_DESCRIPTION = "Syrups, maple"
	private const val MARGARINE_DESCRIPTION =
		"Margarine, regular, 80% fat, composite, stick, without salt"
	private const val MARINARA_DESCRIPTION =
		"Sauce, pasta, spaghetti/marinara, ready-to-serve"
	private const val MARMITE_DESCRIPTION = "Yeast extract spread"
	private const val MAYONNAISE_DESCRIPTION = "Mayonnaise, regular"
	private const val MILK_DESCRIPTION = "Milk, whole, 3.25% milkfat, with added vitamin D"
	private const val MINT_DESCRIPTION = "Spearmint, fresh"
	private const val MISO_DESCRIPTION = "Miso"
	private const val MUSTARD_DESCRIPTION = "Mustard, prepared, yellow"
	private const val MUSTARD_SEED_DESCRIPTION = "Spices, mustard seed, ground"
	private const val MUSHROOM_DESCRIPTION = "Mushrooms, white, raw"
	private const val MUSSEL_DESCRIPTION = "Mollusks, mussel, blue, raw"
	private const val NUTMEG_DESCRIPTION = "Spices, nutmeg, ground"
	private const val OAT_MILK_DESCRIPTION = "Oat milk, unsweetened, plain, refrigerated"
	private const val OAT_MILK_SURVEY_DESCRIPTION = "Oat milk"
	private const val OLIVE_OIL_DESCRIPTION = "Oil, olive, extra virgin"
	private const val OLIVE_OIL_FALLBACK_DESCRIPTION = "Oil, olive, salad or cooking"
	private const val OLIVE_DESCRIPTION = "Olives, ripe, canned (small-extra large)"
	private const val ONION_DESCRIPTION = "Onions, raw"
	private const val ONION_POWDER_DESCRIPTION = "Spices, onion powder"
	private const val OREGANO_DESCRIPTION = "Spices, oregano, dried"
	private const val ORANGE_DESCRIPTION = "Oranges, raw, all commercial varieties"
	private const val OYSTER_SAUCE_DESCRIPTION = "Sauce, oyster, ready-to-serve"
	private const val OYSTER_SAUCE_FALLBACK_DESCRIPTION = "Oyster sauce"
	private const val PAPRIKA_DESCRIPTION = "Spices, paprika"
	private const val PARMESAN_DESCRIPTION = "Cheese, parmesan, grated"
	private const val PARSLEY_DESCRIPTION = "Parsley, fresh"
	private const val PARSLEY_FALLBACK_DESCRIPTION = "Parsley, raw"
	private const val PASTA_DESCRIPTION = "Pasta, dry, unenriched"
	private const val PEANUT_OIL_DESCRIPTION = "Oil, peanut"
	private const val PEANUT_OIL_FALLBACK_DESCRIPTION = "Oil, peanut, salad or cooking"
	private const val PECAN_DESCRIPTION = "Nuts, pecans"
	private const val PINE_NUT_DESCRIPTION = "Nuts, pine nuts, dried"
	private const val PINTO_BEAN_DESCRIPTION = "Beans, pinto, mature seeds, cooked, boiled, without salt"
	private const val PITA_DESCRIPTION = "Bread, pita, white, enriched"
	private const val PLANTAIN_DESCRIPTION = "Plantains, ripe, raw"
	private const val PLANT_MINCE_DESCRIPTION = "Textured vegetable protein, dry"
	private const val RADICCHIO_DESCRIPTION = "Radicchio, raw"
	private const val POMEGRANATE_DESCRIPTION = "Pomegranates, raw"
	private const val PORK_MINCE_DESCRIPTION = "Pork, ground, 84% lean / 16% fat, raw"
	private const val PORK_RIBS_DESCRIPTION =
		"Pork, fresh, spareribs, separable lean and fat, raw"
	private const val PORK_SAUSAGE_DESCRIPTION = "Pork sausage, link/patty, unprepared"
	private const val POTATO_DESCRIPTION = "Potatoes, flesh and skin, raw"
	private const val PUMPKIN_CANNED_DESCRIPTION = "Pumpkin, canned, without salt"
	private const val PUFF_PASTRY_DESCRIPTION = "Puff pastry, frozen, ready-to-bake"
	private const val QUESO_FRESCO_DESCRIPTION = "Cheese, fresh, queso fresco"
	private const val RED_BELL_PEPPER_DESCRIPTION = "Peppers, sweet, red, raw"
	private const val RED_WINE_DESCRIPTION = "Alcoholic beverage, wine, table, red"
	private const val RICE_DESCRIPTION = "Rice, white, long grain, unenriched, raw"
	private const val RICE_FALLBACK_DESCRIPTION = "Rice, white, long-grain, regular, raw, unenriched"
	private const val RICE_NOODLE_DESCRIPTION = "Rice noodles, dry"
	private const val RICE_WINE_DESCRIPTION = "Wine, rice"
	private const val ROMAINE_LETTUCE_DESCRIPTION = "Lettuce, cos or romaine, raw"
	private const val ROMANO_DESCRIPTION = "Cheese, romano"
	private const val ROSEMARY_DESCRIPTION = "Rosemary, fresh"
	private const val SALAMI_DESCRIPTION = "Salami, dry or hard, pork"
	private const val SALT_DESCRIPTION = "Salt, table"
	private const val SELF_RISING_FLOUR_DESCRIPTION = "Wheat flour, white, all-purpose, self-rising, enriched"
	private const val SERRANO_DESCRIPTION = "Peppers, serrano, raw"
	private const val SESAME_OIL_DESCRIPTION = "Oil, sesame, salad or cooking"
	private const val SESAME_SEED_DESCRIPTION = "Seeds, sesame seeds, whole, dried"
	private const val SHALLOT_DESCRIPTION = "Shallots, raw"
	private const val SHRIMP_DESCRIPTION = "Crustaceans, shrimp, raw"
	private const val SINGLE_CREAM_DESCRIPTION =
		"Cream, fluid, light (coffee cream or table cream)"
	private const val SKIM_MILK_DESCRIPTION =
		"Milk, nonfat, fluid, with added vitamin A and vitamin D (fat free or skim)"
	private const val SOY_SAUCE_DESCRIPTION = "Soy sauce made from soy and wheat (shoyu)"
	private const val SOYBEAN_OIL_DESCRIPTION = "Oil, soybean, salad or cooking"
	private const val SPINACH_DESCRIPTION = "Spinach, raw"
	private const val STRAWBERRY_DESCRIPTION = "Strawberries, raw"
	private const val SPRING_ONION_DESCRIPTION = "Onions, spring or scallions (includes tops and bulb), raw"
	private const val SUGAR_DESCRIPTION = "Sugars, granulated"
	private const val SWEET_POTATO_DESCRIPTION =
		"Sweet potato, raw, unprepared (Includes foods for USDA's Food Distribution Program)"
	private const val TAHINI_DESCRIPTION =
		"Seeds, sesame butter, tahini, from roasted and toasted kernels (most common type)"
	private const val TAMARIND_DESCRIPTION = "Tamarinds, raw"
	private const val TAMARIND_FALLBACK_DESCRIPTION = "Tamarind"
	private const val TOFU_DESCRIPTION = "Tofu, raw, firm, prepared with calcium sulfate"
	private const val TOMATO_CRUSHED_DESCRIPTION = "Tomatoes, crushed, canned"
	private const val TOMATO_DICED_DESCRIPTION = "Tomatoes, canned, red, ripe, diced"
	private const val TOMATO_PASTE_DESCRIPTION =
		"Tomato products, canned, paste, without salt added (Includes foods for USDA's Food Distribution Program)"
	private const val TOMATO_RAW_DESCRIPTION = "Tomatoes, red, ripe, raw, year round average"
	private const val TOMATO_SAUCE_DESCRIPTION = "Tomato products, canned, sauce"
	private const val TOMATILLO_DESCRIPTION = "Tomatillos, raw"
	private const val TOMATILLO_FALLBACK_DESCRIPTION = "Tomatillos, dehusked, raw"
	private const val TURKEY_DESCRIPTION = "Turkey, whole, meat only, raw"
	private const val TURKEY_FALLBACK_DESCRIPTION = "Turkey, dark meat, meat and skin, raw"
	private const val TURMERIC_DESCRIPTION = "Spices, turmeric, ground"
	private const val VANILLA_DESCRIPTION = "Vanilla extract"
	private const val VEGETABLE_BROTH_DESCRIPTION = "Soup, vegetable broth, ready to serve"
	private const val VEGGIE_BURGER_DESCRIPTION = "Veggie burgers or soyburgers, unprepared"
	private const val WALNUT_DESCRIPTION = "Nuts, walnuts, english"
	private const val WATER_CHESTNUT_DESCRIPTION =
		"Waterchestnuts, chinese, canned, solids and liquids"
	private const val WATER_CHESTNUT_RAW_DESCRIPTION = "Waterchestnuts, chinese, (matai), raw"
	private const val WATER_DESCRIPTION = "Water, tap"
	private const val WATER_FALLBACK_DESCRIPTION = "Water, NFS"
	private const val WHITE_BREAD_DESCRIPTION =
		"Bread, white, commercially prepared (includes soft bread crumbs)"
	private const val WHITE_BREAD_FALLBACK_DESCRIPTION = "Bread, white"
	private const val WHITE_PEPPER_DESCRIPTION = "Spices, pepper, white"
	private const val WHITE_VINEGAR_DESCRIPTION = "Vinegar, distilled"
	private const val WHITE_WINE_DESCRIPTION = "Alcoholic beverage, wine, table, white"
	private const val WHOLE_CHICKEN_DESCRIPTION = "Chicken, broilers or fryers, meat and skin, raw"
	private const val WORCESTERSHIRE_DESCRIPTION = "Sauce, worcestershire"
	private const val YELLOW_BELL_PEPPER_DESCRIPTION = "Peppers, sweet, yellow, raw"
	private const val YOGURT_DESCRIPTION = "Yogurt, plain, whole milk"
	private const val ZUCCHINI_DESCRIPTION = "Squash, summer, zucchini, includes skin, raw"

	private const val CLAM_JUICE_DESCRIPTION = "Soup, clam chowder, new england, canned, ready-to-serve"

	private const val GRAHAM_DESCRIPTION = "Cookies, graham crackers, plain or honey (includes cinnamon)"

	private const val GREEN_TEA_DESCRIPTION = "Beverages, tea, green, ready to drink, unsweetened"

	private const val ORANGE_JUICE_DESCRIPTION = "Orange juice, raw"

	private const val PARSNIP_DESCRIPTION = "Parsnips, raw"

	private const val PICKLE_DESCRIPTION = "Pickles, cucumber, dill or kosher dill"

	private const val PINEAPPLE_DESCRIPTION = "Pineapple, raw, all varieties"

	private const val SALMON_DESCRIPTION = "Fish, salmon, Atlantic, farmed, raw"

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias("achiote paste", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("agave nectar", AGAVE_SYRUP_DESCRIPTION),
		NutritionSeedAlias(
			"aged sherry vinegar",
			CIDER_VINEGAR_DESCRIPTION,
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION
		),
		NutritionSeedAlias("all-purpose flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("almond extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("almond flour", ALMOND_DESCRIPTION),
		NutritionSeedAlias("almonds", ALMOND_DESCRIPTION),
		NutritionSeedAlias("anaheim chilli", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("anaheim chillies", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("anaheim peppers", HOT_CHILI_GREEN_DESCRIPTION, BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("ancho chile", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("ancho chiles", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("ancho chili powder", CHILI_POWDER_DESCRIPTION, ANCHO_DESCRIPTION),
		NutritionSeedAlias("ancho chilli", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("ancho chilli powder", CHILI_POWDER_DESCRIPTION, ANCHO_DESCRIPTION),
		NutritionSeedAlias("anchovies", ANCHOVY_DESCRIPTION, ANCHOVY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("anchovy", ANCHOVY_DESCRIPTION, ANCHOVY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("apple cider vinegar", CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("arborio rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("arborio risotto rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("asafetida", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("asafoetida", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("asparagus thirds", ASPARAGUS_DESCRIPTION),
		NutritionSeedAlias("atar", OREGANO_DESCRIPTION),
		NutritionSeedAlias("aubergine", EGGPLANT_DESCRIPTION),
		NutritionSeedAlias("avocado", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("baby bok choy", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("baby courgette", ZUCCHINI_DESCRIPTION),
		NutritionSeedAlias("baby gem lettuce", ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("baby pak choi", BOK_CHOY_DESCRIPTION),
		NutritionSeedAlias("baby plum tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("baby plum tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("baby portabella mushrooms", CREMINI_DESCRIPTION, MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("baby potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("baby potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("baby rocket", ARUGULA_DESCRIPTION, SPINACH_DESCRIPTION),
		NutritionSeedAlias("baby salad greens", SPINACH_DESCRIPTION, ICEBERG_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("bacon", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("bacon lardons", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("bacon slices", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("bacon wide strips", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("bag baby salad greens", SPINACH_DESCRIPTION, ICEBERG_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("bag mixed salad", ICEBERG_LETTUCE_DESCRIPTION, ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("baguette", BAGUETTE_DESCRIPTION, ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias("baking potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("baking powder", BAKING_POWDER_DESCRIPTION),
		NutritionSeedAlias("baking soda", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("balsamic vinegar", BALSAMIC_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("basil", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("basmati rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("bean sprouts", BEANSPROUT_DESCRIPTION),
		NutritionSeedAlias("beansprouts", BEANSPROUT_DESCRIPTION),
		NutritionSeedAlias("beef chuck", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("beef chuck cubes", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("beef stewing meat", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("beetroot", BEET_DESCRIPTION),
		NutritionSeedAlias("beets", BEET_DESCRIPTION),
		NutritionSeedAlias("bell pepper", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("best dark chocolate", DARK_CHOCOLATE_DESCRIPTION, DARK_CHOCOLATE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("bicarbonate of soda", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("bird eye chiles", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("bittersweet chocolate", DARK_CHOCOLATE_DESCRIPTION, DARK_CHOCOLATE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"bittersweet chocolate cacao",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
			COCOA_DESCRIPTION,
		),
		NutritionSeedAlias("black beans", BLACK_BEAN_DESCRIPTION),
		NutritionSeedAlias("black coffee", INSTANT_COFFEE_DESCRIPTION, INSTANT_COFFEE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("black peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("black pudding", BLOOD_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("blanched almond flour", ALMOND_DESCRIPTION),
		NutritionSeedAlias("bleached cake flour", CAKE_FLOUR_DESCRIPTION, FLOUR_DESCRIPTION),
		NutritionSeedAlias("block extra firm tofu", TOFU_DESCRIPTION),
		NutritionSeedAlias("bok choy", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias(
			"bone skin chicken thighs",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast halves",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts bite",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts bite sized",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("breadcrumb", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("brined capers", CAPERS_DESCRIPTION),
		NutritionSeedAlias("brioche bun", BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias("broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("brown mustard seeds", MUSTARD_SEED_DESCRIPTION),
		NutritionSeedAlias("brown onion", ONION_DESCRIPTION),
		NutritionSeedAlias("brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("bunch flat leaf parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("burger bun", BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias("butter beans", BUTTER_BEAN_DESCRIPTION),
		NutritionSeedAlias("butternut squash", BUTTERNUT_SQUASH_DESCRIPTION),
		NutritionSeedAlias("butternut squash puree", BUTTERNUT_SQUASH_DESCRIPTION),
		NutritionSeedAlias("cabbage", CABBAGE_DESCRIPTION),
		NutritionSeedAlias("canned water chestnuts", WATER_CHESTNUT_DESCRIPTION, WATER_CHESTNUT_RAW_DESCRIPTION),
		NutritionSeedAlias("cannellini bean", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("cannellini beans", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("can pumpkin", PUMPKIN_CANNED_DESCRIPTION),
		NutritionSeedAlias("cannellini white kidney beans", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("chinese broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("cooked bacon slices", BACON_DESCRIPTION, BACON_UNPREPARED_DESCRIPTION),
		NutritionSeedAlias("filo pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("frozen cranberries", CRANBERRY_DESCRIPTION),
		NutritionSeedAlias("garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic crosswise", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic whole", GARLIC_DESCRIPTION),
		NutritionSeedAlias("passata tomato puree", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("phyllo pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("plum tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("pumpkin puree", PUMPKIN_CANNED_DESCRIPTION),
		NutritionSeedAlias("canola oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("capers", CAPERS_DESCRIPTION),
		NutritionSeedAlias("cardamom pod", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("cardamom pods", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("carrot", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots inch rounds", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots inch thick rounds", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots the holes box grater", CARROT_DESCRIPTION),
		NutritionSeedAlias("caster sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("cauliflower", CAULIFLOWER_DESCRIPTION),
		NutritionSeedAlias("cavolo nero", KALE_DESCRIPTION),
		NutritionSeedAlias("cayenne pepper", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("celeriac", CELERIAC_DESCRIPTION),
		NutritionSeedAlias("celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery rib", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery ribs", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery stalk", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery stalks", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery stick", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery thirds", CELERY_DESCRIPTION),
		NutritionSeedAlias("cheddar cheese", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("cherry tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("chestnut mushroom", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias(
			"chicken breast",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken broth", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"chicken mince",
			CHICKEN_MINCE_DESCRIPTION,
			CHICKEN_MINCE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock cube", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"chicken thigh bone",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken thigh bone removed",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chickpea", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("chickpea flour", CHICKPEA_FLOUR_DESCRIPTION),
		NutritionSeedAlias("chickpeas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("chili flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chili powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chilli", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chilli flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chilli oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("chilli powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chilli powder couple palmfuls", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chilli powder palmfuls", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chillies", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias(
			"chinese black vinegar",
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("chipotle chilli flake", CAYENNE_DESCRIPTION, CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias(
			"chipotle paste",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias("chopped tomatoes", TOMATO_DICED_DESCRIPTION),
		NutritionSeedAlias("chorizo", CHORIZO_DESCRIPTION),
		NutritionSeedAlias("ciabatta", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias("cider vinegar", CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("cilantro", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("corainder", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("culantro", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("clam juice", CLAM_JUICE_DESCRIPTION, FISH_SAUCE_DESCRIPTION),
		NutritionSeedAlias("clove garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias("cloves garlic whole", GARLIC_DESCRIPTION),
		NutritionSeedAlias("coarse salt", SALT_DESCRIPTION),
		NutritionSeedAlias("coarse salt black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("coconut yoghurt", COCONUT_YOGURT_DESCRIPTION),
		NutritionSeedAlias("coconut yogurt", COCONUT_YOGURT_DESCRIPTION),
		NutritionSeedAlias("cointreau", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias(
			"cooked roast chicken bite size",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cooking oil spray", COOKING_SPRAY_DESCRIPTION, CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("cooking spray", COOKING_SPRAY_DESCRIPTION, CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("corn oil", CORN_OIL_DESCRIPTION, CORN_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("corn on the cob", CORN_COB_DESCRIPTION),
		NutritionSeedAlias("corn tortilla", CORN_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("corn tortillas", CORN_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("cornflour", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias("cornstarch", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias("courgette", ZUCCHINI_DESCRIPTION),
		NutritionSeedAlias("cr me fra che", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("cracked black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("creme fraiche", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("cremini mushrooms", CREMINI_DESCRIPTION, MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("crispy chilli oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("crumbled queso fresco", QUESO_FRESCO_DESCRIPTION, FETA_DESCRIPTION),
		NutritionSeedAlias("crushed red pepper flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("cucumber", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("cucumber lengthwise", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("cumin seed", CUMIN_DESCRIPTION),
		NutritionSeedAlias("curry powder", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("dairy free yoghurt", COCONUT_YOGURT_DESCRIPTION, YOGURT_DESCRIPTION),
		NutritionSeedAlias("dark brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("dark sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("dark soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("diamond crystal", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt seasoning", SALT_DESCRIPTION),
		NutritionSeedAlias("dijon mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("dill fronds", DILL_DESCRIPTION),
		NutritionSeedAlias("distilled white vinegar", WHITE_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("double cream", CREAM_DESCRIPTION, CREAM_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("double tomato paste", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("drained cooked chickpeas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("dried oregano", OREGANO_DESCRIPTION),
		NutritionSeedAlias("dry marsala wine", DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("dry red wine", RED_WINE_DESCRIPTION),
		NutritionSeedAlias("dry red wine italian wine", RED_WINE_DESCRIPTION),
		NutritionSeedAlias("dry sherry", DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("dry vermouth", WHITE_WINE_DESCRIPTION, DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("dry white wine", WHITE_WINE_DESCRIPTION),
		NutritionSeedAlias("dutch-process cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("egg", EGG_DESCRIPTION),
		NutritionSeedAlias("egg brush pastry", EGG_DESCRIPTION),
		NutritionSeedAlias("egg noodle", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("egg white", EGG_WHITE_DESCRIPTION, EGG_WHITE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("egg whites", EGG_WHITE_DESCRIPTION, EGG_WHITE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("egg yolk", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("egg yolks", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("egg yolks from eggs", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("eggplant", EGGPLANT_DESCRIPTION),
		NutritionSeedAlias("eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("english mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("envelope instant yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("envelope instant yeast tsp", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("evaporated milk", EVAPORATED_MILK_DESCRIPTION),
		NutritionSeedAlias("extra sharp cheddar", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("extra sharp cheddar holes grater", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("extra sharp cheddar the holes box grater", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias(
			"extra virgin olive oil",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"extra virgin olive oil liberal",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"extra-virgin olive oil",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("easy long grain rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"british chicken breast fillets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("fennel seed toasted", FENNEL_SEED_DESCRIPTION),
		NutritionSeedAlias("feta", FETA_DESCRIPTION),
		NutritionSeedAlias("fine salt", SALT_DESCRIPTION),
		NutritionSeedAlias("fire roasted tomatoes", TOMATO_DICED_DESCRIPTION, TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("firm tofu", TOFU_DESCRIPTION),
		NutritionSeedAlias("fish sauce", FISH_SAUCE_DESCRIPTION),
		NutritionSeedAlias("flaked almond", ALMOND_DESCRIPTION),
		NutritionSeedAlias("flaky salt", SALT_DESCRIPTION),
		NutritionSeedAlias("flaky sea salt", SALT_DESCRIPTION),
		NutritionSeedAlias("flaky sea salt black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("flat leaf parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("flat-leaf parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("flatbread", FLATBREAD_DESCRIPTION),
		NutritionSeedAlias("flour tortilla", FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("flour tortillas", FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("fra che", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("fraiche", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("french green lentils", LENTIL_DESCRIPTION),
		NutritionSeedAlias("french green lentils picked through", LENTIL_DESCRIPTION),
		NutritionSeedAlias("fresh coriander", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("fresh ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("fresh lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("fresh lime juice", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("fresh mint", MINT_DESCRIPTION),
		NutritionSeedAlias("freshly ground black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("frozen blueberries", BLUEBERRY_FROZEN_DESCRIPTION, BLUEBERRY_DESCRIPTION),
		NutritionSeedAlias("frozen pearl onions thawed", ONION_DESCRIPTION),
		NutritionSeedAlias("frozen peas", GREEN_PEAS_FALLBACK_DESCRIPTION, GREEN_PEAS_DESCRIPTION),
		NutritionSeedAlias("frozen peas thawed", GREEN_PEAS_FALLBACK_DESCRIPTION, GREEN_PEAS_DESCRIPTION),
		NutritionSeedAlias("galangal", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("gammon", HAM_DESCRIPTION),
		NutritionSeedAlias("garlic clove", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic left whole", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic microplane", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic powder", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("garlic powder palmful", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("garlic tablespoon", GARLIC_DESCRIPTION),
		NutritionSeedAlias("ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias(
			"golden syrup",
			MAPLE_SYRUP_DESCRIPTION,
			AGAVE_SYRUP_DESCRIPTION,
			CORN_SYRUP_DESCRIPTION,
		),
		NutritionSeedAlias("good mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("good olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("good quality marinara sauce", MARINARA_DESCRIPTION, TOMATO_SAUCE_DESCRIPTION),
		NutritionSeedAlias("gram flour", CHICKPEA_FLOUR_DESCRIPTION),
		NutritionSeedAlias("granulated sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("granulated sugar 200", SUGAR_DESCRIPTION),
		NutritionSeedAlias("granulated sugar 300", SUGAR_DESCRIPTION),
		NutritionSeedAlias("grated parmesan", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("green bell pepper", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("green bell pepper stemmed", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("green chilli", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("green chillies", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("green onion", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("green pepper", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("ground black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("ground cinnamon", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("ground clove", CLOVE_SPICE_DESCRIPTION),
		NutritionSeedAlias("ground cloves", CLOVE_SPICE_DESCRIPTION),
		NutritionSeedAlias("ground coriander", CORIANDER_SEED_DESCRIPTION),
		NutritionSeedAlias("ground cumin", CUMIN_DESCRIPTION),
		NutritionSeedAlias("ground ginger", GINGER_GROUND_DESCRIPTION),
		NutritionSeedAlias("ground turmeric", TURMERIC_DESCRIPTION),
		NutritionSeedAlias("groundnut oil", PEANUT_OIL_DESCRIPTION, PEANUT_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("gruy", GRUYERE_DESCRIPTION, GRUYERE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("gruy cheese cups", GRUYERE_DESCRIPTION, GRUYERE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("gruy re", GRUYERE_DESCRIPTION, GRUYERE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("gruyere", GRUYERE_DESCRIPTION, GRUYERE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("guinness", BEER_DESCRIPTION),
		NutritionSeedAlias("guinness stout", BEER_DESCRIPTION),
		NutritionSeedAlias("halloumi", HALLOUMI_DESCRIPTION),
		NutritionSeedAlias("hass avocado", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("head cauliflower", CAULIFLOWER_DESCRIPTION),
		NutritionSeedAlias("head iceberg lettuce", ICEBERG_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("heavy cream", CREAM_DESCRIPTION, CREAM_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("heavy cream cup", CREAM_DESCRIPTION, CREAM_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"high protein all purpose flour",
			FLOUR_DESCRIPTION,
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"high quality sandwich bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION,
		),
		NutritionSeedAlias("iceberg lettuce", ICEBERG_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("inch knob ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias(
			"instant espresso powder",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("instant yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("jalapeno", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jalapeno pepper", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jalapenos", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jarred red pepper", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("kalamata olives", OLIVE_DESCRIPTION),
		NutritionSeedAlias("kashmiri chile powder", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("ketchup", KETCHUP_DESCRIPTION),
		NutritionSeedAlias("king prawn", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("king prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("kosher salt black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("kosher salt pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("lacinato", KALE_DESCRIPTION),
		NutritionSeedAlias("lacinato kale", KALE_DESCRIPTION),
		NutritionSeedAlias("lamb mince", LAMB_MINCE_DESCRIPTION),
		NutritionSeedAlias("leek", LEEK_DESCRIPTION),
		NutritionSeedAlias("leftover turkey", TURKEY_DESCRIPTION, TURKEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("lemon", LEMON_DESCRIPTION),
		NutritionSeedAlias("lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemon twists", LEMON_DESCRIPTION),
		NutritionSeedAlias("lemon wedges", LEMON_DESCRIPTION),
		NutritionSeedAlias("lemon zest lemon juice lemon", LEMON_JUICE_DESCRIPTION, LEMON_PEEL_DESCRIPTION),
		NutritionSeedAlias("lemon zest tablespoon lemon juice from lemon", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemon zest tablespoons lemon juice from lemon", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemongrass puree", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias("lemons", LEMON_DESCRIPTION),
		NutritionSeedAlias("lentils", LENTIL_DESCRIPTION),
		NutritionSeedAlias("light brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("light crisp lager narragansett", BEER_DESCRIPTION),
		NutritionSeedAlias("light soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("lime", LIME_DESCRIPTION),
		NutritionSeedAlias("lime juice from lime", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("lime juice from limes", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("lime wedges", LIME_DESCRIPTION),
		NutritionSeedAlias("limes", LIME_DESCRIPTION),
		NutritionSeedAlias("linguine", PASTA_DESCRIPTION),
		NutritionSeedAlias(
			"long grain brown rice",
			BROWN_RICE_DESCRIPTION,
			BROWN_RICE_FALLBACK_DESCRIPTION,
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("long grain rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("loosely packed flat leaf parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("low sodium vegetable broth", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("low-sodium chicken broth", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("low-sodium chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("low-sodium soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("low-sodium vegetable broth", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("mangetout", EDIBLE_PODDED_PEA_DESCRIPTION),
		NutritionSeedAlias("mango", MANGO_DESCRIPTION),
		NutritionSeedAlias("maple syrup", MAPLE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("maris piper potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("maris piper potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("marmite", MARMITE_DESCRIPTION),
		NutritionSeedAlias("marsala", DESSERT_WINE_SWEET_DESCRIPTION, DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("marsala wine", DESSERT_WINE_SWEET_DESCRIPTION, DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("mature spinach", SPINACH_DESCRIPTION),
		NutritionSeedAlias("mezze rigatoni", PASTA_DESCRIPTION),
		NutritionSeedAlias("mint", MINT_DESCRIPTION),
		NutritionSeedAlias("miso", MISO_DESCRIPTION),
		NutritionSeedAlias("miso paste", MISO_DESCRIPTION),
		NutritionSeedAlias("mixed salad leaf", ICEBERG_LETTUCE_DESCRIPTION, ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("morton kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("mussels debearded", MUSSEL_DESCRIPTION),
		NutritionSeedAlias("natural yoghurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("nduja", CHORIZO_DESCRIPTION, SALAMI_DESCRIPTION),
		NutritionSeedAlias("neutral oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("new potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("new potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("nonfat dry milk powder", DRY_MILK_DESCRIPTION),
		NutritionSeedAlias("nonstick cooking spray", COOKING_SPRAY_DESCRIPTION, CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("nutmeg", NUTMEG_DESCRIPTION),
		NutritionSeedAlias("oat milk", OAT_MILK_DESCRIPTION, OAT_MILK_SURVEY_DESCRIPTION),
		NutritionSeedAlias("olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("olive oil additional", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"olive oil cooking spray",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("onion", ONION_DESCRIPTION),
		NutritionSeedAlias("onion powder palmful", ONION_POWDER_DESCRIPTION),
		NutritionSeedAlias("onion wedges", ONION_DESCRIPTION),
		NutritionSeedAlias("onions", ONION_DESCRIPTION),
		NutritionSeedAlias("orange", ORANGE_DESCRIPTION),
		NutritionSeedAlias("orange liqueur", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("orzo", PASTA_DESCRIPTION),
		NutritionSeedAlias("packed dark brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("packed light brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("packed culantro", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("pak choi", BOK_CHOY_DESCRIPTION),
		NutritionSeedAlias("pancetta italian bacon", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("panko breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("parmesan cheese cup packed", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parmesan cups", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("passata", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("passata pomodoro", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("pasta dough inch wide ribbons", PASTA_DESCRIPTION, EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("pecorino cheese", ROMANO_DESCRIPTION),
		NutritionSeedAlias("pecorino romano", ROMANO_DESCRIPTION),
		NutritionSeedAlias("penne pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("peppermint extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("petit pois", GREEN_PEAS_DESCRIPTION, GREEN_PEAS_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("pickled jalapeno", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("pickled jalapenos", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("pine nuts", PINE_NUT_DESCRIPTION),
		NutritionSeedAlias("pink himalayan salt", SALT_DESCRIPTION),
		NutritionSeedAlias("pinto beans", PINTO_BEAN_DESCRIPTION),
		NutritionSeedAlias("pita", PITA_DESCRIPTION),
		NutritionSeedAlias("pitta", PITA_DESCRIPTION),
		NutritionSeedAlias("plain flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("plain white flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("plant based mince", PLANT_MINCE_DESCRIPTION),
		NutritionSeedAlias("plantain", PLANTAIN_DESCRIPTION),
		NutritionSeedAlias("plum tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("pomegranate seeds", POMEGRANATE_DESCRIPTION),
		NutritionSeedAlias("pork mince", PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias("potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("prawn", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("prawns deveined", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pure maple syrup", MAPLE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("pure vanilla extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("quarts neutral oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("quarts water", WATER_DESCRIPTION, WATER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("rack louis ribs", PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias("rapeseed oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("ready rolled filo pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("red bell pepper", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red cabbage", CABBAGE_RED_DESCRIPTION),
		NutritionSeedAlias("red chilli", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red chilli flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red chilli powder", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red chillies", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red onion", ONION_DESCRIPTION),
		NutritionSeedAlias("red onions", ONION_DESCRIPTION),
		NutritionSeedAlias("red pepper", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red pepper flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red pepper flakes heavier extra spicy", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red pepper strips", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red peppers", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("rice noodle sheets", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("rice vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("rice wine vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("ripe avocado", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("ripe tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("rocket", ARUGULA_DESCRIPTION, SPINACH_DESCRIPTION),
		NutritionSeedAlias("roll brioche bun", BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias("romaine lettuce", ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("rosemary", ROSEMARY_DESCRIPTION),
		NutritionSeedAlias("rosemary sprigs", ROSEMARY_DESCRIPTION),
		NutritionSeedAlias("runny honey", HONEY_DESCRIPTION),
		NutritionSeedAlias("russet potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("salt", SALT_DESCRIPTION),
		NutritionSeedAlias("salt additional seasoning", SALT_DESCRIPTION),
		NutritionSeedAlias("salt black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("salt pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("sausage meat", PORK_SAUSAGE_DESCRIPTION, PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias("scallion", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions white green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions white green parts", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions white light green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions white light green parts", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scotch bonnet chilli", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("sea salt", SALT_DESCRIPTION),
		NutritionSeedAlias("sea salt black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("sea salt grey salt", SALT_DESCRIPTION),
		NutritionSeedAlias("seasoned breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("seeds cardamom pods", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("seeds from cardamom pods using pestle mortar", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("self raising flour", SELF_RISING_FLOUR_DESCRIPTION),
		NutritionSeedAlias("self raising flour sifted", SELF_RISING_FLOUR_DESCRIPTION),
		NutritionSeedAlias("self-raising flour", SELF_RISING_FLOUR_DESCRIPTION),
		NutritionSeedAlias("serrano chile", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias("serrano chillies", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias("sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("sesame seeds", SESAME_SEED_DESCRIPTION),
		NutritionSeedAlias("shallot", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shallot tablespoons", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shallots", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shaoxing wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("sichuan peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("single cream", SINGLE_CREAM_DESCRIPTION),
		NutritionSeedAlias("skimmed milk", SKIM_MILK_DESCRIPTION),
		NutritionSeedAlias(
			"skinless chicken thigh",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("slices bacon", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("smoked bacon", BACON_DESCRIPTION),
		NutritionSeedAlias("smoked paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("soft boiled egg", EGG_DESCRIPTION),
		NutritionSeedAlias("soft brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("sour cream", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"sourdough starter",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("spanish onion", ONION_DESCRIPTION),
		NutritionSeedAlias("spanish onions", ONION_DESCRIPTION),
		NutritionSeedAlias("sprigs rosemary", ROSEMARY_DESCRIPTION),
		NutritionSeedAlias("spring onion", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions lengthways", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions white green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions white green parts", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("stalk celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("stalks celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("star anise", ANISE_DESCRIPTION),
		NutritionSeedAlias("stick celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("stilton cheese", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("stock cube", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("store bought passata pomodoro", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("strawberries", STRAWBERRY_DESCRIPTION),
		NutritionSeedAlias("streaky bacon", BACON_DESCRIPTION, BACON_UNPREPARED_DESCRIPTION),
		NutritionSeedAlias(
			"strong white bread flour",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("sugar diamonds", SUGAR_DESCRIPTION),
		NutritionSeedAlias("sumac", LEMON_PEEL_DESCRIPTION, PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("sweet onion", ONION_DESCRIPTION),
		NutritionSeedAlias("sweet paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("sweet potato", SWEET_POTATO_DESCRIPTION),
		NutritionSeedAlias("tabasco original sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("tablespoons neutral oil 205", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("tahini", TAHINI_DESCRIPTION),
		NutritionSeedAlias(
			"tamarind paste",
			TAMARIND_DESCRIPTION,
			TAMARIND_FALLBACK_DESCRIPTION,
			LEMON_JUICE_DESCRIPTION,
		),
		NutritionSeedAlias("teaspoon diamond crystal kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("teaspoon granulated sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("teaspoon olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tenderstem broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("thai black soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias(
			"thai oyster sauce",
			OYSTER_SAUCE_DESCRIPTION,
			OYSTER_SAUCE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("thai thin soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias(
			"the operative british chicken breast fillets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"the operative easy cook long grain rice",
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("toasted sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("tomato ketchup", KETCHUP_DESCRIPTION),
		NutritionSeedAlias("tomatillo", TOMATILLO_DESCRIPTION, TOMATILLO_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tomatillos", TOMATILLO_DESCRIPTION, TOMATILLO_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tomatillos husk", TOMATILLO_DESCRIPTION, TOMATILLO_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tomatillos husked", TOMATILLO_DESCRIPTION, TOMATILLO_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tomato passata", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("tomato paste", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomato puree", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomato purée", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("tortilla", CORN_TORTILLA_DESCRIPTION, FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("tsp diamond crystal", SALT_DESCRIPTION),
		NutritionSeedAlias("tsp sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("turmeric", TURMERIC_DESCRIPTION),
		NutritionSeedAlias("unsalted butter", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter 170", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter brushing", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter cooled", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter cubes", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter pan", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter tablespoon", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"unsalted butter tablespoons",
			BUTTER_DESCRIPTION,
			BUTTER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("unsalted butter thick", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"unseasoned rice vinegar",
			WHITE_VINEGAR_DESCRIPTION,
			CIDER_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("vanilla bean paste", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias(
			"vegan butter",
			MARGARINE_DESCRIPTION,
			BUTTER_DESCRIPTION,
			BUTTER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("vegetable broth", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vegetable oil", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable oil deep", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable rapeseed", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable stock", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vegetable stock cube", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vine ripened tomatoes tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("vine tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("vine tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("walnuts toasted", WALNUT_DESCRIPTION),
		NutritionSeedAlias("warm water", WATER_DESCRIPTION, WATER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("white cabbage", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("white miso", MISO_DESCRIPTION),
		NutritionSeedAlias("white miso paste", MISO_DESCRIPTION),
		NutritionSeedAlias("white onion", ONION_DESCRIPTION),
		NutritionSeedAlias("white pepper", WHITE_PEPPER_DESCRIPTION),
		NutritionSeedAlias("white sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("white wine vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("whole black mustard seeds", MUSTARD_SEED_DESCRIPTION),
		NutritionSeedAlias("whole black peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("whole coriander seeds", CORIANDER_SEED_DESCRIPTION),
		NutritionSeedAlias("whole cumin seeds", CUMIN_DESCRIPTION),
		NutritionSeedAlias("whole grain mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("whole chicken", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias("whole milk", MILK_DESCRIPTION),
		NutritionSeedAlias("whole pecans", PECAN_DESCRIPTION),
		NutritionSeedAlias(
			"whole split chicken breasts bone skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("worcestershire sauce eyeball", WORCESTERSHIRE_DESCRIPTION),
		NutritionSeedAlias("yellow bell pepper", YELLOW_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("yellow onion", ONION_DESCRIPTION),
		NutritionSeedAlias("yellow pepper", YELLOW_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("yellow potatoes inch", POTATO_DESCRIPTION),
		NutritionSeedAlias("za'atar", OREGANO_DESCRIPTION),
		NutritionSeedAlias("zaatar", OREGANO_DESCRIPTION),
		NutritionSeedAlias("zest juice lemon", LEMON_JUICE_DESCRIPTION, LEMON_PEEL_DESCRIPTION),
		NutritionSeedAlias("zest juice orange", ORANGE_JUICE_DESCRIPTION, ORANGE_DESCRIPTION),
		NutritionSeedAlias("block halloumi", HALLOUMI_DESCRIPTION),
		NutritionSeedAlias("boneless skinless salmon", SALMON_DESCRIPTION),
		NutritionSeedAlias("boneless skinless salmon filets", SALMON_DESCRIPTION),
		NutritionSeedAlias("bottle clam juice", CLAM_JUICE_DESCRIPTION, FISH_SAUCE_DESCRIPTION),
		NutritionSeedAlias("coriander stalks", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("crumbled cotija cheese", QUESO_FRESCO_DESCRIPTION, FETA_DESCRIPTION),
		NutritionSeedAlias("curry leaf", BAY_LEAF_DESCRIPTION, CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("gherkin", PICKLE_DESCRIPTION),
		NutritionSeedAlias("graham cracker", GRAHAM_DESCRIPTION),
		NutritionSeedAlias("graham cracker sheets", GRAHAM_DESCRIPTION),
		NutritionSeedAlias("head garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("kaffir lime leaf", LIME_DESCRIPTION, BAY_LEAF_DESCRIPTION),
		NutritionSeedAlias("matcha powder", GREEN_TEA_DESCRIPTION, INSTANT_COFFEE_DESCRIPTION),
		NutritionSeedAlias("orange blossom water", ORANGE_JUICE_DESCRIPTION, ORANGE_DESCRIPTION),
		NutritionSeedAlias("parsnips", PARSNIP_DESCRIPTION),
		NutritionSeedAlias("parsnips half", PARSNIP_DESCRIPTION),
		NutritionSeedAlias("pineapple chunks", PINEAPPLE_DESCRIPTION),
		NutritionSeedAlias("star anise pods", ANISE_DESCRIPTION),
		NutritionSeedAlias("scallions whites greens", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onion white green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions whites greens", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("red chiles", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red thai chile", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("california chiles seeds", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("sheet puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pouch cooked rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("plant based burger", VEGGIE_BURGER_DESCRIPTION, PLANT_MINCE_DESCRIPTION),
		NutritionSeedAlias("parmigiano reggiano cheese cup", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("instant ramen noodles seasoning", EGG_NOODLE_DESCRIPTION, PASTA_DESCRIPTION),
		NutritionSeedAlias("radicchio", RADICCHIO_DESCRIPTION),
		NutritionSeedAlias("unflavored gelatin", GELATIN_DESCRIPTION),
		NutritionSeedAlias("water chestnuts", WATER_CHESTNUT_DESCRIPTION, WATER_CHESTNUT_RAW_DESCRIPTION),
		NutritionSeedAlias("zucchini", ZUCCHINI_DESCRIPTION),
		NutritionSeedAlias("all purpose flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("all purpose flour extra flour rolling", FLOUR_DESCRIPTION),
		NutritionSeedAlias("ancho chile hand", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("ancho chiles seeds", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("asparagus ends thirds", ASPARAGUS_DESCRIPTION),
		NutritionSeedAlias("bacon cooked until crispy", BACON_DESCRIPTION, BACON_UNPREPARED_DESCRIPTION),
		NutritionSeedAlias("bay leaf", BAY_LEAF_DESCRIPTION),
		NutritionSeedAlias("beefsteak tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("bone beef chuck", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias(
			"bone skin chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"bone skinless chicken thighs",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("boneless center salmon fillet", SALMON_DESCRIPTION),
		NutritionSeedAlias(
			"boneless chicken breast skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless chicken breasts skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast half cutlets pounded",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast pounded thickness",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast split",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts pounded flat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts tenderloins",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken thighs poked fork",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("british free range woodland egg", EGG_DESCRIPTION),
		NutritionSeedAlias("brioche flatbread", FLATBREAD_DESCRIPTION, BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias("butter extra greasing", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("canned baby corn", CORN_COB_DESCRIPTION),
		NutritionSeedAlias(
			"canned chipotles adobo",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias("canned unsweetened coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("unsweetened coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("cardamom seeds", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("carrots batons", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots julienned", CARROT_DESCRIPTION),
		NutritionSeedAlias("caster", SUGAR_DESCRIPTION),
		NutritionSeedAlias("caster superfine sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("cayenne pepper powder", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("celery sticks", CELERY_DESCRIPTION),
		NutritionSeedAlias(
			"chicken breast cutlets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts pounded thin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts skinned boned",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken thighs bone skin",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chinese light soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("chinese rice wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias(
			"chipotle chilli",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias("chipotle powder", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chunk ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("cinnamon stick", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("ckickpeas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("clarified butter", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("clear honey", HONEY_DESCRIPTION),
		NutritionSeedAlias("cloves garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("cooked jasmine rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"cooked roast chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cooked white rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("coriander seeds", CORIANDER_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"country white bread crusts",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("courgettes", ZUCCHINI_DESCRIPTION),
		NutritionSeedAlias("cremini", CREMINI_DESCRIPTION, MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("crumbled blue cheese", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("crusty baguette", BAGUETTE_DESCRIPTION, ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias("curly kale", KALE_DESCRIPTION),
		NutritionSeedAlias("dark asian sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("dark brown soft sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("deli ham", HAM_DESCRIPTION),
		NutritionSeedAlias("demerara sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("deveined shrimp", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("dry breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("dry mustard powder", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("dutch process cocoa", COCOA_DESCRIPTION),
		NutritionSeedAlias("dutch processed cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("egg noodles", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("english cucumber", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("envelope active dry yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("extra sharp cheddar cheese", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias(
			"extra virgin olive oil turns the pan",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("flaky sea salt sprinkling", SALT_DESCRIPTION),
		NutritionSeedAlias("flat parsley", PARSLEY_DESCRIPTION, PARSLEY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("fresno chile", HOT_CHILI_RED_DESCRIPTION, JALAPENO_DESCRIPTION),
		NutritionSeedAlias("frozen corn kernels", CORN_COB_DESCRIPTION),
		NutritionSeedAlias("frozen petit pois", GREEN_PEAS_FALLBACK_DESCRIPTION, GREEN_PEAS_DESCRIPTION),
		NutritionSeedAlias("frozen puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("full fat coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("full fat plain yogurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("garlic granules", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("green cabbage", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("green chile", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("green chiles", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("green chilies", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("green lentils", LENTIL_DESCRIPTION),
		NutritionSeedAlias("green onions", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("grey salt", SALT_DESCRIPTION),
		NutritionSeedAlias("habanero chile", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("halloumi cheese block", HALLOUMI_DESCRIPTION),
		NutritionSeedAlias("hard boiled eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("haricot beans", CANNELLINI_DESCRIPTION, BUTTER_BEAN_DESCRIPTION),
		NutritionSeedAlias("herbes provence", OREGANO_DESCRIPTION),
		NutritionSeedAlias("hot green chiles", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("hot italian sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("hungarian hot paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("ice water", WATER_DESCRIPTION, WATER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("japanese mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("jarred jalapeno", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("juice half lemon", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("juice lime zest", LIME_JUICE_DESCRIPTION, LIME_DESCRIPTION),
		NutritionSeedAlias("juice zest lime", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("kosher salt seasoning", SALT_DESCRIPTION),
		NutritionSeedAlias("leftover ham", HAM_DESCRIPTION),
		NutritionSeedAlias("leftover rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"leftover roast chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("lime juice zest", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("long grain white rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("low salt chicken broth", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("low sodium chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("madeira wine", DESSERT_WINE_DRY_DESCRIPTION, RED_WINE_DESCRIPTION),
		NutritionSeedAlias("matchstick carrots", CARROT_DESCRIPTION),
		NutritionSeedAlias("mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"mexican crema",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("mild green chiles", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("mild green chillies", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("mild italian sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("mirin", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("muscovado sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("napa cabbage", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("natural almond flour", ALMOND_DESCRIPTION),
		NutritionSeedAlias("natural cocoa", COCOA_DESCRIPTION),
		NutritionSeedAlias("neutral cooking oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("neutral flavored oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("nonfat milk powder", DRY_MILK_DESCRIPTION),
		NutritionSeedAlias("nonstick spray", COOKING_SPRAY_DESCRIPTION, CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("onion granules", ONION_POWDER_DESCRIPTION),
		NutritionSeedAlias("orange bell pepper", YELLOW_BELL_PEPPER_DESCRIPTION, RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("oyster mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("pancetta", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("parma ham", HAM_DESCRIPTION),
		NutritionSeedAlias("parmesan cheese", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("pasilla chiles", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("pencil asparagus", ASPARAGUS_DESCRIPTION),
		NutritionSeedAlias("plain yoghurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("poblano chiles", HOT_CHILI_GREEN_DESCRIPTION, BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("powdered gelatin", GELATIN_DESCRIPTION),
		NutritionSeedAlias("powdered ginger", GINGER_GROUND_DESCRIPTION),
		NutritionSeedAlias("prepared grainy mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("prosciutto", HAM_DESCRIPTION),
		NutritionSeedAlias("raw sesame seeds", SESAME_SEED_DESCRIPTION),
		NutritionSeedAlias("red bell peppers", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("reduced salt chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("reduced sodium chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("refried beans", PINTO_BEAN_DESCRIPTION),
		NutritionSeedAlias("rib celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("ripe mango", MANGO_DESCRIPTION),
		NutritionSeedAlias("risotto rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("roma tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"rotisserie chicken breast",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("runner bean", EDIBLE_PODDED_PEA_DESCRIPTION),
		NutritionSeedAlias("russet potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("sake", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("salt sprinkling", SALT_DESCRIPTION),
		NutritionSeedAlias("seedless cucumber", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("serrano ham", HAM_DESCRIPTION),
		NutritionSeedAlias("shrimp", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("sichuan chili flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias(
			"skin bone chicken breast",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"skinless boneless chicken breast fillets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"skinless boneless chicken breast pounded",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("slivered almonds", ALMOND_DESCRIPTION),
		NutritionSeedAlias("small green chiles", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("small new potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("soft burger buns", BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias("soft light brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("soft wheat all purpose flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("spaghetti", PASTA_DESCRIPTION),
		NutritionSeedAlias("spanish chorizo", CHORIZO_DESCRIPTION),
		NutritionSeedAlias("spanish paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias(
			"split chicken breasts bone skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("squeeze lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias(
			"stale white bread crusts",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("stewed tomatoes", TOMATO_DICED_DESCRIPTION),
		NutritionSeedAlias("sundried tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("sunflower oil", CANOLA_OIL_DESCRIPTION, SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("superfine sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("sweetcorn", CORN_COB_DESCRIPTION),
		NutritionSeedAlias("tabasco chipotle sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("tahini sauce", TAHINI_DESCRIPTION),
		NutritionSeedAlias("tamarind water", TAMARIND_DESCRIPTION, TAMARIND_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"white sandwich bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"white sandwich bread crusts",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("white bread", WHITE_BREAD_DESCRIPTION, WHITE_BREAD_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("white rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("yoghurt", YOGURT_DESCRIPTION),

		)
}
