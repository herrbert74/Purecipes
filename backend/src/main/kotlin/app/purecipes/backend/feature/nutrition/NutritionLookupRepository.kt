package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import javax.sql.DataSource

internal class NutritionLookupRepository(
	private val dataSource: DataSource,
) {
	fun loadIndex(): NutritionLookupIndex {
		val foods = loadFoods()
		val aliases = loadAliases()
		val measures = loadMeasures()
		return NutritionLookupIndex(
			foodById = foods.associateBy { it.id },
			foodIdByNormalizedAlias = aliases,
			measuresByFoodId = measures,
		)
	}

	private fun loadFoods(): List<NutritionFoodRecord> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT
					id,
					display_name,
					normalized_name,
					calories_per_100g,
					protein_per_100g,
					carbohydrates_per_100g,
					fat_per_100g,
					fiber_per_100g,
					sugar_per_100g,
					sodium_per_100g
				FROM nutrition_foods
				WHERE calories_per_100g IS NOT NULL
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							val calories = resultSet.getBigDecimal("calories_per_100g")
							add(
								NutritionFoodRecord(
									id = resultSet.getInt("id"),
									displayName = resultSet.getString("display_name"),
									normalizedName = resultSet.getString("normalized_name"),
									nutrients = FdcNutrientsPer100g(
										calories = calories,
										protein = resultSet.getBigDecimal("protein_per_100g"),
										carbohydrates = resultSet.getBigDecimal("carbohydrates_per_100g"),
										fat = resultSet.getBigDecimal("fat_per_100g"),
										fiber = resultSet.getBigDecimal("fiber_per_100g"),
										sugar = resultSet.getBigDecimal("sugar_per_100g"),
										sodium = resultSet.getBigDecimal("sodium_per_100g"),
									),
								),
							)
						}
					}
				}
			}
		}

	private fun loadAliases(): Map<String, Int> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT normalized_alias, food_id
				FROM nutrition_food_aliases
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildMap {
						while (resultSet.next()) {
							put(resultSet.getString("normalized_alias"), resultSet.getInt("food_id"))
						}
					}
				}
			}
		}

	private fun loadMeasures(): Map<Int, Map<String, BigDecimal>> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT food_id, measure_name, grams_per_measure
				FROM nutrition_food_measures
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use(::readMeasures)
			}
		}

	private fun readMeasures(resultSet: java.sql.ResultSet): Map<Int, Map<String, BigDecimal>> {
		val measures = mutableMapOf<Int, MutableMap<String, BigDecimal>>()
		while (resultSet.next()) {
			val foodId = resultSet.getInt("food_id")
			val measureName = resultSet.getString("measure_name")
			val gramsPerMeasure = resultSet.getBigDecimal("grams_per_measure")
			measures.getOrPut(foodId) { mutableMapOf() }[measureName] = gramsPerMeasure
		}
		return measures
	}
}
