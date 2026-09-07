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
	private const val AVOCADO_DESCRIPTION = "Avocados, raw, all commercial varieties"
	private const val BACON_DESCRIPTION = "Pork, cured, bacon, cooked, baked"
	private const val BAKING_POWDER_DESCRIPTION =
		"Leavening agents, baking powder, double-acting, sodium aluminum sulfate"
	private const val BAKING_SODA_DESCRIPTION = "Leavening agents, baking soda"
	private const val BALSAMIC_VINEGAR_DESCRIPTION = "Vinegar, balsamic"
	private const val BEANSPROUT_DESCRIPTION = "Mung beans, mature seeds, sprouted, raw"
	private const val BELL_PEPPER_DESCRIPTION = "Peppers, sweet, green, raw"
	private const val BLACK_PEPPER_DESCRIPTION = "Spices, pepper, black"
	private const val BREAD_FLOUR_DESCRIPTION = "Flour, bread, white, enriched, unbleached"
	private const val BREAD_FLOUR_FALLBACK_DESCRIPTION = "Wheat flour, white, bread, enriched"
	private const val BREADCRUMB_DESCRIPTION = "Bread, crumbs, dry, grated, plain"
	private const val BROCCOLI_DESCRIPTION = "Broccoli, raw"
	private const val BROWN_SUGAR_DESCRIPTION = "Sugars, brown"
	private const val CABBAGE_DESCRIPTION = "Cabbage, raw"
	private const val CABBAGE_RED_DESCRIPTION = "Cabbage, red, raw"
	private const val BUTTER_BEAN_DESCRIPTION =
		"Lima beans, large, mature seeds, cooked, boiled, without salt"
	private const val BUTTER_DESCRIPTION = "Butter, stick, unsalted"
	private const val BUTTER_FALLBACK_DESCRIPTION = "Butter, without salt"
	private const val BUTTERNUT_SQUASH_DESCRIPTION = "Squash, winter, butternut, raw"
	private const val CAPERS_DESCRIPTION = "Capers, canned"
	private const val CANNELLINI_DESCRIPTION =
		"Beans, great northern, mature seeds, cooked, boiled, without salt"
	private const val CANOLA_OIL_DESCRIPTION = "Oil, canola"
	private const val CARROT_DESCRIPTION = "Carrots, raw"
	private const val CAULIFLOWER_DESCRIPTION = "Cauliflower, raw"
	private const val CAYENNE_DESCRIPTION = "Spices, pepper, red or cayenne"
	private const val CHEDDAR_DESCRIPTION = "Cheese, cheddar"
	private const val CHICKEN_BREAST_DESCRIPTION = "Chicken, breast, boneless, skinless, raw"
	private const val CHICKEN_BREAST_FALLBACK_DESCRIPTION =
		"Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw"
	private const val CHICKEN_BROTH_DESCRIPTION = "Soup, chicken broth, ready-to-serve"
	private const val CHICKEN_THIGH_DESCRIPTION = "Chicken, thigh, boneless, skinless, raw"
	private const val CHICKEN_THIGH_FALLBACK_DESCRIPTION =
		"Chicken, broilers or fryers, dark meat, thigh, meat only, raw"
	private const val CHICKPEA_DESCRIPTION =
		"Chickpeas (garbanzo beans, bengal gram), mature seeds, cooked, boiled, without salt"
	private const val CHICKPEA_FLOUR_DESCRIPTION = "Chickpea flour (besan)"
	private const val CHILI_POWDER_DESCRIPTION = "Spices, chili powder"
	private const val CELERY_DESCRIPTION = "Celery, raw"
	private const val CIDER_VINEGAR_DESCRIPTION = "Vinegar, cider"
	private const val CILANTRO_DESCRIPTION = "Coriander (cilantro) leaves, raw"
	private const val CINNAMON_DESCRIPTION = "Spices, cinnamon, ground"
	private const val CLOVE_SPICE_DESCRIPTION = "Spices, cloves, ground"
	private const val COCOA_DESCRIPTION = "Cocoa, dry powder, unsweetened"
	private const val COCONUT_MILK_DESCRIPTION =
		"Nuts, coconut milk, canned (liquid expressed from grated meat and water)"
	private const val CORIANDER_SEED_DESCRIPTION = "Spices, coriander seed"
	private const val CORNSTARCH_DESCRIPTION = "Cornstarch"
	private const val CORN_COB_DESCRIPTION = "Corn, sweet, yellow, raw"
	private const val CREAM_DESCRIPTION = "Cream, heavy"
	private const val CREAM_FALLBACK_DESCRIPTION = "Cream, fluid, heavy whipping"
	private const val CUCUMBER_DESCRIPTION = "Cucumber, with peel, raw"
	private const val CUMIN_DESCRIPTION = "Spices, cumin seed"
	private const val CURRY_POWDER_DESCRIPTION = "Spices, curry powder"
	private const val EGG_DESCRIPTION = "Eggs, Grade A, Large, egg whole"
	private const val EGG_NOODLE_DESCRIPTION = "Noodles, egg, dry, enriched"
	private const val EGG_WHITE_DESCRIPTION = "Eggs, Grade A, Large, egg white"
	private const val EGG_WHITE_FALLBACK_DESCRIPTION = "Egg, white, raw, fresh"
	private const val EGG_YOLK_DESCRIPTION = "Eggs, Grade A, Large, egg yolk"
	private const val EGGPLANT_DESCRIPTION = "Eggplant, raw"
	private const val FETA_DESCRIPTION = "Cheese, feta"
	private const val FISH_SAUCE_DESCRIPTION = "Sauce, fish, ready-to-serve"
	private const val FLOUR_DESCRIPTION = "Flour, wheat, all-purpose, enriched, unbleached"
	private const val GARLIC_DESCRIPTION = "Garlic, raw"
	private const val GARLIC_POWDER_DESCRIPTION = "Spices, garlic powder"
	private const val GINGER_FRESH_DESCRIPTION = "Ginger root, raw"
	private const val GINGER_GROUND_DESCRIPTION = "Spices, ginger, ground"
	private const val HALLOUMI_DESCRIPTION = "Cheese, halloumi"
	private const val ITALIAN_BREAD_DESCRIPTION = "Bread, Italian"
	private const val KETCHUP_DESCRIPTION = "Catsup"
	private const val LEMON_DESCRIPTION = "Lemons, raw, without peel"
	private const val LEMON_JUICE_DESCRIPTION = "Lemon juice, raw"
	private const val LEEK_DESCRIPTION = "Leeks, (bulb and lower leaf-portion), raw"
	private const val LENTIL_DESCRIPTION = "Lentils, mature seeds, cooked, boiled, without salt"
	private const val LIME_DESCRIPTION = "Limes, raw"
	private const val LIME_JUICE_DESCRIPTION = "Lime juice, raw"
	private const val MAPLE_SYRUP_DESCRIPTION = "Syrups, maple"
	private const val MILK_DESCRIPTION = "Milk, whole, 3.25% milkfat, with added vitamin D"
	private const val MINT_DESCRIPTION = "Spearmint, fresh"
	private const val MISO_DESCRIPTION = "Miso"
	private const val MUSTARD_DESCRIPTION = "Mustard, prepared, yellow"
	private const val MUSHROOM_DESCRIPTION = "Mushrooms, white, raw"
	private const val OAT_MILK_DESCRIPTION = "Oat milk, unsweetened, plain, refrigerated"
	private const val OLIVE_OIL_DESCRIPTION = "Oil, olive, extra virgin"
	private const val OLIVE_OIL_FALLBACK_DESCRIPTION = "Oil, olive, salad or cooking"
	private const val OLIVE_DESCRIPTION = "Olives, ripe, canned (small-extra large)"
	private const val ONION_DESCRIPTION = "Onions, raw"
	private const val OREGANO_DESCRIPTION = "Spices, oregano, dried"
	private const val ORANGE_DESCRIPTION = "Oranges, raw, all commercial varieties"
	private const val PAPRIKA_DESCRIPTION = "Spices, paprika"
	private const val PARMESAN_DESCRIPTION = "Cheese, parmesan, grated"
	private const val PASTA_DESCRIPTION = "Pasta, dry, unenriched"
	private const val PINE_NUT_DESCRIPTION = "Nuts, pine nuts, dried"
	private const val PINTO_BEAN_DESCRIPTION = "Beans, pinto, mature seeds, cooked, boiled, without salt"
	private const val POMEGRANATE_DESCRIPTION = "Pomegranates, raw"
	private const val PORK_MINCE_DESCRIPTION = "Pork, ground, 84% lean / 16% fat, raw"
	private const val POTATO_DESCRIPTION = "Potatoes, flesh and skin, raw"
	private const val PUFF_PASTRY_DESCRIPTION = "Puff pastry, frozen, ready-to-bake"
	private const val RICE_DESCRIPTION = "Rice, white, long grain, unenriched, raw"
	private const val RICE_FALLBACK_DESCRIPTION = "Rice, white, long-grain, regular, raw, unenriched"
	private const val RED_BELL_PEPPER_DESCRIPTION = "Peppers, sweet, red, raw"
	private const val ROMANO_DESCRIPTION = "Cheese, romano"
	private const val SALT_DESCRIPTION = "Salt, table"
	private const val SELF_RISING_FLOUR_DESCRIPTION = "Wheat flour, white, all-purpose, self-rising, enriched"
	private const val SESAME_OIL_DESCRIPTION = "Oil, sesame, salad or cooking"
	private const val SESAME_SEED_DESCRIPTION = "Seeds, sesame seeds, whole, dried"
	private const val SHALLOT_DESCRIPTION = "Shallots, raw"
	private const val SHRIMP_DESCRIPTION = "Crustaceans, shrimp, raw"
	private const val SOY_SAUCE_DESCRIPTION = "Soy sauce made from soy and wheat (shoyu)"
	private const val SOYBEAN_OIL_DESCRIPTION = "Oil, soybean, salad or cooking"
	private const val SPRING_ONION_DESCRIPTION = "Onions, spring or scallions (includes tops and bulb), raw"
	private const val SUGAR_DESCRIPTION = "Sugars, granulated"
	private const val SWEET_POTATO_DESCRIPTION =
		"Sweet potato, raw, unprepared (Includes foods for USDA's Food Distribution Program)"
	private const val TAHINI_DESCRIPTION =
		"Seeds, sesame butter, tahini, from roasted and toasted kernels (most common type)"
	private const val TOFU_DESCRIPTION = "Tofu, raw, firm, prepared with calcium sulfate"
	private const val TOMATO_CRUSHED_DESCRIPTION = "Tomatoes, crushed, canned"
	private const val TOMATO_DICED_DESCRIPTION = "Tomatoes, canned, red, ripe, diced"
	private const val TOMATO_PASTE_DESCRIPTION =
		"Tomato products, canned, paste, without salt added (Includes foods for USDA's Food Distribution Program)"
	private const val TOMATO_RAW_DESCRIPTION = "Tomatoes, red, ripe, raw, year round average"
	private const val TURMERIC_DESCRIPTION = "Spices, turmeric, ground"
	private const val VANILLA_DESCRIPTION = "Vanilla extract"
	private const val VEGETABLE_BROTH_DESCRIPTION = "Soup, vegetable broth, ready to serve"
	private const val WHITE_VINEGAR_DESCRIPTION = "Vinegar, distilled"
	private const val WHITE_WINE_DESCRIPTION = "Alcoholic beverage, wine, table, white"
	private const val YELLOW_BELL_PEPPER_DESCRIPTION = "Peppers, sweet, yellow, raw"
	private const val ZUCCHINI_DESCRIPTION = "Squash, summer, zucchini, includes skin, raw"

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias("agave nectar", AGAVE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("all-purpose flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("apple cider vinegar", CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("arborio rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("aubergine", EGGPLANT_DESCRIPTION),
		NutritionSeedAlias("avocado", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("baking powder", BAKING_POWDER_DESCRIPTION),
		NutritionSeedAlias("baking soda", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("balsamic vinegar", BALSAMIC_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("basmati rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("bean sprouts", BEANSPROUT_DESCRIPTION),
		NutritionSeedAlias("beansprouts", BEANSPROUT_DESCRIPTION),
		NutritionSeedAlias("bicarbonate of soda", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("bell pepper", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("black peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("breadcrumb", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("butter beans", BUTTER_BEAN_DESCRIPTION),
		NutritionSeedAlias("butternut squash", BUTTERNUT_SQUASH_DESCRIPTION),
		NutritionSeedAlias("cabbage", CABBAGE_DESCRIPTION),
		NutritionSeedAlias("cannellini bean", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("cannellini beans", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("canola oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("capers", CAPERS_DESCRIPTION),
		NutritionSeedAlias("carrot", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots", CARROT_DESCRIPTION),
		NutritionSeedAlias("caster sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("cauliflower", CAULIFLOWER_DESCRIPTION),
		NutritionSeedAlias("cayenne pepper", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("celery", CELERY_DESCRIPTION),
		NutritionSeedAlias("celery stick", CELERY_DESCRIPTION),
		NutritionSeedAlias("cheddar cheese", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("chestnut mushroom", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("cherry tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("chickpea", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("chickpea flour", CHICKPEA_FLOUR_DESCRIPTION),
		NutritionSeedAlias("chickpeas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("ciabatta", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias(
			"chicken breast",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken broth", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chili flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chili powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chilli flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("chilli powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chopped tomatoes", TOMATO_DICED_DESCRIPTION),
		NutritionSeedAlias("cider vinegar", CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("clove garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias("cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("corn on the cob", CORN_COB_DESCRIPTION),
		NutritionSeedAlias("cornflour", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias("cornstarch", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias("courgette", ZUCCHINI_DESCRIPTION),
		NutritionSeedAlias("crushed red pepper flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("cucumber", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("cumin seed", CUMIN_DESCRIPTION),
		NutritionSeedAlias("curry powder", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("dark brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("dark soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("diamond crystal", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("dijon mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("distilled white vinegar", WHITE_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("double cream", CREAM_DESCRIPTION, CREAM_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("double tomato paste", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("drained cooked chickpeas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("dried oregano", OREGANO_DESCRIPTION),
		NutritionSeedAlias("dry white wine", WHITE_WINE_DESCRIPTION),
		NutritionSeedAlias("dutch-process cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("egg", EGG_DESCRIPTION),
		NutritionSeedAlias("egg noodle", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("egg white", EGG_WHITE_DESCRIPTION, EGG_WHITE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("egg whites", EGG_WHITE_DESCRIPTION, EGG_WHITE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("egg yolk", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("egg yolks", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("eggplant", EGGPLANT_DESCRIPTION),
		NutritionSeedAlias("extra virgin olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("extra-virgin olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("feta", FETA_DESCRIPTION),
		NutritionSeedAlias("firm tofu", TOFU_DESCRIPTION),
		NutritionSeedAlias("fish sauce", FISH_SAUCE_DESCRIPTION),
		NutritionSeedAlias("flaked almond", ALMOND_DESCRIPTION),
		NutritionSeedAlias("fresh coriander", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("fresh ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("fresh lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("fresh lime juice", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("fresh mint", MINT_DESCRIPTION),
		NutritionSeedAlias("freshly ground black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("garlic clove", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic powder", GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("gram flour", CHICKPEA_FLOUR_DESCRIPTION),
		NutritionSeedAlias("granulated sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("grated parmesan", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("green bell pepper", BELL_PEPPER_DESCRIPTION),
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
		NutritionSeedAlias("halloumi", HALLOUMI_DESCRIPTION),
		NutritionSeedAlias("heavy cream", CREAM_DESCRIPTION, CREAM_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("kalamata olives", OLIVE_DESCRIPTION),
		NutritionSeedAlias("ketchup", KETCHUP_DESCRIPTION),
		NutritionSeedAlias("king prawn", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("king prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("leek", LEEK_DESCRIPTION),
		NutritionSeedAlias("lemon", LEMON_DESCRIPTION),
		NutritionSeedAlias("lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemons", LEMON_DESCRIPTION),
		NutritionSeedAlias("lentils", LENTIL_DESCRIPTION),
		NutritionSeedAlias("light brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("light soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("lime", LIME_DESCRIPTION),
		NutritionSeedAlias("limes", LIME_DESCRIPTION),
		NutritionSeedAlias("linguine", PASTA_DESCRIPTION),
		NutritionSeedAlias("long grain rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("low-sodium chicken broth", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("low-sodium chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("low-sodium soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("maple syrup", MAPLE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("maris piper potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("maris piper potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("mint", MINT_DESCRIPTION),
		NutritionSeedAlias("miso", MISO_DESCRIPTION),
		NutritionSeedAlias("miso paste", MISO_DESCRIPTION),
		NutritionSeedAlias("morton kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("neutral oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("oat milk", OAT_MILK_DESCRIPTION),
		NutritionSeedAlias("olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("onion", ONION_DESCRIPTION),
		NutritionSeedAlias("onions", ONION_DESCRIPTION),
		NutritionSeedAlias("orange", ORANGE_DESCRIPTION),
		NutritionSeedAlias("orzo", PASTA_DESCRIPTION),
		NutritionSeedAlias("panko breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("passata", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("pecorino romano", ROMANO_DESCRIPTION),
		NutritionSeedAlias("penne pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("pine nuts", PINE_NUT_DESCRIPTION),
		NutritionSeedAlias("pinto beans", PINTO_BEAN_DESCRIPTION),
		NutritionSeedAlias("plain flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("plum tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("pomegranate seeds", POMEGRANATE_DESCRIPTION),
		NutritionSeedAlias("pork mince", PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias("potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("prawn", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pure maple syrup", MAPLE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("pure vanilla extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("rapeseed oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("red bell pepper", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red cabbage", CABBAGE_RED_DESCRIPTION),
		NutritionSeedAlias("red chilli flakes", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red onion", ONION_DESCRIPTION),
		NutritionSeedAlias("red onions", ONION_DESCRIPTION),
		NutritionSeedAlias("rice vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("rice wine vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("self raising flour", SELF_RISING_FLOUR_DESCRIPTION),
		NutritionSeedAlias("self-raising flour", SELF_RISING_FLOUR_DESCRIPTION),
		NutritionSeedAlias("sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("sesame seeds", SESAME_SEED_DESCRIPTION),
		NutritionSeedAlias("shallot", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shallots", SHALLOT_DESCRIPTION),
		NutritionSeedAlias(
			"skinless chicken thigh",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("smoked bacon", BACON_DESCRIPTION),
		NutritionSeedAlias("smoked paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("soft brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("spring onion", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("stick celery", CELERY_DESCRIPTION),
		NutritionSeedAlias(
			"strong white bread flour",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("sweet onion", ONION_DESCRIPTION),
		NutritionSeedAlias("sweet paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("sweet potato", SWEET_POTATO_DESCRIPTION),
		NutritionSeedAlias("tahini", TAHINI_DESCRIPTION),
		NutritionSeedAlias("tenderstem broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("toasted sesame oil", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("tomato ketchup", KETCHUP_DESCRIPTION),
		NutritionSeedAlias("tomato paste", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomato puree", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomato purée", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("turmeric", TURMERIC_DESCRIPTION),
		NutritionSeedAlias("unsalted butter", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("vanilla bean paste", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vegetable broth", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vegetable oil", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable stock", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vine tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("vine tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("white miso", MISO_DESCRIPTION),
		NutritionSeedAlias("white miso paste", MISO_DESCRIPTION),
		NutritionSeedAlias("white onion", ONION_DESCRIPTION),
		NutritionSeedAlias("white sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("white wine vinegar", WHITE_VINEGAR_DESCRIPTION, CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("whole black peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("whole grain mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("whole milk", MILK_DESCRIPTION),
		NutritionSeedAlias("yellow bell pepper", YELLOW_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("yellow onion", ONION_DESCRIPTION),
		NutritionSeedAlias("yellow pepper", YELLOW_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("zucchini", ZUCCHINI_DESCRIPTION),
	)
}
