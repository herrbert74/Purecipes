package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALLSPICE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ALMOND_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.APPLE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ASPARAGUS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BACON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BAKING_SODA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BOUILLON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_BRISKET_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_SHORT_RIBS_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BEEF_STRIP_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROCCOLI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BROWN_SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BUCKWHEAT_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANDIED_FRUIT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CARDAMOM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CHEDDAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.COD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORIANDER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNED_BEEF_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNMEAL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORNSTARCH_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_FLOUR_YELLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.DILL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.EGG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.HAM_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.INSTANT_YEAST_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.ITALIAN_SEASONING_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.JELLY_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.LAMB_SHANK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MAYONNAISE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MILK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MOLASSES_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.MUSTARD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.NUTMEG_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.OAT_BRAN_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PASTA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PIGEON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.PORK_SAUSAGE_PIECE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RASPBERRY_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RED_WINE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.RUTABAGA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SAFFRON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUNFLOWER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VENISON_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_FALLBACK_DESCRIPTION
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
	)
}
