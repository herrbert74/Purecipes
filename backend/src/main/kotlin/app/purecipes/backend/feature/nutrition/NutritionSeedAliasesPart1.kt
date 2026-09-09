package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.AGAVE_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHOVY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHOVY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ARUGULA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ASPARAGUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.AVOCADO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_UNPREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAKING_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAKING_SODA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BALSAMIC_VINEGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BASIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEANSPROUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLACK_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLOOD_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUEBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUEBERRY_FROZEN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUE_CHEESE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BOK_CHOY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREADCRUMB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROCCOLI_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAKE_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANNELLINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAPERS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARDAMOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARROT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAULIFLOWER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CELERIAC_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNSTARCH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_COB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CRANBERRY_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DILL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DRY_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EDIBLE_PODDED_PEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGGPLANT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_WHITE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_YOLK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EVAPORATED_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FETA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FISH_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLATBREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_TORTILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GARLIC_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_GROUND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_FALLBACK_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.KETCHUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEEK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMONGRASS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_PEEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LENTIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIME_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MANGO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAPLE_SYRUP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MARGARINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MARINARA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MARMITE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MINT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MISO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSHROOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSSEL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.NUTMEG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OAT_MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OAT_MILK_SURVEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ONION_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OREGANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OYSTER_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OYSTER_SAUCE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PAPRIKA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARMESAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARSLEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARSLEY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUT_OIL_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PECAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINE_NUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PINTO_BEAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PITA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLANTAIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLANT_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POMEGRANATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUMPKIN_CANNED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.QUESO_FRESCO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMAINE_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROSEMARY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALAMI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SELF_RISING_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SERRANO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SESAME_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SESAME_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHALLOT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SINGLE_CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SKIM_MILK_DESCRIPTION
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
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOFU_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATILLO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATILLO_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_CRUSHED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_PASTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURKEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURKEY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TURMERIC_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGETABLE_BROTH_DESCRIPTION
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

internal object NutritionSeedAliasesPart1 {

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
	)
}
