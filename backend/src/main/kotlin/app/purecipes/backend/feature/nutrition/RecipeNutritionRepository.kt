package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.recipe.getNullableString
import java.math.BigDecimal
import java.sql.Connection
import javax.sql.DataSource

private object IngredientMeasurementBindIndex {
	const val INGREDIENT_ID = 1
	const val RAW_TEXT = 2
	const val QUANTITY = 3
	const val UNIT = 4
	const val PARSED_NAME = 5
	const val IS_MEASURABLE = 6
}

private object IngredientMatchBindIndex {
	const val INGREDIENT_ID = 1
	const val RAW_TEXT = 2
	const val QUANTITY = 3
	const val UNIT = 4
	const val PARSED_NAME = 5
	const val FOOD_ID = 6
	const val CONFIDENCE = 7
	const val MATCH_SOURCE = 8
}

private object RecipeNutritionBindIndex {
	const val RECIPE_ID = 1
	const val CALORIES = 2
	const val PROTEIN = 3
	const val CARBOHYDRATES = 4
	const val FAT = 5
	const val FIBER = 6
	const val SUGAR = 7
	const val SODIUM = 8
	const val MATCHED_INGREDIENT_COUNT = 9
	const val TOTAL_INGREDIENT_COUNT = 10
	const val CALCULATION_SOURCE = 11
	const val CONFIDENCE = 12
	const val IS_COMPLETE = 13
	const val TOTAL_WEIGHT_GRAMS = 14
	const val SERVING_COUNT = 15
}

private object IngredientContributionBindIndex {
	const val INGREDIENT_ID = 1
	const val GRAMS = 2
	const val CALORIES = 3
	const val PROTEIN = 4
	const val CARBOHYDRATES = 5
	const val FAT = 6
	const val FIBER = 7
	const val SUGAR = 8
	const val SODIUM = 9
}

internal data class RecipeIngredientRow(
	val ingredientId: Int,
	val rawText: String,
)

