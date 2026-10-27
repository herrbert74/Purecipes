package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.BREAD_FLOUR_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CANOLA_OIL_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CAYENNE_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORIANDER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.CORN_FLOUR_YELLOW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FENNEL_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.POTATO_FLOUR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SALT_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUGAR_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.SUNFLOWER_SEED_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TAHINI_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.TOMATO_RAW_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.VANILLA_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WATER_FALLBACK_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_DESCRIPTION
import app.purecipes.backend.feature.nutrition.NutritionSeedAliasDescriptions.WHITE_BREAD_FALLBACK_DESCRIPTION

internal object NutritionSeedAliasesPart5 {

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias("black sesame paste", TAHINI_DESCRIPTION),
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
		NutritionSeedAlias(
			"gluten flour",
			BREAD_FLOUR_DESCRIPTION,
			BREAD_FLOUR_FALLBACK_DESCRIPTION,
			FLOUR_DESCRIPTION,
		),
		NutritionSeedAlias("lemon extract", VANILLA_DESCRIPTION),
		NutritionSeedAlias("light oil", CANOLA_OIL_DESCRIPTION),
		NutritionSeedAlias("maldon", SALT_DESCRIPTION),
		NutritionSeedAlias("pearl sugar", SUGAR_DESCRIPTION),
		NutritionSeedAlias("potato flakes", POTATO_FLOUR_DESCRIPTION),
		NutritionSeedAlias(
			"rose water",
			WATER_DESCRIPTION,
			WATER_FALLBACK_DESCRIPTION,
		),
		NutritionSeedAlias("sunflower seeds flax seeds", SUNFLOWER_SEED_DESCRIPTION),
		NutritionSeedAlias("sweet tomatoes", TOMATO_RAW_DESCRIPTION),
		NutritionSeedAlias("yellow dent corn flour", CORN_FLOUR_YELLOW_DESCRIPTION),
	)
}
