package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHOVY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_UNPREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAKING_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BRISKET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_OXTAIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_SKIRT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_STRIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_TONGUE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BISCUIT_MIX_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLOOD_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUE_CHEESE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREADCRUMB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BULGUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BURGER_BUN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARDAMOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CASSAVA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CATFISH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHALLAH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHEDDAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_GIBLETS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_LIVER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_LIVER_PATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_THIGH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_THIGH_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_CON_CARNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_WITH_BEANS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHOCOLATE_COFFEE_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHUTNEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CIDER_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CILANTRO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCONUT_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CONDENSED_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNED_BEEF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNMEAL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_COB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_FLOUR_YELLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CURRY_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_YOLK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ENDIVE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_BULB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENUGREEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FETA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FISH_STOCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FRENCH_FRIES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAPES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAPE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_TEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HALLOUMI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HARD_CANDY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HIBISCUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOISIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ICEBERG_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_SEASONING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JALAPENO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KALE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KIWI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LADYFINGER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMONGRASS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_PEEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LENTIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MASA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MINT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MISO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MIXED_GREENS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MIXED_HERBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MIXED_VEGETABLES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOLASSES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OATS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OREGANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OYSTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PAPRIKA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_BUTTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PESTO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PICKLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PIE_CRUST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINTO_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLUM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_BUTT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_LOIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_CHIPS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_CHIPS_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POULTRY_SEASONING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POWDERED_SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POWDERED_SUGAR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUMPKIN_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.QUESO_FRESCO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RADICCHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RANCH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.READY_TO_EAT_CEREAL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RHUBARB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMAINE_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALSA_VERDE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALTED_COD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SAZON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SELF_RISING_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SERRANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SIMPLE_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SNAPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOYBEAN_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOY_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPINACH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPRING_ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SQUID_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SWEET_AND_SOUR_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SWEET_AND_SOUR_SAUCE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.THYME_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_CRUSHED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TUNA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURKEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WALNUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WASABI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATERMELON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITEFISH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_CHICKEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_WHEAT_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WONTON_WRAPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WORCESTERSHIRE_DESCRIPTION
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
		NutritionSeedAlias("seeds cardamom", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("green cardamom", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias(
			"british chicken breast",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("boneless center salmon", SALMON_DESCRIPTION),
		NutritionSeedAlias(
			"chicken giblets stock",
			CHICKEN_GIBLETS_DESCRIPTION,
			CHICKEN_BROTH_DESCRIPTION,
			CHICKEN_LIVER_DESCRIPTION
		),
		NutritionSeedAlias("chicken giblets", CHICKEN_GIBLETS_DESCRIPTION, CHICKEN_LIVER_DESCRIPTION),
		NutritionSeedAlias(
			"chimichurri",
			SALSA_VERDE_DESCRIPTION,
			PESTO_DESCRIPTION
		),
		NutritionSeedAlias(
			"chimichurri sauce",
			SALSA_VERDE_DESCRIPTION,
			PESTO_DESCRIPTION
		),
		NutritionSeedAlias("corn chex", READY_TO_EAT_CEREAL_DESCRIPTION),
		NutritionSeedAlias("rice chex", READY_TO_EAT_CEREAL_DESCRIPTION),
		NutritionSeedAlias("wheat chex", READY_TO_EAT_CEREAL_DESCRIPTION),
		NutritionSeedAlias("cocoa krispies", READY_TO_EAT_CEREAL_DESCRIPTION),
		NutritionSeedAlias("chex", READY_TO_EAT_CEREAL_DESCRIPTION),
		NutritionSeedAlias("loaf challah", CHALLAH_DESCRIPTION),
		NutritionSeedAlias("challah", CHALLAH_DESCRIPTION),
		NutritionSeedAlias("skinless cod", COD_DESCRIPTION),
		NutritionSeedAlias("tuscan kale", KALE_DESCRIPTION),
		NutritionSeedAlias(
			"white fleshed fish",
			WHITEFISH_DESCRIPTION,
			COD_DESCRIPTION
		),
		NutritionSeedAlias("whitefish", WHITEFISH_DESCRIPTION, COD_DESCRIPTION),
		NutritionSeedAlias(
			"japanese karaage",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"karaage",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"morcilla burgos spanish black pudding",
			BLOOD_SAUSAGE_DESCRIPTION
		),
		NutritionSeedAlias("morcilla", BLOOD_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("tapatio", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("herb oil", CANOLA_OIL_DESCRIPTION, SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("hot vegetable", SOYBEAN_OIL_DESCRIPTION, CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("other neutral oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("oil enough", CANOLA_OIL_DESCRIPTION, SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("oil griddle", CANOLA_OIL_DESCRIPTION, SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("stonemill chilli powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("stonemill paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("salt fish", SALTED_COD_DESCRIPTION, COD_DESCRIPTION),
		NutritionSeedAlias("salted fish", SALTED_COD_DESCRIPTION, COD_DESCRIPTION),
		NutritionSeedAlias("cassareep", MOLASSES_DESCRIPTION),
		NutritionSeedAlias("cassava woody center", CASSAVA_DESCRIPTION),
		NutritionSeedAlias("cassava", CASSAVA_DESCRIPTION),
		NutritionSeedAlias(
			"thai basil off",
			BASIL_DESCRIPTION,
			BASIL_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("square sheet frozen puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pie dough crust pie", PIE_CRUST_DESCRIPTION, PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pie dough", PIE_CRUST_DESCRIPTION, PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("simple pork", PORK_MINCE_DESCRIPTION, PORK_BUTT_DESCRIPTION),
		NutritionSeedAlias(
			"skirt whole skirt",
			BEEF_SKIRT_DESCRIPTION,
			BEEF_STRIP_DESCRIPTION,
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias(
			"pickling spice tied cheesecloth",
			POULTRY_SEASONING_DESCRIPTION
		),
		NutritionSeedAlias("grachai", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("henderson relish", WORCESTERSHIRE_DESCRIPTION),
		NutritionSeedAlias("chu hou sauce", HOISIN_DESCRIPTION),
		NutritionSeedAlias("reshampatti", CAYENNE_DESCRIPTION, CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("fish mint", MINT_DESCRIPTION),
		NutritionSeedAlias(
			"loved british beef mince fat",
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias(
			"ginger garlic paste",
			GINGER_FRESH_DESCRIPTION,
			GARLIC_DESCRIPTION
		),
		NutritionSeedAlias(
			"chilli paste",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"chili paste",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"tajin",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias("lager beer", BEER_DESCRIPTION),
		NutritionSeedAlias("lager", BEER_DESCRIPTION),
		NutritionSeedAlias("free range woodland egg", EGG_DESCRIPTION),
		NutritionSeedAlias(
			"regular green lettuce",
			ICEBERG_LETTUCE_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"green lettuce",
			ICEBERG_LETTUCE_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias("whole limes", LIME_DESCRIPTION),
		NutritionSeedAlias(
			"brewed shot espresso",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"shot espresso",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"herb garlic marinade",
			GARLIC_DESCRIPTION,
			BASIL_DESCRIPTION,
			BASIL_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("masala paste", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias(
			"chicken drumsticks patted dry paper towels",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken drumsticks",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken drumstick",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("hard hard steamed eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("hard steamed eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("cassava woody", CASSAVA_DESCRIPTION),
		NutritionSeedAlias("loved beef mince fat", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("beef mince", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias(
			"tomatoes their juices alternatively use canned tomatoes",
			TOMATO_RAW_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION,
			TOMATO_CRUSHED_DESCRIPTION
		),
		NutritionSeedAlias(
			"tomatoes their juices",
			TOMATO_RAW_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION,
			TOMATO_CRUSHED_DESCRIPTION
		),
		NutritionSeedAlias("pickled jalapenos pickling", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("pickled jalapeno pickling", JALAPENO_DESCRIPTION),
		NutritionSeedAlias(
			"zhug",
			SALSA_VERDE_DESCRIPTION,
			CHUTNEY_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION,
			CILANTRO_DESCRIPTION
		),
		NutritionSeedAlias("yellow miso", MISO_DESCRIPTION),
		NutritionSeedAlias(
			"scotch bonnet",
			HOT_CHILI_RED_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias(
			"serrano chilli",
			SERRANO_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"filets mignon",
			BEEF_STRIP_DESCRIPTION,
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias(
			"filet mignon",
			BEEF_STRIP_DESCRIPTION,
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias("garlic scapes", GARLIC_DESCRIPTION),
		NutritionSeedAlias("makrut lime", LIME_DESCRIPTION),
		NutritionSeedAlias(
			"mochiko",
			FLOUR_DESCRIPTION,
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("sanding sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("steel oats", OATS_DESCRIPTION),
		NutritionSeedAlias(
			"canary beans",
			PINTO_BEAN_DESCRIPTION,
			BUTTER_BEAN_DESCRIPTION
		),
		NutritionSeedAlias(
			"chinese bacon",
			BACON_UNPREPARED_DESCRIPTION,
			BACON_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken paillards",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("racks pork spareribs", PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias("raw shelled pumpkin seeds", PUMPKIN_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"whole tinned tomatoes",
			TOMATO_CRUSHED_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION
		),
		NutritionSeedAlias("wiri peppers", HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias(
			"cured streaky bacon",
			BACON_DESCRIPTION,
			BACON_UNPREPARED_DESCRIPTION
		),
		NutritionSeedAlias("loose green tea", GREEN_TEA_DESCRIPTION),
		NutritionSeedAlias("mesclun salad mix", MIXED_GREENS_DESCRIPTION),
		NutritionSeedAlias(
			"garlic herb goat cheese",
			FETA_DESCRIPTION,
			QUESO_FRESCO_DESCRIPTION
		),
		NutritionSeedAlias("full bodied red wine", RED_WINE_DESCRIPTION),
		NutritionSeedAlias(
			"hawaiian sweet rolls buns all still attached",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"leaf lettuce vertically",
			ROMAINE_LETTUCE_DESCRIPTION,
			ICEBERG_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias("vine ripened tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("gorgonzola cheese crumbled", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias(
			"green cabbage core intact",
			CABBAGE_GREEN_DESCRIPTION,
			CABBAGE_DESCRIPTION
		),
		NutritionSeedAlias(
			"green cabbage long strands",
			CABBAGE_GREEN_DESCRIPTION,
			CABBAGE_DESCRIPTION
		),
		NutritionSeedAlias("russet potatoes clean", POTATO_DESCRIPTION),
		NutritionSeedAlias("russet potatoes fries", POTATO_DESCRIPTION),
		NutritionSeedAlias(
			"soft baguettes open",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("shrimp shells separately", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("shrimp tail", SHRIMP_DESCRIPTION),
		NutritionSeedAlias(
			"squid bodies rings tentacles whole",
			SQUID_DESCRIPTION
		),
		NutritionSeedAlias(
			"corn tortillas eighths stale",
			CORN_TORTILLA_DESCRIPTION
		),
		NutritionSeedAlias("cracked freekeh over", BULGUR_DESCRIPTION),
		NutritionSeedAlias(
			"french puy lentils over stones",
			LENTIL_DESCRIPTION
		),
		NutritionSeedAlias("red lentils over", LENTIL_DESCRIPTION),
		NutritionSeedAlias(
			"cranberry beans over",
			PINTO_BEAN_DESCRIPTION,
			BUTTER_BEAN_DESCRIPTION
		),
		NutritionSeedAlias(
			"cooked corned beef brisket fat meat",
			CORNED_BEEF_DESCRIPTION,
			BEEF_BRISKET_DESCRIPTION
		),
		NutritionSeedAlias("morcilla burgos", BLOOD_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias(
			"boneless lamb stew meat",
			LAMB_MINCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"dill pickles dill pickle juice",
			PICKLE_DESCRIPTION
		),
		NutritionSeedAlias("tahini before measuring", TAHINI_DESCRIPTION),
		NutritionSeedAlias(
			"hot water around 200of",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"crabmeat",
			CRAB_DESCRIPTION,
			CRAB_RAW_DESCRIPTION
		),
		NutritionSeedAlias("frijoles refritos", PINTO_BEAN_DESCRIPTION),
		NutritionSeedAlias(
			"green cabbage core",
			CABBAGE_GREEN_DESCRIPTION,
			CABBAGE_DESCRIPTION
		),
		NutritionSeedAlias(
			"rotisserie chicken meat still",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("rhubarb ten stalks", RHUBARB_DESCRIPTION),
		NutritionSeedAlias(
			"peri rice",
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"boneless leg lamb shoulder neck",
			LAMB_MINCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"corn tortillas eighths",
			CORN_TORTILLA_DESCRIPTION
		),
		NutritionSeedAlias(
			"unrefined coconut oil solid but soft",
			COCONUT_OIL_DESCRIPTION
		),
		NutritionSeedAlias(
			"sweetened condensed milk glue",
			CONDENSED_MILK_DESCRIPTION
		),
		NutritionSeedAlias(
			"ginger whacked flat side knife",
			GINGER_FRESH_DESCRIPTION
		),
		NutritionSeedAlias(
			"granulated sugar cane suga",
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias(
			"beefsteak tomato seeds",
			TOMATO_RAW_DESCRIPTION
		),
		NutritionSeedAlias(
			"radicchio frisee",
			RADICCHIO_DESCRIPTION,
			ENDIVE_DESCRIPTION
		),
		NutritionSeedAlias("thyme shallots", THYME_FRESH_DESCRIPTION),
		NutritionSeedAlias(
			"baguettes",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"butter chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"double cream whipped peaks",
			CREAM_DESCRIPTION,
			CREAM_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"hawaiian sweet rolls buns all attached",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"lemongrass outer top third rest",
			LEMONGRASS_DESCRIPTION
		),
		NutritionSeedAlias("lemongrass under", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias(
			"rotisserie chicken breast yield",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("spanish onion unevenly", ONION_DESCRIPTION),
		NutritionSeedAlias(
			"confectioners sugar cinnamon",
			POWDERED_SUGAR_DESCRIPTION,
			POWDERED_SUGAR_FALLBACK_DESCRIPTION,
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias(
			"corn flour yellow cornmeal",
			CORNMEAL_DESCRIPTION,
			CORN_FLOUR_YELLOW_DESCRIPTION,
			MASA_DESCRIPTION
		),
		NutritionSeedAlias("egg yolk milk", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("walnuts pecans", WALNUT_DESCRIPTION),
		NutritionSeedAlias(
			"herbs oregano thyme",
			OREGANO_DESCRIPTION,
			THYME_FRESH_DESCRIPTION
		),
		NutritionSeedAlias(
			"herb spice blend",
			MIXED_HERBS_DESCRIPTION,
			ITALIAN_SEASONING_DESCRIPTION,
			POULTRY_SEASONING_DESCRIPTION
		),
		NutritionSeedAlias(
			"herbs mix",
			MIXED_HERBS_DESCRIPTION,
			ITALIAN_SEASONING_DESCRIPTION,
			POULTRY_SEASONING_DESCRIPTION
		),
		NutritionSeedAlias(
			"sweet sour mix",
			SWEET_AND_SOUR_SAUCE_DESCRIPTION,
			SWEET_AND_SOUR_SAUCE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"pickled apple brine",
			CIDER_VINEGAR_DESCRIPTION,
			PICKLE_DESCRIPTION
		),
		NutritionSeedAlias(
			"sazon sin achiote seasoning",
			SAZON_DESCRIPTION,
			PAPRIKA_DESCRIPTION,
			POULTRY_SEASONING_DESCRIPTION
		),
		NutritionSeedAlias("bread sauce mix", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias(
			"pastry baking mix",
			BISCUIT_MIX_DESCRIPTION,
			FLOUR_DESCRIPTION,
			SELF_RISING_FLOUR_DESCRIPTION
		),
		NutritionSeedAlias(
			"chilli con carne sauce",
			CHILI_CON_CARNE_DESCRIPTION,
			CHILI_WITH_BEANS_DESCRIPTION,
			CHILI_POWDER_DESCRIPTION
		),
		NutritionSeedAlias(
			"chinese stir fry vegetables",
			MIXED_VEGETABLES_DESCRIPTION
		),
		NutritionSeedAlias(
			"granulated sugar light brown sugar",
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias("tomatoes cucumbers", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"baking powder baking soda",
			BAKING_POWDER_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty bread couscous",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("red plums apricots", PLUM_DESCRIPTION),
		NutritionSeedAlias("tomato oil anchovy", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"all seasoning blend",
			ITALIAN_SEASONING_DESCRIPTION,
			POULTRY_SEASONING_DESCRIPTION
		),
		NutritionSeedAlias("chinese duck marinade", DUCK_DESCRIPTION),
		NutritionSeedAlias(
			"chinese light soy sauce chinese dark soy sauce",
			SOY_SAUCE_DESCRIPTION
		),
		NutritionSeedAlias("hoisin garlic stir fry sauce", HOISIN_DESCRIPTION),
		NutritionSeedAlias(
			"cuban black beans rice",
			BLACK_BEAN_DESCRIPTION,
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"cheddar provolone monterey jack muenster cheese",
			CHEDDAR_DESCRIPTION
		),
	)
}