internal class RecipeNutritionRepository(
	private val dataSource: DataSource,
) {
	fun loadRecipeIngredients(recipeId: Int): List<RecipeIngredientRow> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT i.id, i.ingredient
				FROM ingredients i
				JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
				WHERE ig.recipe_id = ?
					AND i.ingredient IS NOT NULL
				ORDER BY ig.order_index ASC, i.order_index ASC
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, recipeId)
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							val rawText = resultSet.getString("ingredient")?.trim().orEmpty()
							if (rawText.isNotEmpty()) {
								add(
									RecipeIngredientRow(
										ingredientId = resultSet.getInt("id"),
										rawText = rawText,
									),
								)
							}
						}
					}
				}
			}
		}

	fun listRecipeIds(): List<Int> =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery("SELECT id FROM recipes ORDER BY id ASC").use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.getInt("id"))
						}
					}
				}
			}
		}

	fun upsertIngredientMeasurement(
		ingredientId: Int,
		parsed: ParsedIngredientLine,
	) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO ingredient_measurements (
					ingredient_id,
					raw_text,
					quantity,
					unit,
					parsed_name,
					is_measurable
				) VALUES (?, ?, ?, ?, ?, ?)
				ON CONFLICT (ingredient_id) DO UPDATE SET
					raw_text = EXCLUDED.raw_text,
					quantity = EXCLUDED.quantity,
					unit = EXCLUDED.unit,
					parsed_name = EXCLUDED.parsed_name,
					is_measurable = EXCLUDED.is_measurable
				""".trimIndent(),
			).use { statement ->
				statement.setInt(IngredientMeasurementBindIndex.INGREDIENT_ID, ingredientId)
				statement.setString(IngredientMeasurementBindIndex.RAW_TEXT, parsed.rawText)
				statement.setBigDecimal(IngredientMeasurementBindIndex.QUANTITY, parsed.quantity)
				statement.setString(IngredientMeasurementBindIndex.UNIT, parsed.unit)
				statement.setString(IngredientMeasurementBindIndex.PARSED_NAME, parsed.parsedName)
				statement.setBoolean(IngredientMeasurementBindIndex.IS_MEASURABLE, parsed.isMeasurable)
				statement.executeUpdate()
			}
		}
	}

	fun upsertIngredientMatch(
		ingredientId: Int,
		parsed: ParsedIngredientLine,
		foodMatch: NutritionFoodMatch,
	) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO ingredient_nutrition_matches (
					ingredient_id,
					raw_text,
					quantity,
					unit,
					parsed_name,
					food_id,
					confidence,
					match_source,
					updated_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
				ON CONFLICT (ingredient_id) DO UPDATE SET
					raw_text = EXCLUDED.raw_text,
					quantity = EXCLUDED.quantity,
					unit = EXCLUDED.unit,
					parsed_name = EXCLUDED.parsed_name,
					food_id = EXCLUDED.food_id,
					confidence = EXCLUDED.confidence,
					match_source = EXCLUDED.match_source,
					updated_at = CURRENT_TIMESTAMP
				""".trimIndent(),
			).use { statement ->
				statement.setInt(IngredientMatchBindIndex.INGREDIENT_ID, ingredientId)
				statement.setString(IngredientMatchBindIndex.RAW_TEXT, parsed.rawText)
				statement.setBigDecimal(IngredientMatchBindIndex.QUANTITY, parsed.quantity)
				statement.setString(IngredientMatchBindIndex.UNIT, parsed.unit)
				statement.setString(IngredientMatchBindIndex.PARSED_NAME, parsed.parsedName)
				statement.setInt(IngredientMatchBindIndex.FOOD_ID, foodMatch.foodId)
				statement.setBigDecimal(IngredientMatchBindIndex.CONFIDENCE, foodMatch.confidence)
				statement.setString(IngredientMatchBindIndex.MATCH_SOURCE, foodMatch.matchSource)
				statement.executeUpdate()
			}
		}
	}

	fun loadRecipeYields(recipeId: Int): String? =
		dataSource.connection.use { connection ->
			connection.readRecipeYields(recipeId)
		}

	fun deleteIngredientMatch(ingredientId: Int) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"DELETE FROM ingredient_nutrition_matches WHERE ingredient_id = ?",
			).use { statement ->
				statement.setInt(1, ingredientId)
				statement.executeUpdate()
			}
		}
	}

	fun upsertIngredientContribution(
		ingredientId: Int,
		contribution: StoredIngredientNutrition,
	) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO ingredient_nutrition_contributions (
					ingredient_id,
					grams_resolved,
					calories,
					protein,
					carbohydrates,
					fat,
					fiber,
					sugar,
					sodium,
					updated_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
				ON CONFLICT (ingredient_id) DO UPDATE SET
					grams_resolved = EXCLUDED.grams_resolved,
					calories = EXCLUDED.calories,
					protein = EXCLUDED.protein,
					carbohydrates = EXCLUDED.carbohydrates,
					fat = EXCLUDED.fat,
					fiber = EXCLUDED.fiber,
					sugar = EXCLUDED.sugar,
					sodium = EXCLUDED.sodium,
					updated_at = CURRENT_TIMESTAMP
				""".trimIndent(),
			).use { statement ->
				statement.setInt(IngredientContributionBindIndex.INGREDIENT_ID, ingredientId)
				statement.setBigDecimal(IngredientContributionBindIndex.GRAMS, contribution.grams)
				statement.setBigDecimal(IngredientContributionBindIndex.CALORIES, contribution.calories)
				statement.setBigDecimal(IngredientContributionBindIndex.PROTEIN, contribution.protein)
				statement.setBigDecimal(IngredientContributionBindIndex.CARBOHYDRATES, contribution.carbohydrates)
				statement.setBigDecimal(IngredientContributionBindIndex.FAT, contribution.fat)
				statement.setBigDecimal(IngredientContributionBindIndex.FIBER, contribution.fiber)
				statement.setBigDecimal(IngredientContributionBindIndex.SUGAR, contribution.sugar)
				statement.setBigDecimal(IngredientContributionBindIndex.SODIUM, contribution.sodium)
				statement.executeUpdate()
			}
		}
	}

	fun deleteIngredientContribution(ingredientId: Int) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"DELETE FROM ingredient_nutrition_contributions WHERE ingredient_id = ?",
			).use { statement ->
				statement.setInt(1, ingredientId)
				statement.executeUpdate()
			}
		}
	}

	fun upsertRecipeNutrition(
		recipeId: Int,
		totals: CalculatedNutritionTotals,
		totalWeightGrams: BigDecimal?,
		servingCount: BigDecimal?,
	) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO nutrition (
					recipe_id,
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
					is_complete,
					total_weight_grams,
					serving_count,
					updated_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
				ON CONFLICT (recipe_id) DO UPDATE SET
					calories = EXCLUDED.calories,
					protein = EXCLUDED.protein,
					carbohydrates = EXCLUDED.carbohydrates,
					fat = EXCLUDED.fat,
					fiber = EXCLUDED.fiber,
					sugar = EXCLUDED.sugar,
					sodium = EXCLUDED.sodium,
					matched_ingredient_count = EXCLUDED.matched_ingredient_count,
					total_ingredient_count = EXCLUDED.total_ingredient_count,
					calculation_source = EXCLUDED.calculation_source,
					confidence = EXCLUDED.confidence,
					is_complete = EXCLUDED.is_complete,
					total_weight_grams = EXCLUDED.total_weight_grams,
					serving_count = EXCLUDED.serving_count,
					updated_at = CURRENT_TIMESTAMP
				""".trimIndent(),
			).use { statement ->
				statement.setInt(RecipeNutritionBindIndex.RECIPE_ID, recipeId)
				statement.setBigDecimal(RecipeNutritionBindIndex.CALORIES, totals.calories)
				statement.setBigDecimal(RecipeNutritionBindIndex.PROTEIN, totals.protein)
				statement.setBigDecimal(RecipeNutritionBindIndex.CARBOHYDRATES, totals.carbohydrates)
				statement.setBigDecimal(RecipeNutritionBindIndex.FAT, totals.fat)
				statement.setBigDecimal(RecipeNutritionBindIndex.FIBER, totals.fiber)
				statement.setBigDecimal(RecipeNutritionBindIndex.SUGAR, totals.sugar)
				statement.setBigDecimal(RecipeNutritionBindIndex.SODIUM, totals.sodium)
				statement.setInt(RecipeNutritionBindIndex.MATCHED_INGREDIENT_COUNT, totals.matchedIngredientCount)
				statement.setInt(RecipeNutritionBindIndex.TOTAL_INGREDIENT_COUNT, totals.totalIngredientCount)
				statement.setString(RecipeNutritionBindIndex.CALCULATION_SOURCE, CALCULATION_SOURCE)
				statement.setString(RecipeNutritionBindIndex.CONFIDENCE, recipeConfidence(totals))
				statement.setBoolean(RecipeNutritionBindIndex.IS_COMPLETE, totals.isComplete)
				statement.setBigDecimal(RecipeNutritionBindIndex.TOTAL_WEIGHT_GRAMS, totalWeightGrams)
				statement.setBigDecimal(RecipeNutritionBindIndex.SERVING_COUNT, servingCount)
				statement.executeUpdate()
			}
		}
	}

	private fun recipeConfidence(totals: CalculatedNutritionTotals): String =
		if (totals.isComplete) {
			RECIPE_CONFIDENCE_COMPLETE
		} else {
			RECIPE_CONFIDENCE_PARTIAL
		}

	private companion object {
		const val CALCULATION_SOURCE = "calculated"
		const val RECIPE_CONFIDENCE_COMPLETE = "complete"
		const val RECIPE_CONFIDENCE_PARTIAL = "partial"
	}
}

private fun Connection.readRecipeYields(recipeId: Int): String? =
	prepareStatement("SELECT yields FROM recipes WHERE id = ?").use { statement ->
		statement.setInt(1, recipeId)
		statement.executeQuery().use { resultSet ->
			if (resultSet.next()) {
				resultSet.getNullableString("yields")
			} else {
				null
			}
		}
	}
