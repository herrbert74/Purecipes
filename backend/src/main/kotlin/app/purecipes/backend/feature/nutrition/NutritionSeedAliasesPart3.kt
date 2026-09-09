package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALLSPICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ARUGULA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ASPARAGUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.AVOCADO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_UNPREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BALSAMIC_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BANANA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BRISKET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_OXTAIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_STRIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_EYED_PEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BURGER_BUN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTERNUT_SQUASH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANNELLINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CASHEW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAULIFLOWER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CELERY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHEDDAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BREAST_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_MINCE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_THIGH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_THIGH_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKPEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHORIZO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CIDER_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CILANTRO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CLAM_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCONUT_DRIED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CONDENSED_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COOKING_SPRAY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRAB_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_CHEESE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREMINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CROISSANT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CUCUMBER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CURRY_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DAIKON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DARK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DARK_CHOCOLATE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_DRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_SWEET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DISTILLED_SPIRITS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DRY_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EDIBLE_PODDED_PEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FETA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GELATIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_GROUND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAHAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HALLOUMI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HONEYDEW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ICEBERG_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JALAPENO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMONGRASS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_PEEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAPLE_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MARSHMALLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MINT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MISO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOZZARELLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOZZARELLA_LOW_MOISTURE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSHROOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSSEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OATS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OAT_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PAPRIKA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARMESAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_BUTTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PICKLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINEAPPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINTO_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PISTACHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PITA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLANT_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLUM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_BELLY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PRUNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUMPKIN_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.QUESO_FRESCO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RADICCHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RADISH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RAISIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RASPBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMAINE_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SAFFRON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SCALLOP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SELF_RISING_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SERRANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SINGLE_CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SNAPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOYBEAN_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOY_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOY_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPINACH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPRING_ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SQUID_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.STRAWBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUNFLOWER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SWEET_POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TARRAGON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOFU_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_CRUSHED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TUNA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURKEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGETABLE_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGGIE_BURGER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATERMELON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_CHICKEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WILD_RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WORCESTERSHIRE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.YELLOW_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ZUCCHINI_DESCRIPTION

