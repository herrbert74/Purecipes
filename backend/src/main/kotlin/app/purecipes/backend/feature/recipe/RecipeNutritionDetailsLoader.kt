package app.purecipes.backend.feature.recipe

import app.purecipes.backend.feature.nutrition.ServingsParser
import app.purecipes.backend.feature.nutrition.scale
import app.purecipes.shared.domain.model.IngredientNutritionLine
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionConfidence
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeNutrition
import java.math.BigDecimal
import java.sql.Connection
import java.sql.ResultSet

internal object RecipeNutritionDetailsLoader {

	private const val RECIPE_NUTRITION_SQL = """
		SELECT
			n.calories,
			n.protein,
			n.carbohydrates,
			n.fat,
			n.fiber,
			n.sugar,
			n.sodium,
			n.matched_ingredient_count,
			n.total_ingredient_count,
			n.calculation_source,
			n.confidence,
			n.is_complete,
			n.total_weight_grams,
			n.serving_count,
			r.yields
		FROM nutrition n
		JOIN recipes r ON r.id = n.recipe_id
		WHERE n.recipe_id = ?
	"""

	private const val INGREDIENT_NUTRITION_SQL = """
		SELECT
			i.id AS ingredient_id,
			i.ingredient AS raw_text,
			im.parsed_name,
			im.quantity,
			im.unit,
			im.is_measurable,
			inm.food_id,
			nf.display_name AS food_display_name,
			inc.grams_resolved,
			inc.calories,
			inc.protein,
			inc.carbohydrates,
			inc.fat,
			inc.fiber,
			inc.sugar,
			inc.sodium,
			inc.override_calories,
			inc.override_protein,
			inc.override_carbohydrates,
			inc.override_fat,
			inc.override_fiber,
			inc.override_sugar,
			inc.override_sodium,
			inc.uses_user_override,
			nf.calories_per_100g AS food_calories_per_100,
			nf.protein_per_100g AS food_protein_per_100,
			nf.carbohydrates_per_100g AS food_carbs_per_100,
			nf.fat_per_100g AS food_fat_per_100,
			nf.fiber_per_100g AS food_fiber_per_100,
			nf.sugar_per_100g AS food_sugar_per_100,
			nf.sodium_per_100g AS food_sodium_per_100
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		LEFT JOIN ingredient_measurements im ON im.ingredient_id = i.id
		LEFT JOIN ingredient_nutrition_matches inm ON inm.ingredient_id = i.id
		LEFT JOIN ingredient_nutrition_contributions inc ON inc.ingredient_id = i.id
		LEFT JOIN nutrition_foods nf ON nf.id = inm.food_id
		WHERE ig.recipe_id = ?
			AND i.ingredient IS NOT NULL
		ORDER BY ig.order_index ASC, i.order_index ASC
	"""

	fun load(connection: Connection, recipeId: Int): RecipeNutrition? {
		val recipeRow = connection.prepareStatement(RECIPE_NUTRITION_SQL).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use { resultSet ->
				if (!resultSet.next()) {
					return@use null
				}
				resultSet.toRecipeNutritionRow()
			}
		}
		if (recipeRow == null || recipeRow.calories == null) {
			return null
		}

		val ingredients = loadIngredientLines(connection, recipeId)
		val recipeTotals = recipeRow.toRecipeTotals(ingredients)
		val totalWeightGrams = recipeRow.totalWeightGrams
			?: ingredients.mapNotNull(IngredientNutritionLine::grams).sum().takeIf { sum -> sum > 0.0 }
		val parsedServings = ServingsParser.parse(recipeRow.yields)
		val servingCount = recipeRow.servingCount ?: parsedServings?.count
		val perServing = servingCount?.let { count ->
			recipeTotals.scale(1.0 / count)
		}
		val per100Grams = totalWeightGrams
			?.takeIf { weight -> weight > 0.0 }
			?.let { weight -> recipeTotals.scale(GRAMS_PER_100_G / weight) }

