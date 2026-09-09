package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class SupplementalMeasure(
	val measureName: String,
	val gramsPerMeasure: BigDecimal,
)

internal object NutritionSupplementalMeasures {

	private const val BROCCOLI_FDC_ID = 170379L
	private const val CABBAGE_FDC_ID = 169975L
	private const val CAULIFLOWER_FDC_ID = 169986L
	private const val CELERY_FDC_ID = 169988L
	private const val CORN_FDC_ID = 169998L
	private const val FLOUR_FDC_ID = 789951L
	private const val GARLIC_FDC_ID = 1104647L
	private const val RED_CABBAGE_FDC_ID = 169977L
	private const val SHALLOT_FDC_ID = 170499L
	private const val YELLOW_ONION_FDC_ID = 790646L
	private const val YELLOW_PEPPER_FDC_ID = 169383L

	private const val BACON_COOKED_DESCRIPTION = "Pork, cured, bacon, cooked, baked"
	private const val BACON_UNPREPARED_DESCRIPTION = "Pork, cured, bacon, unprepared"
	private const val BANANA_DESCRIPTION = "Bananas, raw"
	private const val BASIL_FRESH_DESCRIPTION = "Basil, fresh"
	private const val BASIL_RAW_DESCRIPTION = "Basil, raw"
	private const val BAY_LEAF_DESCRIPTION = "Spices, bay leaf"
	private const val BEET_DESCRIPTION = "Beets, raw"
	private const val BROCCOLI_DESCRIPTION = "Broccoli, raw"
	private const val BUTTERNUT_SQUASH_DESCRIPTION = "Squash, winter, butternut, raw"
	private const val CABBAGE_DESCRIPTION = "Cabbage, raw"
	private const val CAULIFLOWER_DESCRIPTION = "Cauliflower, raw"
	private const val CELERIAC_DESCRIPTION = "Celeriac, raw"
	private const val CELERY_DESCRIPTION = "Celery, raw"
	private const val CHEDDAR_DESCRIPTION = "Cheese, cheddar"
	private const val CHICKEN_THIGH_DESCRIPTION = "Chicken, thigh, boneless, skinless, raw"
	private const val CHICKEN_THIGH_FALLBACK_DESCRIPTION =
		"Chicken, broilers or fryers, dark meat, thigh, meat only, raw"
	private const val CHIVES_DESCRIPTION = "Chives, raw"
	private const val CHORIZO_DESCRIPTION = "Sausage, pork, chorizo, link or ground, raw"
	private const val CILANTRO_DESCRIPTION = "Coriander (cilantro) leaves, raw"
	private const val CINNAMON_DESCRIPTION = "Spices, cinnamon, ground"
	private const val CORN_DESCRIPTION = "Corn, sweet, yellow, raw"
	private const val CORN_TORTILLA_DESCRIPTION = "Tortillas, ready-to-bake or -fry, corn"
	private const val DILL_FRESH_DESCRIPTION = "Dill weed, fresh"
	private const val ENDIVE_DESCRIPTION = "Endive, raw"
	private const val FENNEL_BULB_DESCRIPTION = "Fennel, bulb, raw"
	private const val FENNEL_BULB_SURVEY_DESCRIPTION = "Fennel bulb, raw"
	private const val FLOUR_TORTILLA_DESCRIPTION =
		"Tortillas, ready-to-bake or -fry, flour, refrigerated"
	private const val GARLIC_DESCRIPTION = "Garlic, raw"
	private const val GELATIN_DESCRIPTION = "Gelatins, dry powder, unsweetened"
	private const val GINGER_FRESH_DESCRIPTION = "Ginger root, raw"
	private const val GRAHAM_DESCRIPTION =
		"Cookies, graham crackers, plain or honey (includes cinnamon)"
	private const val HOT_CHILI_GREEN_DESCRIPTION = "Peppers, hot chili, green, raw"
	private const val HOT_CHILI_RED_DESCRIPTION = "Peppers, hot chili, red, raw"
	private const val HOT_DOG_DESCRIPTION = "Frankfurter, beef, unheated"
	private const val ICEBERG_LETTUCE_DESCRIPTION =
		"Lettuce, iceberg (includes crisphead types), raw"
	private const val ITALIAN_SAUSAGE_DESCRIPTION = "Sausage, Italian, pork, mild, raw"
	private const val JALAPENO_DESCRIPTION = "Peppers, jalapeno, raw"
	private const val KALE_DESCRIPTION = "Kale, raw"
	private const val LEMONGRASS_DESCRIPTION = "Lemon grass (citronella), raw"
	private const val MINT_DESCRIPTION = "Spearmint, fresh"
	private const val MUSHROOM_DESCRIPTION = "Mushrooms, white, raw"
	private const val PARSLEY_DESCRIPTION = "Parsley, fresh"
	private const val PARSLEY_FALLBACK_DESCRIPTION = "Parsley, raw"
	private const val PARSNIP_DESCRIPTION = "Parsnips, raw"
	private const val PEACH_DESCRIPTION = "Peaches, raw"
	private const val PEAR_DESCRIPTION = "Pears, raw"
	private const val PINEAPPLE_DESCRIPTION = "Pineapple, raw, all varieties"
	private const val PLANTAIN_DESCRIPTION = "Plantains, ripe, raw"
	private const val PLANTAIN_GREEN_DESCRIPTION = "Plantains, green, raw"
	private const val PLANTAIN_SURVEY_DESCRIPTION = "Plantain, raw"
	private const val PLANTAIN_YELLOW_DESCRIPTION = "Plantains, yellow, raw"
	private const val PORK_SAUSAGE_DESCRIPTION = "Pork sausage, link/patty, unprepared"
	private const val RADICCHIO_DESCRIPTION = "Radicchio, raw"
	private const val RADISH_DESCRIPTION = "Radishes, raw"
	private const val RED_CABBAGE_DESCRIPTION = "Cabbage, red, raw"
	private const val ROMAINE_LETTUCE_DESCRIPTION = "Lettuce, cos or romaine, raw"
	private const val ROSEMARY_FRESH_DESCRIPTION = "Rosemary, fresh"
	private const val SALMON_DESCRIPTION = "Fish, salmon, Atlantic, farmed, raw"
	private const val SERRANO_DESCRIPTION = "Peppers, serrano, raw"
	private const val SHALLOT_DESCRIPTION = "Shallots, raw"
	private const val SPINACH_DESCRIPTION = "Spinach, raw"
	private const val STRAWBERRY_DESCRIPTION = "Strawberries, raw"
	private const val THYME_FRESH_DESCRIPTION = "Thyme, fresh"
	private const val TOMATILLO_DESCRIPTION = "Tomatillos, raw"
	private const val TOMATILLO_FALLBACK_DESCRIPTION = "Tomatillos, dehusked, raw"
	private const val VEGGIE_BURGER_DESCRIPTION = "Veggie burgers or soyburgers, unprepared"
	private const val WATER_CHESTNUT_DESCRIPTION =
		"Waterchestnuts, chinese, canned, solids and liquids"
	private const val WATER_CHESTNUT_RAW_DESCRIPTION = "Waterchestnuts, chinese, (matai), raw"
	private const val YELLOW_PEPPER_DESCRIPTION = "Peppers, sweet, yellow, raw"

