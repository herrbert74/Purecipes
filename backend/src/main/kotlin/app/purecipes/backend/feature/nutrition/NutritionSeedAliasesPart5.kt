package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALLSPICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_PASTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANCHOVY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ANISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APRICOT_DRIED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ARTICHOKE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ARUGULA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ASPARAGUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGEL_CHIPS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAGUETTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAKING_SODA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BANANA_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEANSPROUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BOUILLON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BRISKET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_CHUCK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_SHORT_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_SKIRT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_STRIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_TALLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BELL_PEPPER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BISCOTTI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BLUE_CHEESE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREADCRUMB_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BRIE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROCCOLI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUCKWHEAT_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUTTERNUT_SQUASH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANDIED_FRUIT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARAMEL_TOPPING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARDAMOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAVIAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHAMOMILE_TEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHAMOMILE_TEA_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHEDDAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKEN_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICKPEA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHICORY_WITLOOF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHILI_POWDER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHIVES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CINNAMON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CLEMENTINES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COCOA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COFFEE_BREWED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COOKED_RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORIANDER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNED_BEEF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNMEAL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNSTARCH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_FLOUR_YELLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COUSCOUS_DRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CREAM_SOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CURRY_PASTE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DARK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DATES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_DRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DESSERT_WINE_SWEET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DILL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_NOODLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EUROPEAN_CHESTNUTS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FIGS_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GAME_MEAT_RABBIT_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GELATIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGERSNAP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_ALE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GINGER_FRESH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.GREEN_PEAS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HARD_CANDY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HASH_BROWNS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HAZELNUTS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HORSERADISH_PREPARED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_CHILI_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HOT_SAUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ICE_CREAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_YEAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_SEASONING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JACK_MACKEREL_CANNED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LADYFINGER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_CHOP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_SHANK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LEMON_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LETTUCE_BUTTERHEAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LETTUCE_GREEN_LEAF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LIQUEUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MERINGUE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOLASSES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOZZARELLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSHROOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.NON_ALCOHOLIC_SPIRIT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.NUTMEG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OATS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OAT_BRAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OLIVE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ORANGE_JUICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PARMESAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PEANUTS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PICKLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PIE_CRUST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PIGEON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PISTACHIO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PITA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLUMS_DRIED_PRUNES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PLUM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_MINCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_PIECE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POUND_CAKE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PROSCIUTTO_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PROVOLONE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PUFF_PASTRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RAISIN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RASPBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_CRISPY_CEREAL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RICE_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROASTED_SALTED_PEANUTS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ROMAINE_LETTUCE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RUTABAGA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SAFFRON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALSA_RED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALTINE_CRACKER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SEAWEED_DRIED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHALLOT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_COOKED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SHRIMP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SPINACH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SQUID_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SRIRACHA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUNFLOWER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TANGERINES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TANGERINE_JUICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TEQUILA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_DICED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TROUT_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TUNA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VEGETABLE_BROTH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VENISON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WALNUT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHISKEY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_CHOCOLATE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHOLE_WHEAT_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WORCESTERSHIRE_DESCRIPTION

