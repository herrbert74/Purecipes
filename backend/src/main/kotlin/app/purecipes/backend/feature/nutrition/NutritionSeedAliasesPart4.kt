package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHOVY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_UNPREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BRISKET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_OXTAIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_STRIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_TONGUE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREADCRUMB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BULGUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CATFISH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_LIVER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_LIVER_PATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHOCOLATE_COFFEE_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHUTNEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CILANTRO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNED_BEEF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_COB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CURRY_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ENDIVE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_BULB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENUGREEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FISH_STOCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FRENCH_FRIES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAPES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAPE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HALLOUMI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HARD_CANDY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HIBISCUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOISIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JALAPENO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KALE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KIWI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LADYFINGER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMONGRASS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_PEEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LENTIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MASA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MIXED_GREENS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OYSTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_BUTTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PIE_CRUST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_BUTT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_LOIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_CHIPS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_CHIPS_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POULTRY_SEASONING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUMPKIN_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RADICCHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RANCH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SIMPLE_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SNAPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPINACH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPRING_ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.THYME_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TUNA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURKEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WASABI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATERMELON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_WHEAT_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WONTON_WRAPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.YOGURT_DESCRIPTION

internal object NutritionSeedAliasesPart4 {

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias("acorn squash seeds", PUMPKIN_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"any flavor fruit jelly",
			JELLY_DESCRIPTION,
			JELLY_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("breadcrumbs biscuit crumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias(
			"british chips",
			FRENCH_FRIES_DESCRIPTION,
			POTATO_CHIPS_DESCRIPTION,
			POTATO_CHIPS_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("bunch cilantro", CILANTRO_DESCRIPTION),
		NutritionSeedAlias(
			"bunch escarole stem end wilted multiple changes",
			ENDIVE_DESCRIPTION,
			RADICCHIO_DESCRIPTION
		),
		NutritionSeedAlias("bunch lacinato kale", KALE_DESCRIPTION),
		NutritionSeedAlias("bunch swiss chard", CHARD_DESCRIPTION),
		NutritionSeedAlias("butternut squash seeds", PUMPKIN_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"buttery flaky crust double crusted pie",
			PIE_CRUST_DESCRIPTION,
			PUFF_PASTRY_DESCRIPTION
		),
		NutritionSeedAlias(
			"buttery flaky pie crust",
			PIE_CRUST_DESCRIPTION,
			PUFF_PASTRY_DESCRIPTION
		),
		NutritionSeedAlias("caramelised onion chutney", CHUTNEY_DESCRIPTION),
		NutritionSeedAlias("catfish fillets along natural seam", CATFISH_DESCRIPTION),
		NutritionSeedAlias("champagne grapes", GRAPES_DESCRIPTION),
		NutritionSeedAlias(
			"chicken cutlets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken giblets wing tips stock",
			CHICKEN_BROTH_DESCRIPTION,
			CHICKEN_LIVER_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken pate",
			CHICKEN_LIVER_PATE_DESCRIPTION,
			CHICKEN_LIVER_DESCRIPTION
		),
		NutritionSeedAlias(
			"chilli jam",
			JELLY_DESCRIPTION,
			JELLY_FALLBACK_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION,
			JALAPENO_DESCRIPTION
		),
		NutritionSeedAlias("chinese bacon lap yuk", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("chocolate covered coffee beans", CHOCOLATE_COFFEE_BEAN_DESCRIPTION),
		NutritionSeedAlias(
			"cooked corned beef brisket fat meat 5cm",
			CORNED_BEEF_DESCRIPTION,
			BEEF_BRISKET_DESCRIPTION
		),
		NutritionSeedAlias("corn masa mix", MASA_DESCRIPTION),
		NutritionSeedAlias(
			"corned beef brisket flat",
			CORNED_BEEF_DESCRIPTION,
			BEEF_BRISKET_DESCRIPTION
		),
		NutritionSeedAlias("cracked freekeh", BULGUR_DESCRIPTION),
		NutritionSeedAlias("crisp ladyfingers", LADYFINGER_DESCRIPTION),
		NutritionSeedAlias(
			"cube steak round steak that been",
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias("dry wasabi powder", WASABI_DESCRIPTION),
		NutritionSeedAlias("egg blend", EGG_DESCRIPTION),
		NutritionSeedAlias("egg glazing", EGG_DESCRIPTION),
		NutritionSeedAlias("egg mixed", EGG_DESCRIPTION),
		NutritionSeedAlias("fennel bulb stalks core", FENNEL_BULB_DESCRIPTION),
		NutritionSeedAlias("fillets red snapper", SNAPPER_DESCRIPTION),
		NutritionSeedAlias(
			"fish stock seafood stock",
			FISH_STOCK_DESCRIPTION,
			ANCHOVY_DESCRIPTION
		),
		NutritionSeedAlias(
			"french bread fit bowl crisp",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("fried wonton skins", WONTON_WRAPPER_DESCRIPTION),
		NutritionSeedAlias("frozen cherries mix sweet tart", CHERRY_DESCRIPTION),
		NutritionSeedAlias("frozen puff pastry sheets", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("frozen seedless grapes", GRAPES_DESCRIPTION),
		NutritionSeedAlias("frozen sweet cherries", CHERRY_DESCRIPTION),
		NutritionSeedAlias("full candy canes", HARD_CANDY_DESCRIPTION),
		NutritionSeedAlias("garlic scapes scapes woody scapes", GARLIC_DESCRIPTION),
		NutritionSeedAlias("granulated lime zest", LEMON_PEEL_DESCRIPTION),
		NutritionSeedAlias("green peppercorns brine", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("head radicchio", RADICCHIO_DESCRIPTION),
		NutritionSeedAlias("hibiscus flower", HIBISCUS_DESCRIPTION),
		NutritionSeedAlias("hoi sin sauce", HOISIN_DESCRIPTION),
		NutritionSeedAlias("kasoori methi", FENUGREEK_DESCRIPTION),
		NutritionSeedAlias("kiwi pureed", KIWI_DESCRIPTION),
		NutritionSeedAlias("leftover dal", LENTIL_DESCRIPTION),
		NutritionSeedAlias("leftover raita", YOGURT_DESCRIPTION),
		NutritionSeedAlias("leftover saag", SPINACH_DESCRIPTION),
		NutritionSeedAlias("little white chocolate decorate", MILK_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("mesclun green leaf salad mix", MIXED_GREENS_DESCRIPTION),
		NutritionSeedAlias("mexican green onions cebollitas", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("milk chocolate cacao", MILK_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("mixed baby salad greens", MIXED_GREENS_DESCRIPTION),
		NutritionSeedAlias("multigrain spaghetti", PASTA_DESCRIPTION),
		NutritionSeedAlias("non hydrogenated lard", LARD_DESCRIPTION),
		NutritionSeedAlias("oil anchovy", ANCHOVY_DESCRIPTION),
		NutritionSeedAlias("peanut dipping sauce below", PEANUT_BUTTER_DESCRIPTION),
		NutritionSeedAlias(
			"persian leekschives green scallions",
			SPRING_ONION_DESCRIPTION
		),
		NutritionSeedAlias(
			"pie dough double crust pie",
			PIE_CRUST_DESCRIPTION,
			PUFF_PASTRY_DESCRIPTION
		),
		NutritionSeedAlias("pork butt roast", PORK_BUTT_DESCRIPTION, PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias(
			"pork shoulder fat cap",
			PORK_BUTT_DESCRIPTION,
			PORK_MINCE_DESCRIPTION
		),
		NutritionSeedAlias("poultry seasoning full", POULTRY_SEASONING_DESCRIPTION),
		NutritionSeedAlias(
			"pouch hoisin garlic stir fry sauce",
			HOISIN_DESCRIPTION
		),
		NutritionSeedAlias(
			"racks pork spareribs fat",
			PORK_RIBS_DESCRIPTION
		),
		NutritionSeedAlias("ranch dressing mix", RANCH_DESCRIPTION),
		NutritionSeedAlias("raw oysters long", OYSTER_DESCRIPTION),
		NutritionSeedAlias("red capsicum", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red onion chutney", CHUTNEY_DESCRIPTION),
		NutritionSeedAlias("sambar powder", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias(
			"scallions white green cross wise",
			SPRING_ONION_DESCRIPTION
		),
		NutritionSeedAlias("several thyme", THYME_FRESH_DESCRIPTION),
		NutritionSeedAlias("shop shortcrust pastry", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("shortcrust pastry", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("shrimp shells", SHRIMP_DESCRIPTION),
		NutritionSeedAlias(
			"simple syrup oleo saccharum",
			SIMPLE_SYRUP_DESCRIPTION
		),
		NutritionSeedAlias("skimmed roast turkey drippings", TURKEY_DESCRIPTION),
		NutritionSeedAlias(
			"specially selected pork loin steaks",
			PORK_LOIN_DESCRIPTION
		),
		NutritionSeedAlias(
			"stalks lemongrass under flat side knife",
			LEMONGRASS_DESCRIPTION
		),
		NutritionSeedAlias(
			"suckling pig",
			PORK_BUTT_DESCRIPTION,
			PORK_MINCE_DESCRIPTION
		),
		NutritionSeedAlias("sweetcorn cobs corn kernels", CORN_COB_DESCRIPTION),
		NutritionSeedAlias("tagine paste", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("tagine seasoning", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("thyme stripped", THYME_FRESH_DESCRIPTION),
		NutritionSeedAlias("tuna steaks", TUNA_DESCRIPTION),
		NutritionSeedAlias("whole beef tongue", BEEF_TONGUE_DESCRIPTION),
		NutritionSeedAlias("wholemeal", WHOLE_WHEAT_BREAD_DESCRIPTION),
		NutritionSeedAlias("wiri wiri peppers", HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("yukon gold", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potato 3cm", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potatoes bowl", POTATO_DESCRIPTION),
		NutritionSeedAlias("beef stew meat", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("swiss chard", CHARD_DESCRIPTION),
		NutritionSeedAlias("cilantro bunch", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("halloumi block", HALLOUMI_DESCRIPTION),
		NutritionSeedAlias("oxtail", BEEF_OXTAIL_DESCRIPTION, BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("strip steak", BEEF_STRIP_DESCRIPTION, BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("ladyfingers", LADYFINGER_DESCRIPTION),
		NutritionSeedAlias("wonton skins", WONTON_WRAPPER_DESCRIPTION),
		NutritionSeedAlias("wonton wrappers", WONTON_WRAPPER_DESCRIPTION),
		NutritionSeedAlias("wasabi powder", WASABI_DESCRIPTION),
		NutritionSeedAlias("freekeh", BULGUR_DESCRIPTION),
		NutritionSeedAlias("hoisin sauce", HOISIN_DESCRIPTION),
		NutritionSeedAlias("potato chips", POTATO_CHIPS_DESCRIPTION, POTATO_CHIPS_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("french fries", FRENCH_FRIES_DESCRIPTION),
		NutritionSeedAlias("masa harina", MASA_DESCRIPTION),
		NutritionSeedAlias("pie crust", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("shortcrust", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("chicken liver pate", CHICKEN_LIVER_PATE_DESCRIPTION),
		NutritionSeedAlias("grapes", GRAPES_DESCRIPTION),
		NutritionSeedAlias("catfish fillets", CATFISH_DESCRIPTION),
		NutritionSeedAlias("red snapper fillets", SNAPPER_DESCRIPTION),
		NutritionSeedAlias(
			"chicken bit",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"cooked crab claws cracked legs shell quarters",
			CRAB_RAW_DESCRIPTION,
			CRAB_DESCRIPTION
		),
		NutritionSeedAlias("east end tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("leek grit", LEEK_DESCRIPTION),
		NutritionSeedAlias(
			"new orleans remoulade sauce",
			MAYONNAISE_DESCRIPTION
		),
		NutritionSeedAlias("pickled apples", APPLE_DESCRIPTION),
		NutritionSeedAlias("pickled watermelon rind", WATERMELON_DESCRIPTION),
		NutritionSeedAlias("quart chicken", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("rose syrup", SIMPLE_SYRUP_DESCRIPTION),
		NutritionSeedAlias("salt chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"salt egg whites",
			EGG_WHITE_DESCRIPTION,
			EGG_WHITE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"seasoned flour livers",
			CHICKEN_LIVER_DESCRIPTION
		),
		NutritionSeedAlias(
			"tomatoes green chiles",
			TOMATO_DICED_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"tomatoes green chillies",
			TOMATO_DICED_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias("verjus", GRAPE_JUICE_DESCRIPTION),
	)
}
