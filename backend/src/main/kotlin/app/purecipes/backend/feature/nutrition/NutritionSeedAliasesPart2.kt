package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ASPARAGUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.AVOCADO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_UNPREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BALSAMIC_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAY_LEAF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEANSPROUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUE_CHEESE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREADCRUMB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BURGER_BUN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTERNUT_SQUASH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CABBAGE_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANNELLINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARDAMOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARROT_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKPEA_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHORIZO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CIDER_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CILANTRO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CINNAMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CLAM_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CLOVE_SPICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCOA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCONUT_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCONUT_YOGURT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COOKING_SPRAY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORIANDER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_COB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREMINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CUCUMBER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CUMIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CURRY_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DARK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DARK_CHOCOLATE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_DRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_SWEET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DRY_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EDIBLE_PODDED_PEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_YOLK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FETA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FISH_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLATBREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GELATIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_GROUND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRAHAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_TEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRUYERE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GRUYERE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HALLOUMI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HONEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_GREEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ICEBERG_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_COFFEE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_YEAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JALAPENO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KALE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMONGRASS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_PEEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LENTIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MANGO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MINT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSHROOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.NUTMEG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OREGANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PAPRIKA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARMESAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARSLEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARSLEY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARSNIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PICKLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINEAPPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINE_NUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINTO_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PITA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLANT_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POMEGRANATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.QUESO_FRESCO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RADICCHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMAINE_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROSEMARY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SERRANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SESAME_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SESAME_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHALLOT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOYBEAN_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SOY_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPINACH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPRING_ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.STRAWBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SWEET_POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAMARIND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAMARIND_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATILLO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATILLO_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_CRUSHED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_PASTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGETABLE_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGGIE_BURGER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WALNUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_CHESTNUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_CHESTNUT_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_CHICKEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WORCESTERSHIRE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.YELLOW_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.YOGURT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ZUCCHINI_DESCRIPTION

internal object NutritionSeedAliasesPart2 {