	private val pieceMeasuresByDisplayName: Map<String, List<SupplementalMeasure>> = mapOf(
		BACON_COOKED_DESCRIPTION to pieceGrams("8"),
		BACON_UNPREPARED_DESCRIPTION to pieceGrams("28"),
		BANANA_DESCRIPTION to pieceGrams("118"),
		BASIL_FRESH_DESCRIPTION to pieceGrams("2"),
		BASIL_RAW_DESCRIPTION to pieceGrams("2"),
		BAY_LEAF_DESCRIPTION to pieceGrams("0.2"),
		BEET_DESCRIPTION to pieceGrams("82"),
		BROCCOLI_DESCRIPTION to pieceGrams("151"),
		BUTTERNUT_SQUASH_DESCRIPTION to pieceGrams("1000"),
		CABBAGE_DESCRIPTION to pieceGrams("908"),
		CAULIFLOWER_DESCRIPTION to pieceGrams("588"),
		CELERIAC_DESCRIPTION to pieceGrams("600"),
		CELERY_DESCRIPTION to pieceGrams("40"),
		CHEDDAR_DESCRIPTION to listOf(
			SupplementalMeasure(measureName = "tbsp", gramsPerMeasure = BigDecimal("7")),
			SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal("28")),
		),
		CHICKEN_THIGH_DESCRIPTION to pieceGrams("116"),
		CHICKEN_THIGH_FALLBACK_DESCRIPTION to pieceGrams("116"),
		CHIVES_DESCRIPTION to pieceGrams("1"),
		CHORIZO_DESCRIPTION to pieceGrams("70"),
		CILANTRO_DESCRIPTION to pieceGrams("2"),
		CINNAMON_DESCRIPTION to listOf(
			SupplementalMeasure(measureName = "tsp", gramsPerMeasure = BigDecimal("2.6")),
		),
		CORN_DESCRIPTION to pieceGrams("102"),
		CORN_TORTILLA_DESCRIPTION to pieceGrams("26"),
		DILL_FRESH_DESCRIPTION to pieceGrams("1"),
		ENDIVE_DESCRIPTION to pieceGrams("85"),
		FENNEL_BULB_DESCRIPTION to pieceGrams("234"),
		FENNEL_BULB_SURVEY_DESCRIPTION to pieceGrams("234"),
		FLOUR_TORTILLA_DESCRIPTION to pieceGrams("43"),
		GARLIC_DESCRIPTION to listOf(
			SupplementalMeasure(measureName = "clove", gramsPerMeasure = BigDecimal("3")),
			SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal("3")),
		),
		GELATIN_DESCRIPTION to pieceGrams("7"),
		GINGER_FRESH_DESCRIPTION to pieceGrams("15"),
		GRAHAM_DESCRIPTION to pieceGrams("14"),
		HOT_CHILI_GREEN_DESCRIPTION to pieceGrams("45"),
		HOT_CHILI_RED_DESCRIPTION to pieceGrams("45"),
		HOT_DOG_DESCRIPTION to pieceGrams("45"),
		ICEBERG_LETTUCE_DESCRIPTION to pieceGrams("539"),
		ITALIAN_SAUSAGE_DESCRIPTION to pieceGrams("80"),
		JALAPENO_DESCRIPTION to pieceGrams("14"),
		KALE_DESCRIPTION to pieceGrams("10"),
		LEMONGRASS_DESCRIPTION to pieceGrams("25"),
		MINT_DESCRIPTION to pieceGrams("1"),
		MUSHROOM_DESCRIPTION to pieceGrams("18"),
		PARSLEY_DESCRIPTION to pieceGrams("2"),
		PARSLEY_FALLBACK_DESCRIPTION to pieceGrams("2"),
		PARSNIP_DESCRIPTION to pieceGrams("133"),
		PEACH_DESCRIPTION to pieceGrams("150"),
		PEAR_DESCRIPTION to pieceGrams("178"),
		PINEAPPLE_DESCRIPTION to pieceGrams("905"),
		PLANTAIN_DESCRIPTION to pieceGrams("179"),
		PLANTAIN_GREEN_DESCRIPTION to pieceGrams("140"),
		PLANTAIN_SURVEY_DESCRIPTION to pieceGrams("179"),
		PLANTAIN_YELLOW_DESCRIPTION to pieceGrams("179"),
		PORK_SAUSAGE_DESCRIPTION to pieceGrams("56"),
		RADICCHIO_DESCRIPTION to pieceGrams("100"),
		RADISH_DESCRIPTION to pieceGrams("5"),
		RED_CABBAGE_DESCRIPTION to pieceGrams("839"),
		ROMAINE_LETTUCE_DESCRIPTION to pieceGrams("150"),
		ROSEMARY_FRESH_DESCRIPTION to pieceGrams("1"),
		SALMON_DESCRIPTION to pieceGrams("170"),
		SERRANO_DESCRIPTION to pieceGrams("6"),
		SHALLOT_DESCRIPTION to pieceGrams("30"),
		SPINACH_DESCRIPTION to pieceGrams("142"),
		STRAWBERRY_DESCRIPTION to pieceGrams("12"),
		THYME_FRESH_DESCRIPTION to pieceGrams("1"),
		TOMATILLO_DESCRIPTION to pieceGrams("34"),
		TOMATILLO_FALLBACK_DESCRIPTION to pieceGrams("34"),
		VEGGIE_BURGER_DESCRIPTION to pieceGrams("70"),
		WATER_CHESTNUT_DESCRIPTION to pieceGrams("12"),
		WATER_CHESTNUT_RAW_DESCRIPTION to pieceGrams("12"),
		YELLOW_PEPPER_DESCRIPTION to pieceGrams("186"),
	)

	val measuresByFdcId: Map<Long, List<SupplementalMeasure>> = mapOf(
		FLOUR_FDC_ID to listOf(
			SupplementalMeasure(measureName = "cup", gramsPerMeasure = BigDecimal("120")),
			SupplementalMeasure(measureName = "tbsp", gramsPerMeasure = BigDecimal("7.5")),
		),
		YELLOW_ONION_FDC_ID to listOf(
			SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal("110")),
		),
		GARLIC_FDC_ID to listOf(
			SupplementalMeasure(measureName = "clove", gramsPerMeasure = BigDecimal("3")),
		),
		BROCCOLI_FDC_ID to pieceGrams("151"),
		CABBAGE_FDC_ID to pieceGrams("908"),
		CAULIFLOWER_FDC_ID to pieceGrams("588"),
		CELERY_FDC_ID to pieceGrams("40"),
		CORN_FDC_ID to pieceGrams("102"),
		RED_CABBAGE_FDC_ID to pieceGrams("839"),
		SHALLOT_FDC_ID to pieceGrams("30"),
		YELLOW_PEPPER_FDC_ID to pieceGrams("186"),
	)

	fun overlayMeasures(
		foods: Collection<NutritionFoodRecord>,
		storedMeasures: Map<Int, Map<String, BigDecimal>>,
	): Map<Int, Map<String, BigDecimal>> {
		val result = storedMeasures.mapValues { entry -> entry.value.toMutableMap() }.toMutableMap()
		foods.forEach { food ->
			pieceMeasuresByDisplayName[food.displayName].orEmpty().forEach { supplemental ->
				val measures = result.getOrPut(food.id) { mutableMapOf() }
				if (supplemental.measureName !in measures) {
					measures[supplemental.measureName] = supplemental.gramsPerMeasure
				}
			}
		}
		return result
	}

	private fun pieceGrams(grams: String): List<SupplementalMeasure> =
		listOf(SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal(grams)))
}
