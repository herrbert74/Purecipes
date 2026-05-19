package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionConfidence
import app.purecipes.shared.domain.model.NutritionSummary
import java.math.BigDecimal
import java.sql.Connection
import java.sql.ResultSet

internal object RecipeNutritionSummaryLoader {

	private const val NUTRITION_SQL = """
		SELECT
			calories,
			protein,
			carbohydrates,
			fat,
			fiber,
			sugar,
			sodium,
			matched_ingredient_count,
			total_ingredient_count,
			calculation_source,
			confidence,
			is_complete
		FROM nutrition
		WHERE recipe_id = ?
	"""

	fun load(connection: Connection, recipeId: Int): NutritionSummary? {
		return connection.prepareStatement(NUTRITION_SQL).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use { resultSet ->
				if (!resultSet.next()) {
					return@use null
				}
				resultSet.toNutritionSummary()
			}
		}
	}
}

private fun ResultSet.toNutritionSummary(): NutritionSummary? {
	val calories = getNullableDouble("calories")
	val protein = getNullableDouble("protein")
	val carbohydrates = getNullableDouble("carbohydrates")
	val fat = getNullableDouble("fat")
	val fiber = getNullableDouble("fiber")
	val sugar = getNullableDouble("sugar")
	val sodium = getNullableDouble("sodium")
	val matchedIngredientCount = getNullableInt("matched_ingredient_count")
	val totalIngredientCount = getNullableInt("total_ingredient_count")
	val calculationSource = parseCalculationSource(getNullableString("calculation_source"))
	val confidence = parseNutritionConfidence(getNullableString("confidence"))
	val isComplete = getBoolean("is_complete")

	if (!hasNutritionValues(
			calories = calories,
			protein = protein,
			carbohydrates = carbohydrates,
			fat = fat,
			fiber = fiber,
			sugar = sugar,
			sodium = sodium,
			matchedIngredientCount = matchedIngredientCount,
			totalIngredientCount = totalIngredientCount,
		)
	) {
		return null
	}

	val resolvedCalculationSource = calculationSource
		?: calories?.let { NutritionCalculationSource.SCRAPED }

	return NutritionSummary(
		calories = calories,
		protein = protein,
		carbohydrates = carbohydrates,
		fat = fat,
		fiber = fiber,
		sugar = sugar,
		sodium = sodium,
		matchedIngredientCount = matchedIngredientCount,
		totalIngredientCount = totalIngredientCount,
		calculationSource = resolvedCalculationSource,
		confidence = confidence,
		isComplete = isComplete,
	)
}

private fun hasNutritionValues(
	calories: Double?,
	protein: Double?,
	carbohydrates: Double?,
	fat: Double?,
	fiber: Double?,
	sugar: Double?,
	sodium: Double?,
	matchedIngredientCount: Int?,
	totalIngredientCount: Int?,
): Boolean =
	listOf(
		calories,
		protein,
		carbohydrates,
		fat,
		fiber,
		sugar,
		sodium,
		matchedIngredientCount,
		totalIngredientCount,
	).any { value -> value != null }

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