internal object NutritionSeedAliasesPart5 {

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias("another flour of your choosing", FLOUR_DESCRIPTION),
		NutritionSeedAlias("artisan bread cristal ciabatta", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias("artisan style bread pan de cristal ciabatta", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias(
			"basil water",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("black sesame paste", TAHINI_DESCRIPTION),
		NutritionSeedAlias(
			"bread flour added vital wheat gluten",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias(
			"bread flour with added vital wheat gluten",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias(
			"bread spice",
			CORIANDER_SEED_DESCRIPTION,
			FENNEL_SEED_DESCRIPTION,
		),
		NutritionSeedAlias(
			"brotgewurz bread spice",
			CORIANDER_SEED_DESCRIPTION,
			FENNEL_SEED_DESCRIPTION,
		),
		NutritionSeedAlias("cayenne powder", CAYENNE_DESCRIPTION),
		NutritionSeedAlias(
			"chamomile tea",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"chestnut flour",
			FLOUR_DESCRIPTION,
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chopped fruit", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"cinnamon roll sourdough bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"diastatic barley malt powder",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("dill and salt and pepper to taste", DILL_DESCRIPTION),
		NutritionSeedAlias("dill salt pepper", DILL_DESCRIPTION),
		NutritionSeedAlias("egg wash egg", EGG_DESCRIPTION),
		NutritionSeedAlias("fine meal", CORNMEAL_DESCRIPTION),
		NutritionSeedAlias("flake salt", SALT_DESCRIPTION),
		NutritionSeedAlias("food grade lye", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("food grade lye use scale density of lye varies", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("fresh milled buckwheat groats flour", BUCKWHEAT_FLOUR_DESCRIPTION),
		NutritionSeedAlias(
			"fresh milled organic yellow dent corn",
			CORN_FLOUR_YELLOW_DESCRIPTION,
			CORNMEAL_DESCRIPTION,
		),
		NutritionSeedAlias(
			"fresh milled pima club wheat berries soft white whole wheat flour",
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("freshmilled buckwheat groats flour", BUCKWHEAT_FLOUR_DESCRIPTION),
		NutritionSeedAlias("fruit", APPLE_DESCRIPTION),
		NutritionSeedAlias(
			"gluten flour",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("ground cardamom cinnamon or ginger", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("ground mahlep", NUTMEG_DESCRIPTION),
		NutritionSeedAlias("lemon extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("lye", BAKING_SODA_DESCRIPTION),
		NutritionSeedAlias("light oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("mahlep", NUTMEG_DESCRIPTION),
		NutritionSeedAlias("maldon", SALT_DESCRIPTION),
		NutritionSeedAlias(
			"other bread",
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("other field corn", CORNMEAL_DESCRIPTION),
		NutritionSeedAlias("other flake salt", SALT_DESCRIPTION),
		NutritionSeedAlias("panela sugar", BROWN_SUGAR_DESCRIPTION),
		NutritionSeedAlias("pearl sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("potato flakes", POTATO_FLOUR_DESCRIPTION),
		NutritionSeedAlias(
			"psyllium husk powder",
			FLOUR_DESCRIPTION,
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"psyllium powder",
			FLOUR_DESCRIPTION,
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("raspberry juice frozen raspberries thru mesh strainer", RASPBERRY_DESCRIPTION),
		NutritionSeedAlias(
			"rose water",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("sliced fruit of your choosing", APPLE_DESCRIPTION),
		NutritionSeedAlias("sunflower seeds flax seeds", SUNFLOWER_SEED_DESCRIPTION),
		NutritionSeedAlias("sweet tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("tapioca starch", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias(
			"the raspberry juice from about 1/2 cup frozen raspberries melted and pressed thru a mesh strainer",
			RASPBERRY_DESCRIPTION,
		),
		NutritionSeedAlias("toasted artisan style bread pan de cristal ciabatta", ITALIAN_BREAD_DESCRIPTION),
		NutritionSeedAlias(
			"unchlorinated water with minerals",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"water milk",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"water milk or beaten egg for brushing on the dough just before baking",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias(
			"water with a few drops of lemon juice",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("yellow dent corn flour", CORN_FLOUR_YELLOW_DESCRIPTION),
		NutritionSeedAlias("candied cherries", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("coarse grain mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("crystallised ginger", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("flaked almonds", ALMOND_DESCRIPTION),
		NutritionSeedAlias("flaked sea salt", SALT_DESCRIPTION),
		NutritionSeedAlias("glace cherries", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("lardons", BACON_DESCRIPTION),
		NutritionSeedAlias("long broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("long stem broccoli", BROCCOLI_DESCRIPTION),
		NutritionSeedAlias("low fat mayonnaise", MAYONNAISE_DESCRIPTION),
		NutritionSeedAlias("macaroni", PASTA_DESCRIPTION),
		NutritionSeedAlias("mature cheddar", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("mixed herbs", ITALIAN_SEASONING_DESCRIPTION),
		NutritionSeedAlias("oatbran", OAT_BRAN_DESCRIPTION),
		NutritionSeedAlias("quick yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("seedless raspberry jam", JELLY_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("semi skimmed milk", MILK_DESCRIPTION),
		NutritionSeedAlias("strong wholemeal flour", WHOLE_WHEAT_FLOUR_DESCRIPTION),
		NutritionSeedAlias("tinned corned beef", CORNED_BEEF_DESCRIPTION),
		NutritionSeedAlias("treacle", MOLASSES_DESCRIPTION),
		NutritionSeedAlias("wine", RED_WINE_DESCRIPTION),
		NutritionSeedAlias("worcester sauce", WORCESTERSHIRE_DESCRIPTION),
		NutritionSeedAlias("bovril", BEEF_BOUILLON_DESCRIPTION),
		NutritionSeedAlias("brisket", BEEF_BRISKET_DESCRIPTION),
		NutritionSeedAlias("cod cheeks", COD_DESCRIPTION),
		NutritionSeedAlias("flanken ribs", BEEF_SHORT_RIBS_DESCRIPTION),
		NutritionSeedAlias("gammon", HAM_DESCRIPTION),
		NutritionSeedAlias("jacob ladder beef ribs", BEEF_SHORT_RIBS_DESCRIPTION),
		NutritionSeedAlias("mixed spice", ALLSPICE_DESCRIPTION),
		NutritionSeedAlias("redcurrant jelly", JELLY_DESCRIPTION),
		NutritionSeedAlias("samphire", ASPARAGUS_DESCRIPTION),
		NutritionSeedAlias("spider", BEEF_STRIP_DESCRIPTION),
		NutritionSeedAlias("spider steak", BEEF_STRIP_DESCRIPTION),
		NutritionSeedAlias("strands saffron", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("saffron", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("sugar active yeast", SUGAR_DESCRIPTION),
		NutritionSeedAlias("sugar if using active dried yeast", SUGAR_DESCRIPTION),
		NutritionSeedAlias("swede", RUTABAGA_DESCRIPTION),
		NutritionSeedAlias("venison haunch", VENISON_DESCRIPTION),
		NutritionSeedAlias("venison", VENISON_DESCRIPTION),
		NutritionSeedAlias("juniper berries", ALLSPICE_DESCRIPTION),
		NutritionSeedAlias("lamb shanks", LAMB_SHANK_DESCRIPTION),
		NutritionSeedAlias("pigeons", PIGEON_DESCRIPTION),
		NutritionSeedAlias("pork chipolatas", PORK_SAUSAGE_PIECE_DESCRIPTION),
		NutritionSeedAlias("acini di pepe", PASTA_DESCRIPTION),
		NutritionSeedAlias("acini pepe", PASTA_DESCRIPTION),
		NutritionSeedAlias("any pasta shape", PASTA_DESCRIPTION),
		NutritionSeedAlias("any small pasta shape", PASTA_DESCRIPTION),
		NutritionSeedAlias("baking potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("baking potato", POTATO_DESCRIPTION),
		NutritionSeedAlias("bavette", BEEF_SKIRT_DESCRIPTION),
		NutritionSeedAlias("bavette steak", BEEF_SKIRT_DESCRIPTION),
		NutritionSeedAlias("beansprout", BEANSPROUT_DESCRIPTION),
		NutritionSeedAlias("black cardamom", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias("braising beef", BEEF_CHUCK_DESCRIPTION),
		NutritionSeedAlias("brown or black cardamom", CARDAMOM_DESCRIPTION),
		NutritionSeedAlias(
			"camomile tea",
			CHAMOMILE_TEA_DESCRIPTION,
			CHAMOMILE_TEA_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cassia bark", CINNAMON_DESCRIPTION),
		NutritionSeedAlias("caster sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias(
			"chamomile tea",
			CHAMOMILE_TEA_DESCRIPTION,
			CHAMOMILE_TEA_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("chestnut mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("chestnut mushroom", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("chex cereal", RICE_CRISPY_CEREAL_DESCRIPTION),
		NutritionSeedAlias("chicken tikka masala paste", CURRY_PASTE_DESCRIPTION),
		NutritionSeedAlias("chipolata", PORK_SAUSAGE_PIECE_DESCRIPTION),
		NutritionSeedAlias("chipolatas", PORK_SAUSAGE_PIECE_DESCRIPTION),
		NutritionSeedAlias(
			"clementine juice",
			TANGERINE_JUICE_DESCRIPTION,
			ORANGE_JUICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("cooked and peeled tiger prawns tails left on", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("cooked carlin peas", CHICKPEA_DESCRIPTION),
		NutritionSeedAlias("cooked sushi rice", COOKED_RICE_DESCRIPTION),
		NutritionSeedAlias("corn chex cereal", RICE_CRISPY_CEREAL_DESCRIPTION),
		NutritionSeedAlias(
			"crostini",
			BAGUETTE_DESCRIPTION,
			WHITE_BREAD_DESCRIPTION,
			WHITE_BREAD_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("crustless white bread", WHITE_BREAD_DESCRIPTION),
		NutritionSeedAlias("deghi mirch powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("dried fast action yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("dried ziti", PASTA_DESCRIPTION),
		NutritionSeedAlias(
			"dry white vermouth",
			WHITE_WINE_DESCRIPTION,
			DESSERT_WINE_DRY_DESCRIPTION,
		),
		NutritionSeedAlias("extra mature cheddar", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("fast action dried yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("fast action yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("fast-action dried yeast", INSTANT_YEAST_DESCRIPTION),
		NutritionSeedAlias("fat lamb mince", LAMB_MINCE_DESCRIPTION),
		NutritionSeedAlias(
			"freshly squeezed clementine juice",
			TANGERINE_JUICE_DESCRIPTION,
			ORANGE_JUICE_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("frozen tater tots", HASH_BROWNS_DESCRIPTION),
		NutritionSeedAlias("full fat milk", MILK_DESCRIPTION),
		NutritionSeedAlias("full-fat milk", MILK_DESCRIPTION),
		NutritionSeedAlias("garlic bagel chips", BAGEL_CHIPS_DESCRIPTION),
		NutritionSeedAlias("ginger cordial", GINGER_ALE_DESCRIPTION),
		NutritionSeedAlias("goat venison lamb", LAMB_CHOP_DESCRIPTION),
		NutritionSeedAlias("golden caster sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("golden granulated sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("grating of nutmeg", NUTMEG_DESCRIPTION),
		NutritionSeedAlias("hashbrown bites", HASH_BROWNS_DESCRIPTION),
		NutritionSeedAlias("hot chilli powder", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("irish whiskey", WHISKEY_DESCRIPTION),
		NutritionSeedAlias("kashmiri chilli", CHILI_POWDER_DESCRIPTION),
		NutritionSeedAlias("kewra", WATER_DESCRIPTION),
		NutritionSeedAlias("kewra water", WATER_DESCRIPTION),
		NutritionSeedAlias("light chicken stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("light muscovado sugar", BROWN_SUGAR_DESCRIPTION),
		NutritionSeedAlias("madeira cake", POUND_CAKE_DESCRIPTION),
		NutritionSeedAlias("meringues", MERINGUE_DESCRIPTION),
		NutritionSeedAlias("meringues broken", MERINGUE_DESCRIPTION),
		NutritionSeedAlias("mixed white yellow cheddar", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("mushroom stock", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("mushroom stock cube", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("muscovado sugar", BROWN_SUGAR_DESCRIPTION),
		NutritionSeedAlias("neutral tasting oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("neutral-tasting oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("non alcoholic gin", NON_ALCOHOLIC_SPIRIT_DESCRIPTION),
		NutritionSeedAlias("non alcoholic gin alternative", NON_ALCOHOLIC_SPIRIT_DESCRIPTION),
		NutritionSeedAlias("non-alcoholic gin", NON_ALCOHOLIC_SPIRIT_DESCRIPTION),
		NutritionSeedAlias("non-alcoholic gin alternative", NON_ALCOHOLIC_SPIRIT_DESCRIPTION),
		NutritionSeedAlias("nori", SEAWEED_DRIED_DESCRIPTION),
		NutritionSeedAlias("other salted crackers", SALTINE_CRACKER_DESCRIPTION),
		NutritionSeedAlias("peach schnapps", LIQUEUR_DESCRIPTION),
		NutritionSeedAlias("plums", PLUM_DESCRIPTION),
		NutritionSeedAlias("polenta", CORNMEAL_DESCRIPTION),
		NutritionSeedAlias("raspberry liqueur", LIQUEUR_DESCRIPTION),
		NutritionSeedAlias("ready-rolled puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("rice chex cereal", RICE_CRISPY_CEREAL_DESCRIPTION),
		NutritionSeedAlias("ro-tel diced tomatoes with green chillies", TOMATO_DICED_DESCRIPTION),
		NutritionSeedAlias("saltine", SALTINE_CRACKER_DESCRIPTION),
		NutritionSeedAlias("saltine crackers", SALTINE_CRACKER_DESCRIPTION),
		NutritionSeedAlias("salted crackers", SALTINE_CRACKER_DESCRIPTION),
		NutritionSeedAlias("sheets ready-rolled puff pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("shelled pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias(
			"soured cream",
			CREAM_SOUR_FALLBACK_DESCRIPTION,
			CREAM_SOUR_DESCRIPTION,
		),
		NutritionSeedAlias("sponge fingers", LADYFINGER_DESCRIPTION),
		NutritionSeedAlias("sriracha chilli sauce", SRIRACHA_DESCRIPTION),
		NutritionSeedAlias("strong coffee", COFFEE_BREWED_DESCRIPTION),
		NutritionSeedAlias("sushi rice", COOKED_RICE_DESCRIPTION),
		NutritionSeedAlias("tel tomatoes green chillies", TOMATO_DICED_DESCRIPTION),
		NutritionSeedAlias("tequila", TEQUILA_DESCRIPTION),
		NutritionSeedAlias("tequila reposado", TEQUILA_DESCRIPTION),
		NutritionSeedAlias("tiger prawn", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("tiger prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("tomato salsa", SALSA_RED_DESCRIPTION),
		NutritionSeedAlias("vegetarian alternative", PARMESAN_DESCRIPTION),
		NutritionSeedAlias("vegetarian mincemeat", RAISIN_DESCRIPTION),
		NutritionSeedAlias("white bread crumbs", WHITE_BREAD_DESCRIPTION),
		NutritionSeedAlias("ziti", PASTA_DESCRIPTION),
		NutritionSeedAlias("anchovies in olive oil", ANCHOVY_DESCRIPTION),
		NutritionSeedAlias("beef tomato", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("beef tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("beetroot", BEET_DESCRIPTION),
		NutritionSeedAlias("blanched whole almonds", ALMOND_DESCRIPTION),
		NutritionSeedAlias("bramley apple", APPLE_DESCRIPTION),
		NutritionSeedAlias("bramley apples", APPLE_DESCRIPTION),
		NutritionSeedAlias("castelfranco lettuce", LETTUCE_GREEN_LEAF_DESCRIPTION),
		NutritionSeedAlias("chicory", CHICORY_WITLOOF_DESCRIPTION),
		NutritionSeedAlias("chipolata sausages", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("chipotle chilli paste", HOT_SAUCE_DESCRIPTION),
		NutritionSeedAlias("clementine", CLEMENTINES_DESCRIPTION),
		NutritionSeedAlias("clementines", CLEMENTINES_DESCRIPTION),
		NutritionSeedAlias("clotted cream", CREAM_DESCRIPTION),
		NutritionSeedAlias("cooked brown shrimp", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("cooked chestnuts", EUROPEAN_CHESTNUTS_DESCRIPTION),
		NutritionSeedAlias("cooked peeled prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("cos", ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("cumberland sausages", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("eating apple", APPLE_DESCRIPTION),
		NutritionSeedAlias("eating apples", APPLE_DESCRIPTION),
		NutritionSeedAlias("fig", FIGS_RAW_DESCRIPTION),
		NutritionSeedAlias("figs", FIGS_RAW_DESCRIPTION),
		NutritionSeedAlias("frisee lettuce", LETTUCE_GREEN_LEAF_DESCRIPTION),
		NutritionSeedAlias("gherkin", PICKLE_DESCRIPTION),
		NutritionSeedAlias("gherkins", PICKLE_DESCRIPTION),
		NutritionSeedAlias("gorgonzola", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("gorgonzola cheese", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("grainy mustard", MUSTARD_DESCRIPTION),
		NutritionSeedAlias("hot-smoked trout", TROUT_RAW_DESCRIPTION),
		NutritionSeedAlias("jarred horseradish", HORSERADISH_PREPARED_DESCRIPTION),
		NutritionSeedAlias("light ale", BEER_DESCRIPTION),
		NutritionSeedAlias("little gem lettuce", ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("little gem lettuces", ROMAINE_LETTUCE_DESCRIPTION),
		NutritionSeedAlias("long red chicory", CHICORY_WITLOOF_DESCRIPTION),
		NutritionSeedAlias("long white chicory", CHICORY_WITLOOF_DESCRIPTION),
		NutritionSeedAlias("mackerel in sunflower oil", JACK_MACKEREL_CANNED_DESCRIPTION),
		NutritionSeedAlias("mixed heritage tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("porridge oats", OATS_DESCRIPTION),
		NutritionSeedAlias("prune", PLUMS_DRIED_PRUNES_DESCRIPTION),
		NutritionSeedAlias("prunes", PLUMS_DRIED_PRUNES_DESCRIPTION),
		NutritionSeedAlias("rabbit", GAME_MEAT_RABBIT_RAW_DESCRIPTION),
		NutritionSeedAlias("raw beetroot", BEET_DESCRIPTION),
		NutritionSeedAlias("ripe figs", FIGS_RAW_DESCRIPTION),
		NutritionSeedAlias("ripe mixed heritage tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("round lettuce", LETTUCE_BUTTERHEAD_DESCRIPTION),
		NutritionSeedAlias("round red chicory", CHICORY_WITLOOF_DESCRIPTION),
		NutritionSeedAlias(
			"shelled roasted peanuts",
			ROASTED_SALTED_PEANUTS_DESCRIPTION,
			PEANUTS_DESCRIPTION,
		),
		NutritionSeedAlias("shelled walnuts", WALNUT_DESCRIPTION),
		NutritionSeedAlias("spring peas", GREEN_PEAS_DESCRIPTION),
		NutritionSeedAlias("tipo 00 flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("trout", TROUT_RAW_DESCRIPTION),
		NutritionSeedAlias("tuna in olive oil", TUNA_DESCRIPTION),
		NutritionSeedAlias("vin santo", DESSERT_WINE_SWEET_DESCRIPTION),
		NutritionSeedAlias("whole rabbit", GAME_MEAT_RABBIT_RAW_DESCRIPTION),
		NutritionSeedAlias("wholemeal flour", WHOLE_WHEAT_FLOUR_DESCRIPTION),
		NutritionSeedAlias("wholewheat couscous", COUSCOUS_DRY_DESCRIPTION),
		NutritionSeedAlias("wild rocket", ARUGULA_DESCRIPTION),
		NutritionSeedAlias("macarated raisins", RAISIN_DESCRIPTION),
		NutritionSeedAlias("macerated raisins", RAISIN_DESCRIPTION),
		NutritionSeedAlias("roasted pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("pistachios", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("pistachio", PISTACHIO_DESCRIPTION),
		NutritionSeedAlias("egg glaze", EGG_DESCRIPTION),
		NutritionSeedAlias("egg extra egg glaze", EGG_DESCRIPTION),
		NutritionSeedAlias("egg milk glazing", EGG_DESCRIPTION),
		NutritionSeedAlias("salt pinch eggwash", EGG_DESCRIPTION),
		NutritionSeedAlias("mincemeat", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("satsumas segmented", CLEMENTINES_DESCRIPTION),
		NutritionSeedAlias("satsuma", CLEMENTINES_DESCRIPTION),
		NutritionSeedAlias("satsumas", CLEMENTINES_DESCRIPTION),
		NutritionSeedAlias("mandarin segments", TANGERINES_DESCRIPTION),
		NutritionSeedAlias("eschalions", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("eschalion", SHALLOT_DESCRIPTION),
		NutritionSeedAlias("floury potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("waxy potatoes", POTATO_DESCRIPTION),
		NutritionSeedAlias("marzipan", ALMOND_PASTE_DESCRIPTION),
		NutritionSeedAlias("mixed peel", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("mixed candied peel", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("stoned dates", DATES_DESCRIPTION),
		NutritionSeedAlias("dates", DATES_DESCRIPTION),
		NutritionSeedAlias("strong white flour", BREAD_FLOUR_DESCRIPTION),
		NutritionSeedAlias("plain wholemeal flour", WHOLE_WHEAT_FLOUR_DESCRIPTION),
		NutritionSeedAlias("beef suet", BEEF_TALLOW_DESCRIPTION),
		NutritionSeedAlias("suet", BEEF_TALLOW_DESCRIPTION),
		NutritionSeedAlias("candied angelica", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("glac pineapple", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("glace pineapple", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("chicken bouillon powder kosher salt", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("unsalted chicken vegetable stock", CHICKEN_BROTH_DESCRIPTION),
		NutritionSeedAlias("cocoa nibs", COCOA_DESCRIPTION),
		NutritionSeedAlias("cooked chesnut mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("chesnut mushrooms", MUSHROOM_DESCRIPTION),
		NutritionSeedAlias("cooked ham hock", HAM_DESCRIPTION),
		NutritionSeedAlias("custard powder", CORNSTARCH_DESCRIPTION),
		NutritionSeedAlias("egg noodles oil", EGG_NOODLE_DESCRIPTION),
		NutritionSeedAlias("frozen artichoke butter", ARTICHOKE_DESCRIPTION),
		NutritionSeedAlias("frozen whole leaf spinach", SPINACH_DESCRIPTION),
		NutritionSeedAlias("ginger nut biscuits", GINGERSNAP_DESCRIPTION),
		NutritionSeedAlias("ginger syrup ginger", GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("kalamata black olives", OLIVE_DESCRIPTION),
		NutritionSeedAlias("kirsch", LIQUEUR_DESCRIPTION),
		NutritionSeedAlias("pernod", ANISE_DESCRIPTION),
		NutritionSeedAlias("langoustine tails", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("leaf gelatine", GELATIN_DESCRIPTION),
		NutritionSeedAlias("lemon curd", LEMON_JUICE_DESCRIPTION),
		NutritionSeedAlias("lime sherbet sweets", HARD_CANDY_DESCRIPTION),
		NutritionSeedAlias("caster sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("mince pork", PORK_MINCE_DESCRIPTION),
		NutritionSeedAlias("mozarella", MOZZARELLA_DESCRIPTION),
		NutritionSeedAlias("parma han", PROSCIUTTO_DESCRIPTION),
		NutritionSeedAlias("parma ham", PROSCIUTTO_DESCRIPTION),
		NutritionSeedAlias("pinch saffron strands", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("saffron strands", SAFFRON_DESCRIPTION),
		NutritionSeedAlias("plain chocolate", DARK_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("quantity creme patissiere", CUSTARD_DESCRIPTION),
		NutritionSeedAlias("creme patissiere", CUSTARD_DESCRIPTION),
		NutritionSeedAlias("quantity danish pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("danish pastry", PUFF_PASTRY_DESCRIPTION),
		NutritionSeedAlias("quantity sweet pastry", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("sweet pastry", PIE_CRUST_DESCRIPTION),
		NutritionSeedAlias("ready eat apricots", APRICOT_DRIED_DESCRIPTION),
		NutritionSeedAlias("rectangular flatbreads", PITA_DESCRIPTION),
		NutritionSeedAlias("salt white pepper", SALT_DESCRIPTION),
		NutritionSeedAlias("sausgemeat", PORK_SAUSAGE_DESCRIPTION),
		NutritionSeedAlias("seasoned flour", FLOUR_DESCRIPTION),
		NutritionSeedAlias("stilton", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("stilton cheese", BLUE_CHEESE_DESCRIPTION),
		NutritionSeedAlias("talaggio cheese", PROVOLONE_DESCRIPTION),
		NutritionSeedAlias("taleggio cheese", PROVOLONE_DESCRIPTION),
		NutritionSeedAlias("unsmoked back bacon", BACON_DESCRIPTION),
		NutritionSeedAlias("back bacon", BACON_DESCRIPTION),
		NutritionSeedAlias("vanilla ice cream", ICE_CREAM_DESCRIPTION),
		NutritionSeedAlias("vanilla seeds out", VANILLA_DESCRIPTION),
		NutritionSeedAlias("wensleydale cheese", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("wensleydale", CHEDDAR_DESCRIPTION),
		NutritionSeedAlias("whole somerset", BRIE_DESCRIPTION),
		NutritionSeedAlias("somerset brie", BRIE_DESCRIPTION),
		NutritionSeedAlias("chives salt", CHIVES_DESCRIPTION),
		NutritionSeedAlias("light sugar", BROWN_SUGAR_DESCRIPTION),
		NutritionSeedAlias("red yellow pepper", BELL_PEPPER_DESCRIPTION),
		NutritionSeedAlias("apricot jam glaze", JAM_DESCRIPTION),
		NutritionSeedAlias("shelled cobnuts", HAZELNUTS_DESCRIPTION),
		NutritionSeedAlias("cobnuts", HAZELNUTS_DESCRIPTION),
		NutritionSeedAlias("delicata squash", BUTTERNUT_SQUASH_DESCRIPTION),
		NutritionSeedAlias("fresno chillies", HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("fresno chile", HOT_CHILI_RED_DESCRIPTION),
		NutritionSeedAlias("pickled mild banana pepper", BANANA_PEPPER_DESCRIPTION),
		NutritionSeedAlias("squid bodies tentacles", SQUID_DESCRIPTION),
		NutritionSeedAlias("squid bodies tentacles whole", SQUID_DESCRIPTION),
		NutritionSeedAlias("white chocolate decorate", WHITE_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("white chocolate", WHITE_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("candied ginger", CANDIED_FRUIT_DESCRIPTION, GINGER_FRESH_DESCRIPTION),
		NutritionSeedAlias("fish roe", CAVIAR_DESCRIPTION),
		NutritionSeedAlias("beetroot juice", BEET_DESCRIPTION),
		NutritionSeedAlias("candied fruit peel", CANDIED_FRUIT_DESCRIPTION),
		NutritionSeedAlias("caramel sauce", CARAMEL_TOPPING_DESCRIPTION),
		NutritionSeedAlias("cooked prawns", SHRIMP_COOKED_DESCRIPTION, SHRIMP_DESCRIPTION),
		NutritionSeedAlias("raw prawns", SHRIMP_DESCRIPTION),
		NutritionSeedAlias("fruit jam", JAM_DESCRIPTION),
		NutritionSeedAlias("paella rice", RICE_DESCRIPTION, RICE_FALLBACK_DESCRIPTION),
		NutritionSeedAlias("toffee chocolate", MILK_CHOCOLATE_DESCRIPTION),
		NutritionSeedAlias("vegetable bouillon powder", VEGETABLE_BROTH_DESCRIPTION),
		NutritionSeedAlias("white breadcrumbs", BREADCRUMB_DESCRIPTION),
		NutritionSeedAlias("biscotti", BISCOTTI_DESCRIPTION),
	)
}