	val aliases: List<NutritionSeedAlias> = listOf(
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
		NutritionSeedAlias("all purpose flour extra flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("amaro nonino", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("ancho chiles water", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias(
			"angostura bitters",
			DESSERT_WINE_DRY_DESCRIPTION,
			DESSERT_WINE_SWEET_DESCRIPTION,
		),
		NutritionSeedAlias("anise liqueur", DESSERT_WINE_SWEET_DESCRIPTION, ANISE_DESCRIPTION),
		NutritionSeedAlias("anise seeds spice grinder", ANISE_DESCRIPTION),
		NutritionSeedAlias("aperol", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("asian chile sauce", HOT_SAUCE_DESCRIPTION, CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("bacon wide", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("baguette half split all", BAGUETTE_DESCRIPTION, ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias(
			"baker german sweet chocolate brand",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("basmati rice water", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("beef bouillon", CHICKEN_BROTH_DESCRIPTION, VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("beefsteak tomato remove seeds", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("berbere spice blend", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias(
			"bittersweet cacao",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
			COCOA_DESCRIPTION,
		),
		NutritionSeedAlias("block processed cheese", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias(
			"bone chicken breast skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"bone skin chicken thighs thighs",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("boneless pork butt", PORK_MINCE_DESCRIPTION, PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias(
			"boneless skinless chicken breast half cutlets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast split two",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breast thin long",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts flat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"boneless skinless chicken breasts thin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("bouillon flavoring", VEGETABLE_BROTH_DESCRIPTION, CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("braising steak mince", BEEF_CHUCK_DESCRIPTION, PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias(
			"brandy armagnac",
			DESSERT_WINE_DRY_DESCRIPTION,
			DESSERT_WINE_SWEET_DESCRIPTION,
		),
		NutritionSeedAlias("breadcrumbs biscuit crumbs preferred", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("brined white beans", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias(
			"brown bread your choice",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"buckwheat soba noodle",
			EGG_NOODLE_DESCRIPTION,
			PASTA_DESCRIPTION,
			RICE_NOODLE_DESCRIPTION,
		),
		NutritionSeedAlias("bulk sweet", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("butter extra", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("butter lettuce", ICEBERG_LETTUCE_DESCRIPTION, ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("butter naan", FLATBREAD_DESCRIPTION, PITA_DESCRIPTION),
		NutritionSeedAlias("butter three", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("caesar mayo", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"calabrian chile paste",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION,
		),
		NutritionSeedAlias(
			"canary mayocoba peruano beans",
			PINTO_BEAN_DESCRIPTION,
			BUTTER_BEAN_DESCRIPTION,
		),
		NutritionSeedAlias(
			"canned chipotle chiles your heat",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias(
			"cape estate extra virgin olive oil",
			OLIVE_OIL_DESCRIPTION,
			OLIVE_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("caramel extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("carrot across", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots holes grater", CARROT_DESCRIPTION),
		NutritionSeedAlias("carrots two", CARROT_DESCRIPTION),
		NutritionSeedAlias("cassia bark", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("cassia bark often sold cinnamon", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("cauliflower head", CAULIFLOWER_DESCRIPTION),
		NutritionSeedAlias("celery stalk thin half", CELERY_DESCRIPTION),
		NutritionSeedAlias("chaat masala seasoning", CURRY_POWDER_DESCRIPTION, CUMIN_DESCRIPTION),
		NutritionSeedAlias("chapati flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("cheddar using holes grater", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("cheese monterey jack best", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("cherry brandy", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias(
			"chicken bone skin",
			WHOLE_CHICKEN_DESCRIPTION,
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts bone fat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts out",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts ribs",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken breasts thin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken broth powder", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"chicken sausage casings",
			PORK_SAUSAGE_DESCRIPTION,
			CHICKEN_MINCE_DESCRIPTION,
			CHICKEN_MINCE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken sausage links",
			PORK_SAUSAGE_DESCRIPTION,
			CHICKEN_MINCE_DESCRIPTION,
			CHICKEN_MINCE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken stock canned broth may substituted", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock made stock cube", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock made stock hot", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock stock cube", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock two", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("chicken stock water", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"chicken tenders half",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken thigh fillets skin bones organic",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken thighs skin bones",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chicken thighs smaller ones bone skin",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chicken wings drumettes flats", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias("chicken wings flats drumettes", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"chickpea flour available speciality markets health food stores",
			CHICKPEA_FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("chickpeas dry beans cooked dente", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("chilli garlic sauce", HOT_SAUCE_DESCRIPTION, CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("chinese sausage", CHORIZO_DESCRIPTION, PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("chipolata sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias(
			"chipotle chile chipotles adobo",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chipotle chilli adobo sauce",
			CHILI_POWDER_DESCRIPTION,
			ANCHO_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias("chorizo sausage casings", CHORIZO_DESCRIPTION),
		NutritionSeedAlias(
			"chuka soba noodles",
			EGG_NOODLE_DESCRIPTION,
			RICE_NOODLE_DESCRIPTION,
			PASTA_DESCRIPTION,
		),
		NutritionSeedAlias("chunk rock sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("cinnamon half", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("cinnamon mexican", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("clarified butter soft", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("closed chestnut mushrooms", MUSHROOM_DESCRIPTION, CREMINI_DESCRIPTION),
		NutritionSeedAlias("comte", GRUYERE_DESCRIPTION, GRUYERE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("cooked black beans water", BLACK_BEAN_DESCRIPTION),
		NutritionSeedAlias(
			"cooked chicken breast thin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"cooked chicken rotisserie other leftover chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cooked ramen noodles", EGG_NOODLE_DESCRIPTION, PASTA_DESCRIPTION),
		NutritionSeedAlias(
			"cooked rspca assured chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cooked white rice hot", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("cooking sherry", DESSERT_WINE_DRY_DESCRIPTION),
		NutritionSeedAlias("coriander seeds cracked", CORIANDER_SEED_DESCRIPTION),
		NutritionSeedAlias("corkscrew shaped pasta lines", PASTA_DESCRIPTION),
		NutritionSeedAlias("corn cob", CORN_COB_DESCRIPTION),
		NutritionSeedAlias(
			"corn tortillas eighths stale spread out dry overnight",
			CORN_TORTILLA_DESCRIPTION,
		),
		NutritionSeedAlias("corn tortillas thin", CORN_TORTILLA_DESCRIPTION),
		NutritionSeedAlias(
			"country white bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("crumbled blue cheese recommended cabrales", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("crumbled blue cheese recommended stilton", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("crumbled cotija cheese lime", QUESO_FRESCO_DESCRIPTION, FETA_DESCRIPTION),
		NutritionSeedAlias("crumbled queso fresco cheese", QUESO_FRESCO_DESCRIPTION, FETA_DESCRIPTION),
		NutritionSeedAlias("cucumber thin half", CUCUMBER_DESCRIPTION),
		NutritionSeedAlias("cured spanish chorizo", CHORIZO_DESCRIPTION),
		NutritionSeedAlias("curly leaf kale", KALE_DESCRIPTION),
		NutritionSeedAlias("curry powder palmfuls", CURRY_POWDER_DESCRIPTION),
		NutritionSeedAlias("dark chilli powder", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias(
			"dark chinese vinegar",
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias(
			"dark chocolate between cacao",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"dark chocolate cocoa solids",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"dark chocolate recommended ghirardelli",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("dark soy sauce table", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("delicata squash rings", BUTTERNUT_SQUASH_DESCRIPTION),
		NutritionSeedAlias("demarara sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt brining", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt cooking water", SALT_DESCRIPTION),
		NutritionSeedAlias("diamond crystal kosher salt extra seasoning", SALT_DESCRIPTION),
		NutritionSeedAlias(
			"diamond crystal kosher salt table salt use half much volume",
			SALT_DESCRIPTION,
		),
		NutritionSeedAlias("ditalini pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("dollop sour cream", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("dollops hellmann light mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("dollops hellmann mayonnaise spark chilli", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"double cream whipped soft peaks",
			CREAM_DESCRIPTION,
			CREAM_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("double strength chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("dozen mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("dry cured streaky bacon", BACON_DESCRIPTION, BACON_UNPREPARED_DESCRIPTION),
		NutritionSeedAlias("dry white wine recommended chilean sauvignon blanc", WHITE_WINE_DESCRIPTION),
		NutritionSeedAlias("duke mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("dutch cocoa powder", COCOA_DESCRIPTION),
		NutritionSeedAlias("egg noodles wide ones", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("elephant ear garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias("elephant ear garlic cloves", GARLIC_DESCRIPTION),
		NutritionSeedAlias("envelopes unflavored powdered gelatin", GELATIN_DESCRIPTION),
		NutritionSeedAlias("extra lean british beef stir fry", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("extra sharp cheddar cheese holes grater", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("extra sharp orange cheddar", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("fat rice noodles cooked", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("firm ripe avocados", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("firmly light brown sugar", BROWN_SUGAR_DESCRIPTION, SUGAR_DESCRIPTION),
		NutritionSeedAlias("flaky sea salt topping", SALT_DESCRIPTION),
		NutritionSeedAlias("flat rice noodle", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("flour tortilla grilled thin", FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("french puy lentils", LENTIL_DESCRIPTION),
		NutritionSeedAlias("fresno chile very", HOT_CHILI_RED_DESCRIPTION, JALAPENO_DESCRIPTION),
		NutritionSeedAlias("fresno chillies rings", HOT_CHILI_RED_DESCRIPTION, JALAPENO_DESCRIPTION),
		NutritionSeedAlias("frozen pearl onions", ONION_DESCRIPTION),
		NutritionSeedAlias("frozen puff pastry one half", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias(
			"full fat cream cheese",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"full fat philadelphia cream cheese",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("full fat yoghurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("garam masala palmful", CURRY_POWDER_DESCRIPTION, CUMIN_DESCRIPTION),
		NutritionSeedAlias("garlic clove half", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic cloves rasp", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic cracked skin split", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic half", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic head", GARLIC_DESCRIPTION),
		NutritionSeedAlias("garlic skin", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"ghirardelli semi sweet chocolate baking bar",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("ghirardelli unsweetened cocoa", COCOA_DESCRIPTION),
		NutritionSeedAlias("ginger paper thin", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("ginger whacked open flat side knife", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("glass noodle", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("glug olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("goat cheese crumbled", FETA_DESCRIPTION, QUESO_FRESCO_DESCRIPTION),
		NutritionSeedAlias("good apple cider vinegar", CIDER_VINEGAR_DESCRIPTION),
		NutritionSeedAlias("good chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias(
			"good dark chocolate",
			DARK_CHOCOLATE_DESCRIPTION,
			DARK_CHOCOLATE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("good few pinches salt", SALT_DESCRIPTION),
		NutritionSeedAlias("good mayonnaise bread", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias(
			"good quality red wine vinegar",
			CIDER_VINEGAR_DESCRIPTION,
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("gorgonzola cheese crumbled crumbled", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("greek seasoning", OREGANO_DESCRIPTION, GARLIC_POWDER_DESCRIPTION),
		NutritionSeedAlias("greek style natural yogurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("green bell pepper ribs seeds", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("green bell pepper thin", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias(
			"green bird eye chilli",
			HOT_CHILI_GREEN_DESCRIPTION,
			HOT_CHILI_RED_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias(
			"green cabbage cabbage core intact",
			CABBAGE_GREEN_DESCRIPTION,
			CABBAGE_DESCRIPTION,
		),
		NutritionSeedAlias("green cardamom pod", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("green cardamom pods", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("green finger chilli", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias(
			"green leaf lettuce vertically",
			ROMAINE_LETTUCE_DESCRIPTION,
			ICEBERG_LETTUCE_DESCRIPTION,
		),
		NutritionSeedAlias("green onion white light green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("green onions green white", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("green onions white green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("green pepper julienne", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias(
			"grill seasoning blend",
			PAPRIKA_DESCRIPTION,
			GARLIC_POWDER_DESCRIPTION,
			ONION_POWDER_DESCRIPTION,
		),
		NutritionSeedAlias(
			"groundnut oil peanut",
			PEANUT_OIL_DESCRIPTION,
			PEANUT_OIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("guajillo chiles seeds", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("guajillo chillies", ANCHO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("guanciale pancetta", BACON_UNPREPARED_DESCRIPTION, BACON_DESCRIPTION),
		NutritionSeedAlias("guinness extra stout beer", BEER_DESCRIPTION),
		NutritionSeedAlias("half bunch coriander", CILANTRO_DESCRIPTION),
		NutritionSeedAlias("hard boiled eggs half", EGG_DESCRIPTION),
		NutritionSeedAlias(
			"hatch green chile peppers",
			HOT_CHILI_GREEN_DESCRIPTION,
			HOT_CHILI_RED_DESCRIPTION,
		),
		NutritionSeedAlias("head escarole", RADICCHIO_DESCRIPTION, ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("head napa cabbage", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("head red cabbage", CABBAGE_RED_DESCRIPTION),
		NutritionSeedAlias("heaped stonemill chilli powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("heaped stonemill paprika", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("hispi cabbage", CABBAGE_GREEN_DESCRIPTION, CABBAGE_DESCRIPTION),
		NutritionSeedAlias("hot italian sausage casings", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias(
			"imported italian fontina cheese",
			GRUYERE_DESCRIPTION,
			GRUYERE_FALLBACK_DESCRIPTION,
			CHEDDAR_DESCRIPTION,
		),
		NutritionSeedAlias("imported prosciutto", HAM_DESCRIPTION),
		NutritionSeedAlias(
			"instant espresso",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"italian herb mix",
			OREGANO_DESCRIPTION,
			BASIL_DESCRIPTION,
			BASIL_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"jaggery brown sugar you find jaggery",
			BROWN_SUGAR_DESCRIPTION,
			SUGAR_DESCRIPTION,
		),
		NutritionSeedAlias("jalapeno chiles", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jalapeno chilli", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jalapeno peppers seed", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("jalapeno remove seeds less heat", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("japanese sweet potatoes", SWEET_POTATO_DESCRIPTION),
		NutritionSeedAlias("jigger triple sec orange flavored liqueur", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("juice two limes", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("juice zest orange", ORANGE_JUICE_DESCRIPTION, ORANGE_DESCRIPTION),
		NutritionSeedAlias("kale center", KALE_DESCRIPTION),
		NutritionSeedAlias("korean chilli flake", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("kosher salt peppe", SALT_DESCRIPTION),
		NutritionSeedAlias("ladle chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("lasagne sheet", PASTA_DESCRIPTION, EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("leek white light green part", LEEK_DESCRIPTION),
		NutritionSeedAlias("leerdammer cheese", CHEDDAR_DESCRIPTION, GRUYERE_DESCRIPTION),
		NutritionSeedAlias("lemon juice very ripe lemon", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lemongrass stalk pale white part segments", LEMONGRASS_DESCRIPTION),
		NutritionSeedAlias(
			"less fat creme fraiche",
			CREAM_SOUR_DESCRIPTION,
			CREAM_SOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("light lager", BEER_DESCRIPTION),
		NutritionSeedAlias("lighter ready rolled puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("lime zest juice lime", LIME_JUICE_DESCRIPTION, LIME_DESCRIPTION),
		NutritionSeedAlias("linguini pasta cooked", PASTA_DESCRIPTION),
		NutritionSeedAlias("links sweet italian pork sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("little nutmeg", NUTMEG_DESCRIPTION),
		NutritionSeedAlias(
			"loaf baguette bread day old",
			BAGUETTE_DESCRIPTION,
			ITALIAN_BREAD_DESCRIPTION,
		),
		NutritionSeedAlias("loaf crusty french bread", BAGUETTE_DESCRIPTION, ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias("long cinnamon", CINNAMON_DESCRIPTION),
		NutritionSeedAlias(
			"long grain white rice not rinse",
			RICE_DESCRIPTION,
			RICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("long red chiles", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("loose dry green tea", GREEN_TEA_DESCRIPTION),
		NutritionSeedAlias(
			"louisiana style cayenne hot sauce",
			HOT_SAUCE_DESCRIPTION,
			CAYENNE_DESCRIPTION,
		),
		NutritionSeedAlias(
			"operative british chicken breast fillets",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("operative easy long grain rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("parmesiano reggiano cheese microplane", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parmigiano reggiano cheese microplaned", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("parmigiano reggiano cheese rind", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("pasta dough wide ribbons", PASTA_DESCRIPTION, EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("pecorino", ROMANO_DESCRIPTION),
		NutritionSeedAlias("penne pasta cooked", PASTA_DESCRIPTION),
		NutritionSeedAlias("pepper jelly", HOT_SAUCE_DESCRIPTION, JALAPENO_DESCRIPTION),
		NutritionSeedAlias("phyllo pastry frozen", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("pickled jalapenos one", JALAPENO_DESCRIPTION),
		NutritionSeedAlias("pickled mild banana pepper rings", PICKLE_DESCRIPTION, JALAPENO_DESCRIPTION),
		NutritionSeedAlias(
			"pickling spice",
			CLOVE_SPICE_DESCRIPTION,
			CINNAMON_DESCRIPTION,
			MUSTARD_SEED_DESCRIPTION,
		),
		NutritionSeedAlias("pimenton vera", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("pink peppercorns", BLACK_PEPPER_DESCRIPTION, WHITE_PEPPER_DESCRIPTION),
		NutritionSeedAlias("pita squares", PITA_DESCRIPTION),
		NutritionSeedAlias("plain flour tortillas", FLOUR_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("plain full fat whole milk greek yoghurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("plain strained whole milk greek style yogurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("plain whole milk strained greek style yogurt", YOGURT_DESCRIPTION),
		NutritionSeedAlias("plain yoghurt smooth", YOGURT_DESCRIPTION),
		NutritionSeedAlias("plum roma tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("plum tomatoes basil", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias(
			"poached chicken",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("pomegranate seeds pomegranate", POMEGRANATE_DESCRIPTION),
		NutritionSeedAlias("pork stewing meat", PORK_MINCE_DESCRIPTION, PORK_RIBS_DESCRIPTION),
		NutritionSeedAlias("potatoes big", POTATO_DESCRIPTION),
		NutritionSeedAlias("powdered unflavored gelatin", GELATIN_DESCRIPTION),
		NutritionSeedAlias("prawns tails", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("preserved horseradish", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("prosciutto parma", HAM_DESCRIPTION),
		NutritionSeedAlias("proscuitto ham", HAM_DESCRIPTION),
		NutritionSeedAlias("purple sweet potatoes very", SWEET_POTATO_DESCRIPTION),
		NutritionSeedAlias("quart strawberries", STRAWBERRY_DESCRIPTION),
		NutritionSeedAlias("quarts vegetable oil", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias(
			"quick cooking brown rice cooked",
			BROWN_RICE_DESCRIPTION,
			BROWN_RICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("ready rolled filo pastry half", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("real bacon bits", BACON_DESCRIPTION, BACON_UNPREPARED_DESCRIPTION),
		NutritionSeedAlias("red bell pepper ribs seeds", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red bell pepper thin", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red bird eye chilli", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red chile powder kashmiri", CHILI_POWDER_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red jalapeno", JALAPENO_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("red long chile", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red onion rings", ONION_DESCRIPTION),
		NutritionSeedAlias("red pepper flakes healthy", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("red pepper julienne", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("red potatoes very", POTATO_DESCRIPTION),
		NutritionSeedAlias(
			"red wine vinegar italian",
			CIDER_VINEGAR_DESCRIPTION,
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("reduced salt soy sauce your", SOY_SAUCE_DESCRIPTION),
		NutritionSeedAlias("refrigerated hot breakfast sausage", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("ribs celery leafy green", CELERY_DESCRIPTION),
		NutritionSeedAlias("ripe avocados", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("ripe avocados pit skin", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("ripe beefsteak tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("ripe plum tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("ripe plum tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("ripe plum tomatoes thin", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("risotto rice used riso gallo", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("roma tomatoes under ripe", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("rosemary few", ROSEMARY_DESCRIPTION),
		NutritionSeedAlias("rosewater", ORANGE_JUICE_DESCRIPTION, VANILLA_DESCRIPTION),
		NutritionSeedAlias(
			"rotisserie chicken breast yield meat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"rotisserie chicken meat still warm",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("rspca assured eggs", EGG_DESCRIPTION),
		NutritionSeedAlias("rum flavored extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("russet potatoes eighths", POTATO_DESCRIPTION),
		NutritionSeedAlias("russet potatoes very mandoline", POTATO_DESCRIPTION),
		NutritionSeedAlias("sake japanese rice wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("salt cracked black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("salted butter irish butter", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("salted vegetable stock", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("sambal hot sauce", HOT_SAUCE_DESCRIPTION, CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("sanding sugar coating", SUGAR_DESCRIPTION),
		NutritionSeedAlias("sazon achiote", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("scallion trimmings", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("scallions greens included", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("sea salt cracked black pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("seasoned italian breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("serrano chile pepper", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias("serrano chilli seeds", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias("serrano pepper whole", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias(
			"serrano peppers seeds you don want spicy",
			SERRANO_DESCRIPTION,
			HOT_CHILI_GREEN_DESCRIPTION,
		),
		NutritionSeedAlias("sesame oil extra", SESAME_OIL_DESCRIPTION),
		NutritionSeedAlias("several grinds black pepper", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("shah jeera", CUMIN_DESCRIPTION),
		NutritionSeedAlias("shallot thin", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shallots root", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("shaoxing wine chinese rice wine", RICE_WINE_DESCRIPTION),
		NutritionSeedAlias("sherry sweet option like pedro ximenez", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("shittake mushrooms rehydrated", MUSHROOM_DESCRIPTION, CREMINI_DESCRIPTION),
		NutritionSeedAlias("shrimp gulf shrimp", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("shrimp patted dry", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("shrimp per shells separately", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("shrimp per tail", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("shrimp tails", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("sichuan peppercorn", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("sichuan peppercorns spice mill", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias(
			"skinless boneless chicken breasts grain",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"skinless boneless chicken breasts half",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"skinless chicken breasts bone",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"skinless chicken leg quarters",
			CHICKEN_THIGH_DESCRIPTION,
			CHICKEN_THIGH_FALLBACK_DESCRIPTION,
			WHOLE_CHICKEN_DESCRIPTION,
		),
		NutritionSeedAlias("skirt steak whole skirt steak", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("solesta olive oil", OLIVE_OIL_DESCRIPTION, OLIVE_OIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("spaghetti another pasta choice", PASTA_DESCRIPTION),
		NutritionSeedAlias("spanish onions julienne", ONION_DESCRIPTION),
		NutritionSeedAlias("spanish onions skin", ONION_DESCRIPTION),
		NutritionSeedAlias("splash lime juice", LIME_JUICE_DESCRIPTION),
		NutritionSeedAlias("splenda calorie sweetener granulated", SUGAR_DESCRIPTION),
		NutritionSeedAlias(
			"split chicken breasts bone fat",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"split whole chicken breasts bone skin",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"spring mix",
			SPINACH_DESCRIPTION,
			ICEBERG_LETTUCE_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION,
		),
		NutritionSeedAlias("spring onion green", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias("spring onions lengthways thin", SPRING_ONION_DESCRIPTION),
		NutritionSeedAlias(
			"stale white bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("stalk celery any included", CELERY_DESCRIPTION),
		NutritionSeedAlias("stalks rosemary", ROSEMARY_DESCRIPTION),
		NutritionSeedAlias("starchy potatoes skin", POTATO_DESCRIPTION),
		NutritionSeedAlias(
			"stewed tomatoes pureed",
			TOMATO_DICED_DESCRIPTION,
			TOMATO_CRUSHED_DESCRIPTION,
		),
		NutritionSeedAlias("stonemill basil", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("stonemill garlic", GARLIC_POWDER_DESCRIPTION, GARLIC_DESCRIPTION),
		NutritionSeedAlias("strained lemon juice", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("strip lemon zest", LEMON_PEEL_DESCRIPTION),
		NutritionSeedAlias(
			"strong black coffee",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"strong brewed coffee",
			INSTANT_COFFEE_DESCRIPTION,
			INSTANT_COFFEE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("sun tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("sun tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("sundried tomatoes oil", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("sunflower oil extra", CANOLA_OIL_DESCRIPTION, SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias(
			"super young crisp lettuce",
			ICEBERG_LETTUCE_DESCRIPTION,
			ROMAINE_LETTUCE_DESCRIPTION,
		),
		NutritionSeedAlias("sushi rice cooked", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("sweet fennel sausage links", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("sweet fennel sausage thin", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("sweet paprika full", PAPRIKA_DESCRIPTION),
		NutritionSeedAlias("sweet red pointed pepper", RED_BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("tabasco brand chipotle pepper sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("tabasco brand green jalapeno pepper sauce", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias(
			"taco seasoning your own mix",
			CHILI_POWDER_DESCRIPTION,
			CUMIN_DESCRIPTION,
			PAPRIKA_DESCRIPTION,
		),
		NutritionSeedAlias("thai basil hung que", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("thai basil off stem", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias(
			"thai chilli paste",
			CHILI_POWDER_DESCRIPTION,
			CAYENNE_DESCRIPTION,
			HOT_SAUCE_DESCRIPTION,
		),
		NutritionSeedAlias("thai fish sauce", FISH_SAUCE_DESCRIPTION),
		NutritionSeedAlias("thai green chiles", HOT_CHILI_GREEN_DESCRIPTION, HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("thai holy basil", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("thai red chiles", HOT_CHILI_RED_DESCRIPTION, CAYENNE_DESCRIPTION),
		NutritionSeedAlias("thai sweet basil", BASIL_DESCRIPTION, BASIL_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("thickly chestnut mushrooms", MUSHROOM_DESCRIPTION, CREMINI_DESCRIPTION),
		NutritionSeedAlias("thin ham", HAM_DESCRIPTION),
		NutritionSeedAlias("thin pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("three tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("thumb root ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("tinned coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("tinned pineapple half juice", PINEAPPLE_DESCRIPTION),
		NutritionSeedAlias(
			"tomatillos husks half",
			TOMATILLO_DESCRIPTION,
			TOMATILLO_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("tomato passata strained tomatoes one", TOMATO_CRUSHED_DESCRIPTION),
		NutritionSeedAlias("tomato paste one", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("toor daal", LENTIL_DESCRIPTION),
		NutritionSeedAlias("tub sour cream", CREAM_SOUR_DESCRIPTION, CREAM_SOUR_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("tube tomato paste", TOMATO_PASTE_DESCRIPTION),
		NutritionSeedAlias("tuscan kale bunches", KALE_DESCRIPTION),
		NutritionSeedAlias("uncooked rice vermicelli noodles", RICE_NOODLE_DESCRIPTION),
		NutritionSeedAlias("uncooked tri color rotini pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("uncooked white long grain rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter bread", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter dish", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter extra", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter frozen", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter loaf", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted butter three", BUTTER_DESCRIPTION, BUTTER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("unsalted white beans", CANNELLINI_DESCRIPTION),
		NutritionSeedAlias("unsifted confectioners sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("unsweetened full fat coconut milk", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("unsweetened full fat coconut milk before use", COCONUT_MILK_DESCRIPTION),
		NutritionSeedAlias("untoasted almonds half", ALMOND_DESCRIPTION),
		NutritionSeedAlias("untoasted pine nuts", PINE_NUT_DESCRIPTION),
		NutritionSeedAlias("untoasted walnuts", WALNUT_DESCRIPTION),
		NutritionSeedAlias("urfa chilli flake", CAYENNE_DESCRIPTION),
		NutritionSeedAlias("vanilla bean split", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla bean split scraped", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla bean split seeds scraped", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vanilla split seeds", VANILLA_DESCRIPTION),
		NutritionSeedAlias("vegan burger bun", BURGER_BUN_DESCRIPTION),
		NutritionSeedAlias(
			"vegan champagne vinegar",
			WHITE_VINEGAR_DESCRIPTION,
			CIDER_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("vegan dijon mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("vegan puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias(
			"vegan red wine vinegar",
			CIDER_VINEGAR_DESCRIPTION,
			WHITE_VINEGAR_DESCRIPTION,
			BALSAMIC_VINEGAR_DESCRIPTION,
		),
		NutritionSeedAlias("vegan yoghurt", COCONUT_YOGURT_DESCRIPTION, YOGURT_DESCRIPTION),
		NutritionSeedAlias("vegetable bouillon power", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("vegetable oil cast iron skillet", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias(
			"vegetable oil cooking spray",
			COOKING_SPRAY_DESCRIPTION,
			SOYBEAN_OIL_DESCRIPTION,
		),
		NutritionSeedAlias("vegetable oil grilling", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable oil shallow", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable oil shallow fry", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable oil skillet", SOYBEAN_OIL_DESCRIPTION),
		NutritionSeedAlias("vegetable quixo stock", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("very mint", MINT_DESCRIPTION),
		NutritionSeedAlias("very ripe avocado flesh", AVOCADO_DESCRIPTION),
		NutritionSeedAlias("very shallot shallot", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("very tiny pinch kosher salt", SALT_DESCRIPTION),
		NutritionSeedAlias("warm tortillas", FLOUR_TORTILLA_DESCRIPTION, CORN_TORTILLA_DESCRIPTION),
		NutritionSeedAlias("warm water 90of 32oc", WATER_DESCRIPTION, WATER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("warm water extra", WATER_DESCRIPTION, WATER_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("warm whole milk", MILK_DESCRIPTION),
		NutritionSeedAlias("wedge lime", LIME_DESCRIPTION),
		NutritionSeedAlias(
			"wheat vermicelli noodles",
			PASTA_DESCRIPTION,
			EGG_NOODLE_DESCRIPTION,
			RICE_NOODLE_DESCRIPTION,
		),
		NutritionSeedAlias("white onion topping", ONION_DESCRIPTION),
		NutritionSeedAlias(
			"white pepper substitute black pepper necessary",
			WHITE_PEPPER_DESCRIPTION,
			BLACK_PEPPER_DESCRIPTION,
		),
		NutritionSeedAlias("white rum", DESSERT_WINE_DRY_DESCRIPTION, DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias(
			"white whole wheat flour",
			FLOUR_DESCRIPTION,
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"whole boneless skinless chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"whole chicken breasts",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"whole chicken breasts bone skin split half",
			CHICKEN_BREAST_DESCRIPTION,
			CHICKEN_BREAST_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("whole chicken wings joints drumettes flats", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias(
			"whole green chillies slitted",
			HOT_CHILI_GREEN_DESCRIPTION,
			HOT_CHILI_RED_DESCRIPTION,
		),
		NutritionSeedAlias("whole heads garlic", GARLIC_DESCRIPTION),
		NutritionSeedAlias(
			"whole italian plum tomatoes",
			TOMATO_RAW_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION,
		),
		NutritionSeedAlias("whole milk brought boil", MILK_DESCRIPTION),
		NutritionSeedAlias("whole mung beans", BEANSPROUT_DESCRIPTION, LENTIL_DESCRIPTION),
		NutritionSeedAlias("whole peppercorns", BLACK_PEPPER_DESCRIPTION),
		NutritionSeedAlias("whole raw chicken", WHOLE_CHICKEN_DESCRIPTION),
		NutritionSeedAlias("whole red asian shallots", SHALLOT_DESCRIPTION),
		NutritionSeedAlias(
			"whole san marzano tomatoes",
			TOMATO_RAW_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION,
			TOMATO_CRUSHED_DESCRIPTION,
		),
		NutritionSeedAlias("whole serrano chiles", SERRANO_DESCRIPTION, HOT_CHILI_GREEN_DESCRIPTION),
		NutritionSeedAlias("whole star anise", ANISE_DESCRIPTION),
		NutritionSeedAlias(
			"whole tinned tomatoes puree",
			TOMATO_CRUSHED_DESCRIPTION,
			TOMATO_DICED_DESCRIPTION,
		),
		NutritionSeedAlias("whole tomatoes basil", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("whole wheat noodle", PASTA_DESCRIPTION, EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("whole white onion", ONION_DESCRIPTION),
		NutritionSeedAlias("wholewheat pasta", PASTA_DESCRIPTION),
		NutritionSeedAlias("wholewheat tagliatelle", PASTA_DESCRIPTION, EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("wide egg noodles cooked dente hot", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("worcestershire powder", WORCESTERSHIRE_DESCRIPTION),
		NutritionSeedAlias("yellow mustard seeds", MUSTARD_SEED_DESCRIPTION),
		NutritionSeedAlias("yellow onion thirds", ONION_DESCRIPTION),
		NutritionSeedAlias("yellow sweet onion", ONION_DESCRIPTION),
		NutritionSeedAlias("yoghurt smooth", YOGURT_DESCRIPTION),
		NutritionSeedAlias("yolks eggs straight fridge", EGG_YOLK_DESCRIPTION),
		NutritionSeedAlias("young spinach", SPINACH_DESCRIPTION),
		NutritionSeedAlias("your favorite potato crisps", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potatoes bowl water", POTATO_DESCRIPTION),
		NutritionSeedAlias("yukon gold potatoes potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("zest juice lime", LIME_JUICE_DESCRIPTION, LIME_DESCRIPTION),
		NutritionSeedAlias("zest lime lime juice", LIME_JUICE_DESCRIPTION, LIME_DESCRIPTION),
		NutritionSeedAlias(
			"zhug yemenite hot sauce cilantro parsley",
			HOT_SAUCE_DESCRIPTION,
			CHILI_POWDER_DESCRIPTION,
		),
	)
}