		return RecipeNutrition(
			recipeTotals = recipeTotals,
			perServing = perServing,
			per100Grams = per100Grams,
			servingCount = servingCount,
			servingDescription = parsedServings?.description,
			totalWeightGrams = totalWeightGrams,
			ingredients = ingredients,
		)
	}

	private fun loadIngredientLines(connection: Connection, recipeId: Int): List<IngredientNutritionLine> =
		connection.prepareStatement(INGREDIENT_NUTRITION_SQL).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use { resultSet ->
				buildList {
					while (resultSet.next()) {
						val rawText = resultSet.getString("raw_text")?.trim().orEmpty()
						if (rawText.isEmpty()) {
							continue
						}
						add(resultSet.toIngredientNutritionLine(rawText))
					}
				}
			}
		}

	private fun ResultSet.toRecipeNutritionRow(): RecipeNutritionRow {
		val calories = getNullableDouble("calories")
		return RecipeNutritionRow(
			calories = calories,
			protein = getNullableDouble("protein"),
			carbohydrates = getNullableDouble("carbohydrates"),
			fat = getNullableDouble("fat"),
			fiber = getNullableDouble("fiber"),
			sugar = getNullableDouble("sugar"),
			sodium = getNullableDouble("sodium"),
			matchedIngredientCount = getNullableInt("matched_ingredient_count"),
			totalIngredientCount = getNullableInt("total_ingredient_count"),
			calculationSource = parseCalculationSource(getNullableString("calculation_source"))
				?: calories?.let { NutritionCalculationSource.SCRAPED },
			confidence = parseNutritionConfidence(getNullableString("confidence")),
			isComplete = getBoolean("is_complete"),
			totalWeightGrams = getNullableDouble("total_weight_grams"),
			servingCount = getNullableDouble("serving_count"),
			yields = getNullableString("yields"),
		)
	}

	private fun RecipeNutritionRow.toRecipeTotals(ingredients: List<IngredientNutritionLine>): NutritionSummary {
		val matchedFromIngredients = ingredients.count(IngredientNutritionLine::isMatched)
		val totalFromIngredients = ingredients.size

		return NutritionSummary(
			calories = calories,
			protein = protein,
			carbohydrates = carbohydrates,
			fat = fat,
			fiber = fiber,
			sugar = sugar,
			sodium = sodium,
			matchedIngredientCount = matchedIngredientCount ?: matchedFromIngredients.takeIf { count -> count > 0 },
			totalIngredientCount = totalIngredientCount ?: totalFromIngredients.takeIf { count -> count > 0 },
			calculationSource = calculationSource,
			confidence = confidence,
			isComplete = isComplete,
		)
	}

	private fun ResultSet.toIngredientNutritionLine(rawText: String): IngredientNutritionLine {
		val usesOverride = getBoolean("uses_user_override")
		val contribution = if (usesOverride) {
			readNutritionSummary(
				caloriesColumn = "override_calories",
				proteinColumn = "override_protein",
				carbohydratesColumn = "override_carbohydrates",
				fatColumn = "override_fat",
				fiberColumn = "override_fiber",
				sugarColumn = "override_sugar",
				sodiumColumn = "override_sodium",
			)
		} else {
			readNutritionSummary(
				caloriesColumn = "calories",
				proteinColumn = "protein",
				carbohydratesColumn = "carbohydrates",
				fatColumn = "fat",
				fiberColumn = "fiber",
				sugarColumn = "sugar",
				sodiumColumn = "sodium",
			)
		}
		val foodId = getNullableInt("food_id")
		val per100Grams = foodId?.let {
			readNutritionSummary(
				caloriesColumn = "food_calories_per_100",
				proteinColumn = "food_protein_per_100",
				carbohydratesColumn = "food_carbs_per_100",
				fatColumn = "food_fat_per_100",
				fiberColumn = "food_fiber_per_100",
				sugarColumn = "food_sugar_per_100",
				sodiumColumn = "food_sodium_per_100",
			)
		}

		return IngredientNutritionLine(
			ingredientId = getInt("ingredient_id"),
			rawText = rawText,
			parsedName = getNullableString("parsed_name"),
			quantity = getNullableDouble("quantity"),
			unit = getNullableString("unit"),
			isMeasurable = getBoolean("is_measurable"),
			isMatched = foodId != null,
			foodDisplayName = getNullableString("food_display_name"),
			grams = getNullableDouble("grams_resolved"),
			contribution = contribution,
			per100Grams = per100Grams,
		)
	}

	private fun ResultSet.readNutritionSummary(
		caloriesColumn: String,
		proteinColumn: String,
		carbohydratesColumn: String,
		fatColumn: String,
		fiberColumn: String,
		sugarColumn: String,
		sodiumColumn: String,
	): NutritionSummary =
		NutritionSummary(
			calories = getNullableDouble(caloriesColumn),
			protein = getNullableDouble(proteinColumn),
			carbohydrates = getNullableDouble(carbohydratesColumn),
			fat = getNullableDouble(fatColumn),
			fiber = getNullableDouble(fiberColumn),
			sugar = getNullableDouble(sugarColumn),
			sodium = getNullableDouble(sodiumColumn),
		)
}

private data class RecipeNutritionRow(
	val calories: Double?,
	val protein: Double?,
	val carbohydrates: Double?,
	val fat: Double?,
	val fiber: Double?,
	val sugar: Double?,
	val sodium: Double?,
	val matchedIngredientCount: Int?,
	val totalIngredientCount: Int?,
	val calculationSource: NutritionCalculationSource?,
	val confidence: NutritionConfidence?,
	val isComplete: Boolean,
	val totalWeightGrams: Double?,
	val servingCount: Double?,
	val yields: String?,
)

private fun parseCalculationSource(rawValue: String?): NutritionCalculationSource? =
	when (rawValue) {
		"calculated" -> NutritionCalculationSource.CALCULATED
		"scraped" -> NutritionCalculationSource.SCRAPED
		else -> null
	}

private fun parseNutritionConfidence(rawValue: String?): NutritionConfidence? =
	when (rawValue) {
		"complete" -> NutritionConfidence.COMPLETE
		"partial" -> NutritionConfidence.PARTIAL
		else -> null
	}

private fun ResultSet.getNullableDouble(columnLabel: String): Double? =
	getObject(columnLabel)?.let { value ->
		when (value) {
			is BigDecimal -> value.toDouble()
			is Number -> value.toDouble()
			else -> null
		}
	}

private fun ResultSet.getNullableInt(columnLabel: String): Int? =
	getObject(columnLabel)?.let { value ->
		when (value) {
			is Number -> value.toInt()
			else -> null
		}
	}

private const val GRAMS_PER_100_G = 100.0