internal object NutritionSeedAliasesPart3 {

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias(
			"daikon radish",
			DAIKON_DESCRIPTION,
			RADISH_DESCRIPTION
		),
		NutritionSeedAlias("louis ribs between equal", PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias(
			"pasta dough",
			PASTA_DESCRIPTION,
			EGG_NOODLE_DESCRIPTION
		),
		NutritionSeedAlias(
			"whole split chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("allspice full", ALLSPICE_DESCRIPTION),
		NutritionSeedAlias("amaretti biscuit", GRAHAM_DESCRIPTION),
		NutritionSeedAlias("american lager", BEER_DESCRIPTION),
		NutritionSeedAlias(
			"baguette bread day old",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"baking spray flour",
			COOKING_SPRAY_DESCRIPTION,
			CANOLA_OIL_DESCRIPTION
		),
		NutritionSeedAlias(
			"baps",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("beef brisket grain steaks", BEEF_BRISKET_DESCRIPTION, BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("black garlic paste", GARLIC_DESCRIPTION),
		NutritionSeedAlias("boneless lamb neck", LAMB_MINCE_DESCRIPTION),
		NutritionSeedAlias("boneless lamb stew meat lamb neck leg shoulder combination fat", LAMB_MINCE_DESCRIPTION),
		NutritionSeedAlias("boneless leg lamb lamb shoulder neck", LAMB_MINCE_DESCRIPTION),
		NutritionSeedAlias(
			"boneless skinless chicken breast cutlets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"bread roll sub",
			BURGER_BUN_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"british chicken thighs",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"brown crab meat",
			CRAB_DESCRIPTION,
			CRAB_RAW_DESCRIPTION
		),
		NutritionSeedAlias("bunch coriander", CILANTRO_DESCRIPTION),
		NutritionSeedAlias(
			"burrata",
			MOZZARELLA_DESCRIPTION,
			MOZZARELLA_LOW_MOISTURE_DESCRIPTION
		),
		NutritionSeedAlias("butter biscuits", GRAHAM_DESCRIPTION),
		NutritionSeedAlias("cauliflower couscous", CAULIFLOWER_DESCRIPTION),
		NutritionSeedAlias("celery stalk thin", CELERY_DESCRIPTION),
		NutritionSeedAlias(
			"centre baps",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"chayote squash",
			ZUCCHINI_DESCRIPTION,
			BUTTERNUT_SQUASH_DESCRIPTION
		),
		NutritionSeedAlias("cheddar jack you may need less", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("cheddar monterey jack blend", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias(
			"cheddar mozzarella mix",
			CHEDDAR_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken cutlets evenly flatten",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken cutlets thin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken percent lean",
			CHICKEN_MINCE_DESCRIPTION,
			CHICKEN_MINCE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken scaloppini",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken thigh fillets organic",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken thighs smaller ones",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("chicken wings backs legs", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias("chicken wings tips", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"chipotle pepper adobo sauce",
			HOT_CHILI_RED_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias(
			"ciabatta roll",
			ITALIAN_BREAD_DESCRIPTION,
			BAGUETTE_DESCRIPTION
		),
		NutritionSeedAlias("cilantro thoroughly", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("cleaned squid bodies rings tentacles", SQUID_DESCRIPTION),
		NutritionSeedAlias("cod fillet", COD_DESCRIPTION),
		NutritionSeedAlias("cox apple", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"crabmeat over",
			CRAB_DESCRIPTION,
			CRAB_RAW_DESCRIPTION
		),
		NutritionSeedAlias("creamy peanut butter spread", PEANUT_BUTTER_DESCRIPTION),
		NutritionSeedAlias(
			"crema",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("creme cassis", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias(
			"croissants split",
			CROISSANT_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty french bread",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty sub rolls",
			BURGER_BUN_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("cucumber thin", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("cucumber whatever shape you like", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("dark ale", BEER_DESCRIPTION),
		NutritionSeedAlias("desiccated coconut", COCONUT_DRIED_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt cooking", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt table salt use much volume", SALT_DESCRIPTION),
		NutritionSeedAlias("digestive cookies", GRAHAM_DESCRIPTION),
		NutritionSeedAlias("dry cider", BEER_DESCRIPTION),
		NutritionSeedAlias("egg noodles cooked dente hot", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("egg noodles ones", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias(
			"egg roll skins",
			EGG_NOODLE_DESCRIPTION,
			FLOUR_TORTILLA_DESCRIPTION
		),
		NutritionSeedAlias("egg water egg wash", EGG_DESCRIPTION),
		NutritionSeedAlias("eggs yolks whites", EGG_DESCRIPTION),
		NutritionSeedAlias(
			"escarole stem end wilted very multiple changes",
			RADICCHIO_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"favourite chilli",
			HOT_CHILI_RED_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias("few dashes hot sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("few dashes worcestershire sauce", WORCESTERSHIRE_DESCRIPTION),
		NutritionSeedAlias("few ribs celery leafy", CELERY_DESCRIPTION),
		NutritionSeedAlias(
			"firm mozzarella cheese",
			MOZZARELLA_LOW_MOISTURE_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias("flour work", FLOUR_DESCRIPTION),
		NutritionSeedAlias(
			"french bread sections sub style rolls",
			BAGUETTE_DESCRIPTION,
			BURGER_BUN_DESCRIPTION
		),
		NutritionSeedAlias("frozen french fries shoestring", POTATO_DESCRIPTION),
		NutritionSeedAlias("frozen kataifi dough", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("frozen makrut lime middle ribs", LIME_DESCRIPTION),
		NutritionSeedAlias("full bodied red wine madiran cahors", RED_WINE_DESCRIPTION),
		NutritionSeedAlias(
			"full fat low moisture mozzarella",
			MOZZARELLA_LOW_MOISTURE_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias("garlic cracked split", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"garlic oil",
			GARLIC_DESCRIPTION,
			OLIVE_OIL_DESCRIPTION
		),
		NutritionSeedAlias("garlic off expose cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"gevrik goats cheese crumbled",
			FETA_DESCRIPTION,
			QUESO_FRESCO_DESCRIPTION
		),
		NutritionSeedAlias(
			"ginger garlic puree",
			GINGER_FRESH_DESCRIPTION,
			GARLIC_DESCRIPTION
		),
		NutritionSeedAlias("golden sultanas", RAISIN_DESCRIPTION),
		NutritionSeedAlias("green apple", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"habanero pepper",
			HOT_CHILI_RED_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias(
			"hard paneer",
			HALLOUMI_DESCRIPTION,
			TOFU_DESCRIPTION
		),
		NutritionSeedAlias(
			"hawaiian sweet rolls",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("healthy dash tequila", DISTILLED_SPIRITS_DESCRIPTION),
		NutritionSeedAlias(
			"hot water around 200of 93oc",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("ice vodka", DISTILLED_SPIRITS_DESCRIPTION),
		NutritionSeedAlias("jalapeno seeds", JALAPENO_DESCRIPTION),
		NutritionSeedAlias(
			"jumbo chocolate chip",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("knorr chicken stock made knorr chicken stock diluted", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"lemon zest juice lemons",
			LEMON_JUICE_DESCRIPTION,
			LEMON_PEEL_DESCRIPTION
		),
		NutritionSeedAlias(
			"lemon zest lemon juice",
			LEMON_JUICE_DESCRIPTION,
			LEMON_PEEL_DESCRIPTION
		),
		NutritionSeedAlias(
			"lemon zest lemon juice lemons",
			LEMON_JUICE_DESCRIPTION,
			LEMON_PEEL_DESCRIPTION
		),
		NutritionSeedAlias("lemongrass stalk pale white segments", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias("lemongrass under flat side knife", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias(
			"light cream fat",
			SINGLE_CREAM_DESCRIPTION,
			CREAM_DESCRIPTION
		),
		NutritionSeedAlias("lime pickle", PICKLE_DESCRIPTION),
		NutritionSeedAlias(
			"limes juice zest",
			LIME_JUICE_DESCRIPTION,
			LIME_DESCRIPTION
		),
		NutritionSeedAlias(
			"littleneck clams",
			CLAM_JUICE_DESCRIPTION,
			MUSSEL_DESCRIPTION
		),
		NutritionSeedAlias("loved british beef steak mince fat", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("low salt chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"low sodium beef broth",
			CHICKEN_BROTH_DESCRIPTION,
			VEGETABLE_BROTH_DESCRIPTION
		),
		NutritionSeedAlias(
			"low sodium beef stock",
			CHICKEN_BROTH_DESCRIPTION,
			VEGETABLE_BROTH_DESCRIPTION
		),
		NutritionSeedAlias(
			"low sodium broth",
			VEGETABLE_BROTH_DESCRIPTION,
			CHICKEN_BROTH_DESCRIPTION
		),
		NutritionSeedAlias("low sodium tomato sauce", TOMATO_SAUCE_DESCRIPTION),
		NutritionSeedAlias(
			"madeira",
			DESSERT_WINE_DRY_DESCRIPTION,
			DESSERT_WINE_SWEET_DESCRIPTION
		),
		NutritionSeedAlias(
			"make ahead turkey stock",
			CHICKEN_BROTH_DESCRIPTION,
			TURKEY_DESCRIPTION
		),
		NutritionSeedAlias(
			"malt vinegar placed spray",
			WHITE_VINEGAR_DESCRIPTION,
			CIDER_VINEGAR_DESCRIPTION
		),
		NutritionSeedAlias("malted milk powder", DRY_MILK_DESCRIPTION),
		NutritionSeedAlias("marshmallows any", MARSHMALLOW_DESCRIPTION),
		NutritionSeedAlias("maynnoaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("mayonnaise suggested", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"meatless farm mince",
			PLANT_MINCE_DESCRIPTION,
			VEGGIE_BURGER_DESCRIPTION
		),
		NutritionSeedAlias(
			"mexican cheese blend recommended kraft sargento",
			CHEDDAR_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias(
			"mexican crema creme fraiche",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"mexican sour crema called cream",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"mexican style requeson cheese",
			QUESO_FRESCO_DESCRIPTION,
			CREAM_CHEESE_DESCRIPTION
		),
		NutritionSeedAlias("mexican vanilla extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("mezcal", DISTILLED_SPIRITS_DESCRIPTION),
		NutritionSeedAlias(
			"mild italian sausage bulk",
			PORK_SAUSAGE_DESCRIPTION,
			CHORIZO_DESCRIPTION
		),
		NutritionSeedAlias(
			"mild italian sausage casings",
			PORK_SAUSAGE_DESCRIPTION,
			CHORIZO_DESCRIPTION
		),
		NutritionSeedAlias("milk any percentage will", MILK_DESCRIPTION),
		NutritionSeedAlias("mirin japanese sweet wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("mirin sweet japanese rice wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("miso paste warm", MISO_DESCRIPTION),
		NutritionSeedAlias(
			"mixed baby rocket watercress spinach",
			ARUGULA_DESCRIPTION,
			SPINACH_DESCRIPTION
		),
		NutritionSeedAlias(
			"mixed baby salad greens available produce section",
			ICEBERG_LETTUCE_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"mixed chicken breast thigh",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_THIGH_DESCRIPTION
		),
		NutritionSeedAlias(
			"mixed mushrooms chestnut shitake oyster",
			CREMINI_DESCRIPTION,
			MUSHROOM_DESCRIPTION
		),
		NutritionSeedAlias("mixed olives", OLIVE_DESCRIPTION),
		NutritionSeedAlias(
			"mixed skinless chicken thighs drumsticks",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"mochiko sweet rice flour",
			FLOUR_DESCRIPTION,
			RICE_DESCRIPTION
		),
		NutritionSeedAlias(
			"monterrey cheese",
			CHEDDAR_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias(
			"montrachet goat cheese garlic herbs",
			FETA_DESCRIPTION,
			QUESO_FRESCO_DESCRIPTION
		),
		NutritionSeedAlias("moon cucumber", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias(
			"morels hot",
			MUSHROOM_DESCRIPTION,
			CREMINI_DESCRIPTION
		),
		NutritionSeedAlias(
			"mulato chillies",
			ANCHO_DESCRIPTION,
			HOT_CHILI_RED_DESCRIPTION
		),
		NutritionSeedAlias(
			"muscavado sugar",
			BROWN_SUGAR_DESCRIPTION,
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias(
			"mushroom broth",
			VEGETABLE_BROTH_DESCRIPTION,
			MUSHROOM_DESCRIPTION
		),
		NutritionSeedAlias(
			"mushrooms any variety combination",
			MUSHROOM_DESCRIPTION,
			CREMINI_DESCRIPTION
		),
		NutritionSeedAlias("mussels cleaned debearded", MUSSEL_DESCRIPTION),
		NutritionSeedAlias("mustard whole grain", MUSTARD_DESCRIPTION),
		NutritionSeedAlias(
			"napa cabbage kimchi",
			CABBAGE_GREEN_DESCRIPTION,
			CABBAGE_DESCRIPTION
		),
		NutritionSeedAlias(
			"naturally cured bacon",
			BACON_UNPREPARED_DESCRIPTION,
			BACON_DESCRIPTION
		),
		NutritionSeedAlias(
			"neutral oil like corn vegetable oil",
			CANOLA_OIL_DESCRIPTION,
			CORN_OIL_DESCRIPTION,
			SOYBEAN_OIL_DESCRIPTION
		),
		NutritionSeedAlias(
			"neutral oil such vegetable",
			CANOLA_OIL_DESCRIPTION,
			SOYBEAN_OIL_DESCRIPTION
		),
		NutritionSeedAlias(
			"new mexico red chilli powder",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION,
			PAPRIKA_DESCRIPTION
		),
		NutritionSeedAlias("nigerian style curry powder", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias(
			"nigerian style stock",
			VEGETABLE_BROTH_DESCRIPTION,
			CHICKEN_BROTH_DESCRIPTION
		),
		NutritionSeedAlias(
			"nonstick baking spray",
			COOKING_SPRAY_DESCRIPTION,
			CANOLA_OIL_DESCRIPTION
		),
		NutritionSeedAlias(
			"noodles chewy wonton noodles",
			EGG_NOODLE_DESCRIPTION,
			PASTA_DESCRIPTION
		),
		NutritionSeedAlias(
			"oaxcan style string cheese mozzarella",
			MOZZARELLA_DESCRIPTION,
			MOZZARELLA_LOW_MOISTURE_DESCRIPTION
		),
		NutritionSeedAlias(
			"olive brine",
			OLIVE_DESCRIPTION,
			PICKLE_DESCRIPTION
		),
		NutritionSeedAlias(
			"olive oil extra foil",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"olive oil peppers",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("onion thickly", ONION_DESCRIPTION),
		NutritionSeedAlias(
			"orange juice orange zest oranges",
			ORANGE_JUICE_DESCRIPTION,
			ORANGE_DESCRIPTION
		),
		NutritionSeedAlias(
			"orange yellow peppers seeds",
			YELLOW_BELL_PEPPER_DESCRIPTION,
			RED_BELL_PEPPER_DESCRIPTION
		),
		NutritionSeedAlias(
			"orange zest juice",
			ORANGE_JUICE_DESCRIPTION,
			ORANGE_DESCRIPTION
		),
		NutritionSeedAlias("orange zest navel oranges", ORANGE_DESCRIPTION),
		NutritionSeedAlias("orecchiette pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias(
			"organic chicken thigh",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("original manilife peanut butter", PEANUT_BUTTER_DESCRIPTION),
		NutritionSeedAlias("oxtail joints", BEEF_OXTAIL_DESCRIPTION, BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias(
			"oyster mushrooms caps",
			MUSHROOM_DESCRIPTION,
			CREMINI_DESCRIPTION
		),
		NutritionSeedAlias(
			"packs specially selected pork loin steaks",
			PORK_MINCE_DESCRIPTION,
			PORK_RIBS_DESCRIPTION
		),
		NutritionSeedAlias(
			"pancake syrup mrs butterworth",
			MAPLE_SYRUP_DESCRIPTION,
			CORN_SYRUP_DESCRIPTION
		),
		NutritionSeedAlias(
			"pancetta cooked",
			BACON_UNPREPARED_DESCRIPTION,
			BACON_DESCRIPTION
		),
		NutritionSeedAlias(
			"pancetta fat",
			BACON_UNPREPARED_DESCRIPTION,
			BACON_DESCRIPTION
		),
		NutritionSeedAlias("parmesan cheese wedge", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parmesan rind", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parmesan shavings", PARMESAN_DESCRIPTION),
		NutritionSeedAlias(
			"pastry baking mix recommended bisquick",
			FLOUR_DESCRIPTION,
			SELF_RISING_FLOUR_DESCRIPTION
		),
		NutritionSeedAlias("pears their", PEAR_DESCRIPTION),
		NutritionSeedAlias(
			"peashoot",
			EDIBLE_PODDED_PEA_DESCRIPTION,
			GREEN_PEAS_DESCRIPTION
		),
		NutritionSeedAlias(
			"peel any pith ripe lemon",
			LEMON_PEEL_DESCRIPTION,
			LEMON_DESCRIPTION
		),
		NutritionSeedAlias("pencil asparagus woody", ASPARAGUS_DESCRIPTION),
		NutritionSeedAlias("philadelphia cream cheese", CREAM_CHEESE_DESCRIPTION),
		NutritionSeedAlias(
			"pickled chilli",
			PICKLE_DESCRIPTION,
			JALAPENO_DESCRIPTION
		),
		NutritionSeedAlias("pimento seed", ALLSPICE_DESCRIPTION),
		NutritionSeedAlias("pinhead", OATS_DESCRIPTION),
		NutritionSeedAlias("pistachio paste", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("plain flour seasoned salt pepper", FLOUR_DESCRIPTION),
		NutritionSeedAlias(
			"poblano chillis",
			BELL_PEPPER_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"poblano peppers",
			BELL_PEPPER_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias(
			"porcini mushrooms water soft",
			CREMINI_DESCRIPTION,
			MUSHROOM_DESCRIPTION
		),
		NutritionSeedAlias(
			"pork belly frozen",
			PORK_BELLY_DESCRIPTION,
			BACON_UNPREPARED_DESCRIPTION
		),
		NutritionSeedAlias(
			"pork fillets",
			PORK_MINCE_DESCRIPTION,
			PORK_RIBS_DESCRIPTION
		),
		NutritionSeedAlias("pork percent fat", PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias(
			"pork shoulder very",
			PORK_MINCE_DESCRIPTION,
			PORK_RIBS_DESCRIPTION
		),
		NutritionSeedAlias(
			"pork shoulder very pices",
			PORK_MINCE_DESCRIPTION,
			PORK_RIBS_DESCRIPTION
		),
		NutritionSeedAlias(
			"pressed apple juice",
			APPLE_JUICE_DESCRIPTION,
			ORANGE_JUICE_DESCRIPTION
		),
		NutritionSeedAlias("pretzel salt", SALT_DESCRIPTION),
		NutritionSeedAlias(
			"prosecco",
			WHITE_WINE_DESCRIPTION,
			DESSERT_WINE_SWEET_DESCRIPTION
		),
		NutritionSeedAlias(
			"quorn kebab",
			VEGGIE_BURGER_DESCRIPTION,
			PLANT_MINCE_DESCRIPTION
		),
		NutritionSeedAlias("radicchio frisee head either head", RADICCHIO_DESCRIPTION),
		NutritionSeedAlias("radicchio very", RADICCHIO_DESCRIPTION),
		NutritionSeedAlias("raspberries preferred", RASPBERRY_DESCRIPTION),
		NutritionSeedAlias("raw shelled pumpkin seeds pepitas", PUMPKIN_SEED_DESCRIPTION),
		NutritionSeedAlias("raw unsalted sunflower kernels", SUNFLOWER_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"red green chilli",
			HOT_CHILI_RED_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION
		),
		NutritionSeedAlias("red green heirloom tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"red snapper",
			SNAPPER_DESCRIPTION,
			COD_DESCRIPTION
		),
		NutritionSeedAlias("rice paper wrappers", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("ripe avocados pit", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("ripe tomatoes red yellow", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"roasted anaheim chilli",
			HOT_CHILI_GREEN_DESCRIPTION,
			BELL_PEPPER_DESCRIPTION
		),
		NutritionSeedAlias("roasted salted sunflower seeds", SUNFLOWER_SEED_DESCRIPTION),
		NutritionSeedAlias(
			"rotisserie chicken meat juices",
			CHICKEN_BREAST_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("saffron threads", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("saffron threads threads", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("salmon fillets", SALMON_DESCRIPTION),
		NutritionSeedAlias(
			"salsa spicy you like",
			TOMATO_SAUCE_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"salt pinch black pepper",
			SALT_DESCRIPTION,
			BLACK_PEPPER_DESCRIPTION
		),
		NutritionSeedAlias("salt season", SALT_DESCRIPTION),
		NutritionSeedAlias(
			"scotch bonnet wear gloves when handling",
			HOT_CHILI_RED_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias(
			"sea scallops patted dry",
			SCALLOP_DESCRIPTION,
			SHRIMP_DESCRIPTION
		),
		NutritionSeedAlias("seedless oranges", ORANGE_DESCRIPTION),
		NutritionSeedAlias("seedless watermelon", WATERMELON_DESCRIPTION),
		NutritionSeedAlias(
			"sesame baps",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"sesame sub rolls",
			BURGER_BUN_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("shelled raw unsalted pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("shelled roasted pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("shrimp per tail onpeanut oil", SHRIMP_DESCRIPTION),
		NutritionSeedAlias(
			"skinless boneless flaky white fish",
			COD_DESCRIPTION,
			SOLE_DESCRIPTION
		),
		NutritionSeedAlias("skinless center salmon fillet", SALMON_DESCRIPTION),
		NutritionSeedAlias(
			"skinless chicken breasts thighs",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_THIGH_DESCRIPTION
		),
		NutritionSeedAlias(
			"skinless chicken fillets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("skinless cod fillet", COD_DESCRIPTION),
		NutritionSeedAlias(
			"slider rolls king hawaiian",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"soft prunes",
			PRUNE_DESCRIPTION,
			RAISIN_DESCRIPTION
		),
		NutritionSeedAlias(
			"sour orange juice",
			ORANGE_JUICE_DESCRIPTION,
			ORANGE_DESCRIPTION
		),
		NutritionSeedAlias("sour orange split", ORANGE_DESCRIPTION),
		NutritionSeedAlias(
			"soya milk",
			SOY_MILK_DESCRIPTION,
			OAT_MILK_DESCRIPTION
		),
		NutritionSeedAlias(
			"spicy sausage casings",
			PORK_SAUSAGE_DESCRIPTION,
			CHORIZO_DESCRIPTION
		),
		NutritionSeedAlias(
			"split chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"split chicken breasts fat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"split whole chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("squid bodies rings tentacles whole patted dry", SQUID_DESCRIPTION),
		NutritionSeedAlias("starchy potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias(
			"steamed white rice sesame seeds",
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("steel oats also sold pinhead irish oats", OATS_DESCRIPTION),
		NutritionSeedAlias(
			"strawberry jam",
			STRAWBERRY_DESCRIPTION,
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias("sultanas", RAISIN_DESCRIPTION),
		NutritionSeedAlias("sweet crisp apple", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"sweet glutinous rice flour",
			FLOUR_DESCRIPTION,
			RICE_DESCRIPTION
		),
		NutritionSeedAlias(
			"sweet harvest chunky tomatoes herbs",
			TOMATO_DICED_DESCRIPTION,
			TOMATO_CRUSHED_DESCRIPTION
		),
		NutritionSeedAlias(
			"sweet harvest red kidney beans",
			PINTO_BEAN_DESCRIPTION,
			BLACK_BEAN_DESCRIPTION
		),
		NutritionSeedAlias(
			"sweet rice flour",
			FLOUR_DESCRIPTION,
			RICE_DESCRIPTION
		),
		NutritionSeedAlias("sweet tart apple", APPLE_DESCRIPTION),
		NutritionSeedAlias("sweetened condensed milk label glue", CONDENSED_MILK_DESCRIPTION),
		NutritionSeedAlias("sweetened condensed milk unopened label", CONDENSED_MILK_DESCRIPTION),
		NutritionSeedAlias("tahini sauce garlic lemon", TAHINI_DESCRIPTION),
		NutritionSeedAlias("tarragon few", TARRAGON_DESCRIPTION),
		NutritionSeedAlias("tasso ham", HAM_DESCRIPTION),
		NutritionSeedAlias("tennessee whiskey", DISTILLED_SPIRITS_DESCRIPTION),
		NutritionSeedAlias("tequila gold", DISTILLED_SPIRITS_DESCRIPTION),
		NutritionSeedAlias(
			"thin boneless skinless sole",
			SOLE_DESCRIPTION,
			COD_DESCRIPTION
		),
		NutritionSeedAlias("this all purpose chicken stock used recipes that call stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"tinned pineapple juice",
			PINEAPPLE_DESCRIPTION,
			ORANGE_JUICE_DESCRIPTION
		),
		NutritionSeedAlias(
			"tomato passata strained tomatoes",
			TOMATO_CRUSHED_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION
		),
		NutritionSeedAlias("tons cilantro", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("tortilla chips whole grain tortilla chips", CORN_TORTILLA_DESCRIPTION),
		NutritionSeedAlias(
			"ttbsp crystallized ginger",
			GINGER_GROUND_DESCRIPTION,
			GINGER_FRESH_DESCRIPTION
		),
		NutritionSeedAlias("tuna loin", TUNA_DESCRIPTION),
		NutritionSeedAlias("tuna oil", TUNA_DESCRIPTION),
		NutritionSeedAlias("uncooked old fashioned rolled oats", OATS_DESCRIPTION),
		NutritionSeedAlias(
			"uncooked wild rice",
			WILD_RICE_DESCRIPTION,
			BROWN_RICE_DESCRIPTION
		),
		NutritionSeedAlias("unsalted cashew nuts", CASHEW_DESCRIPTION),
		NutritionSeedAlias("untoasted almonds", ALMOND_DESCRIPTION),
		NutritionSeedAlias("untoasted pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias(
			"vegetarian chicken fillet",
			VEGGIE_BURGER_DESCRIPTION,
			PLANT_MINCE_DESCRIPTION
		),
		NutritionSeedAlias("vegetarian gelatin", GELATIN_DESCRIPTION),
		NutritionSeedAlias("very honeydew melon melon", HONEYDEW_DESCRIPTION),
		NutritionSeedAlias("very ripe bananas", BANANA_DESCRIPTION),
		NutritionSeedAlias("very ripe bananas flesh", BANANA_DESCRIPTION),
		NutritionSeedAlias(
			"vietnamese coriander",
			CILANTRO_DESCRIPTION,
			MINT_DESCRIPTION
		),
		NutritionSeedAlias(
			"vinegar like cider balsamic",
			CIDER_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION
		),
		NutritionSeedAlias("vinegary hot sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias(
			"white crab meat cooked",
			CRAB_DESCRIPTION,
			CRAB_RAW_DESCRIPTION
		),
		NutritionSeedAlias(
			"white fleshed fish fillets",
			COD_DESCRIPTION,
			SOLE_DESCRIPTION
		),
		NutritionSeedAlias(
			"white pepper salt",
			WHITE_PEPPER_DESCRIPTION,
			SALT_DESCRIPTION
		),
		NutritionSeedAlias("whole calamari cleaned", SQUID_DESCRIPTION),
		NutritionSeedAlias(
			"whole chicken breasts split",
			CHICKEN_BREAST_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION
		),
		NutritionSeedAlias("whole chicken close possible", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"wholemeal baps",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"wholemeal sub rolls",
			BURGER_BUN_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"yellow shi wheat flour noodles",
			EGG_NOODLE_DESCRIPTION,
			PASTA_DESCRIPTION
		),
		NutritionSeedAlias(
			"zest juice lemon zest juice",
			LEMON_JUICE_DESCRIPTION,
			LEMON_PEEL_DESCRIPTION
		),
		NutritionSeedAlias("coconut extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias(
			"garlic herb goat cheese recommended montrachet",
			FETA_DESCRIPTION,
			QUESO_FRESCO_DESCRIPTION
		),
		NutritionSeedAlias(
			"high quality dark chocolate percent cacao",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("light corn syrupflaky sea salt topping", CORN_SYRUP_DESCRIPTION),
		NutritionSeedAlias("marinated pork", PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias("powdered gelatin broth", GELATIN_DESCRIPTION),
		NutritionSeedAlias(
			"raspberry jam",
			RASPBERRY_DESCRIPTION,
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias("refrigerated sliceable sugar cookies", GRAHAM_DESCRIPTION),
		NutritionSeedAlias(
			"crumpet",
			WHITE_BREAD_DESCRIPTION,
			BAGUETTE_DESCRIPTION
		),
		NutritionSeedAlias("Tabasco", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("louis ribs", PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias("orange sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("protein all purpose flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("red pepper flakes heavier extra", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("scallions whites", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions whites", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias(
			"almond cream",
			CREAM_DESCRIPTION,
			ALMOND_DESCRIPTION
		),
		NutritionSeedAlias(
			"annatto oil",
			CANOLA_OIL_DESCRIPTION,
			CORN_OIL_DESCRIPTION
		),
		NutritionSeedAlias("avocado flesh", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("avocados pit", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("avocados", AVOCADO_DESCRIPTION),
		NutritionSeedAlias(
			"baguette bread",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("bananas flesh", BANANA_DESCRIPTION),
		NutritionSeedAlias("bananas", BANANA_DESCRIPTION),
		NutritionSeedAlias(
			"black eyed peas",
			BLACK_EYED_PEA_DESCRIPTION,
			BLACK_BEAN_DESCRIPTION
		),
		NutritionSeedAlias(
			"boneless lamb stew meat lamb neck leg",
			LAMB_MINCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast long",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast two",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"brewed espresso",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("butterbeans", BUTTER_BEAN_DESCRIPTION),
		NutritionSeedAlias(
			"canned chipotle chiles",
			HOT_CHILI_RED_DESCRIPTION,
			JALAPENO_DESCRIPTION
		),
		NutritionSeedAlias("cannellini", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias(
			"canary mayocoba",
			PINTO_BEAN_DESCRIPTION,
			BLACK_BEAN_DESCRIPTION
		),
		NutritionSeedAlias("chick peas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias(
			"chicken paillards chef below",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("chicken quarters", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"chicken stock canned broth substituted",
			CHICKEN_BROTH_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken stock made chicken stock diluted",
			CHICKEN_BROTH_DESCRIPTION
		),
		NutritionSeedAlias(
			"chicken thigh fillets",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("chickens eighths", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"clarified butter vegetable oil",
			BUTTER_DESCRIPTION,
			BUTTER_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias(
			"crisp baguettes",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crostini",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty baps",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty bread",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"crusty rolls",
			BURGER_BUN_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION
		),
		NutritionSeedAlias(
			"cube steak round steak that been extra",
			BEEF_CHUCK_DESCRIPTION
		),
		NutritionSeedAlias(
			"dark chocolate recommended",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("dollops light mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("dollops mayonnaise spark chilli", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("elephant garlic cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"escarole stem end wilted multiple changes",
			RADICCHIO_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION
		),
		NutritionSeedAlias("extra british beef stir fry", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias(
			"fat creme fraiche",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("firm avocados", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("flour tortilla grilled", FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias(
			"french bread sub rolls",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("full bodied red wine madiran", RED_WINE_DESCRIPTION),
		NutritionSeedAlias("garlic cracked", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"garlic crouton",
			WHITE_BREAD_DESCRIPTION,
			BAGUETTE_DESCRIPTION
		),
		NutritionSeedAlias(
			"german sweet chocolate",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("ginger paper", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias(
			"hard dough bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("honeycrisp", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"jaggery brown sugar",
			BROWN_SUGAR_DESCRIPTION,
			SUGAR_DESCRIPTION
		),
		NutritionSeedAlias("jalapeno seeds heat", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("lemon juice lemonc", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemongrass stalk", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias(
			"lemongrass stalks outer top third rest",
			LEMONGRASS_DESCRIPTION
		),
		NutritionSeedAlias("light corn syrupflaky sea salt", CORN_SYRUP_DESCRIPTION),
		NutritionSeedAlias(
			"louisiana cayenne hot sauce",
			HOT_SAUCE_DESCRIPTION,
			CAYENNE_DESCRIPTION
		),
		NutritionSeedAlias("low sodium chicken", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("mayonnaise bread", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"mexican cheese blend recommended",
			CHEDDAR_DESCRIPTION,
			MOZZARELLA_DESCRIPTION
		),
		NutritionSeedAlias("mexican green onions", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("mini pitas", PITA_DESCRIPTION),
		NutritionSeedAlias("mushrooms any", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias(
			"neutral oil like corn",
			CORN_OIL_DESCRIPTION,
			CANOLA_OIL_DESCRIPTION
		),
		NutritionSeedAlias("new york strip", BEEF_STRIP_DESCRIPTION, BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("nigerian curry powder", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias(
			"msg neutral oil such vegetable oil",
			SOYBEAN_OIL_DESCRIPTION,
			CANOLA_OIL_DESCRIPTION
		),
		NutritionSeedAlias("paper prosciutto", HAM_DESCRIPTION),
		NutritionSeedAlias("pinches salt", SALT_DESCRIPTION),
		NutritionSeedAlias(
			"plums stoned plums",
			PLUM_DESCRIPTION,
			PRUNE_DESCRIPTION
		),
		NutritionSeedAlias(
			"pork shoulder pices",
			PORK_BELLY_DESCRIPTION,
			PORK_MINCE_DESCRIPTION
		),
		NutritionSeedAlias(
			"pouch cooked coconut rice",
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("purple sweet potatoes", SWEET_POTATO_DESCRIPTION),
		NutritionSeedAlias("quick brown rice cooked", BROWN_RICE_DESCRIPTION),
		NutritionSeedAlias("radicchio frisee head either", RADICCHIO_DESCRIPTION),
		NutritionSeedAlias("rare roast beef", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("reduced salt soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("ribs celery leafy", CELERY_DESCRIPTION),
		NutritionSeedAlias("rice vermicelli noodles", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("roasted mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("roma tomatoes under", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("rustic italian bread", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias(
			"semi sweet chocolate baking bar",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION
		),
		NutritionSeedAlias("serrano peppers seeds", SERRANO_DESCRIPTION),
		NutritionSeedAlias("sherry sweet option", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("skinless center salmon", SALMON_DESCRIPTION),
		NutritionSeedAlias(
			"soft baguettes",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION
		),
		NutritionSeedAlias("sour orange", ORANGE_DESCRIPTION),
		NutritionSeedAlias("squid bodies rings tentacles", SQUID_DESCRIPTION),
		NutritionSeedAlias("steel oats also sold pinhead", OATS_DESCRIPTION),
		NutritionSeedAlias(
			"strip orange peel white pith",
			ORANGE_DESCRIPTION,
			LEMON_PEEL_DESCRIPTION
		),
		NutritionSeedAlias("sweet fennel sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("thai soy sauce", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("tiny pinch kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("tomato passata tomatoes", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias(
			"tomatoes juice",
			TOMATO_RAW_DESCRIPTION,
			TOMATO_SAUCE_DESCRIPTION
		),
		NutritionSeedAlias("tomatoes red yellow", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("unsalted chicken", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("vanilla bean", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla bean seeds", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla seeds", VANILLA_DESCRIPTION),
		NutritionSeedAlias("whole allspice", ALLSPICE_DESCRIPTION),
		NutritionSeedAlias("whole allspice berries", ALLSPICE_DESCRIPTION),
	)
}
