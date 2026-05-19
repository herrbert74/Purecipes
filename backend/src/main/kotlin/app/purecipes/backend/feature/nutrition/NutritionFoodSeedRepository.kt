package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import javax.sql.DataSource

private object UpsertFoodBindIndex {
	const val SOURCE_NAME = 1
	const val SOURCE_ID = 2
	const val DISPLAY_NAME = 3
	const val NORMALIZED_NAME = 4
	const val CALORIES = 5
	const val PROTEIN = 6
	const val CARBOHYDRATES = 7
	const val FAT = 8
	const val FIBER = 9
	const val SUGAR = 10
	const val SODIUM = 11
	const val SOURCE_METADATA = 12
}

private object UpsertAliasBindIndex {
	const val FOOD_ID = 1
	const val ALIAS = 2
	const val NORMALIZED_ALIAS = 3
}

private object UpsertMeasureBindIndex {
	const val FOOD_ID = 1
	const val MEASURE_NAME = 2
	const val GRAMS_PER_MEASURE = 3
}

internal class NutritionFoodSeedRepository(
	private val dataSource: DataSource,
) {
	fun replaceSeedData() {
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute("DELETE FROM ingredient_nutrition_matches")
				statement.execute("DELETE FROM nutrition_food_aliases")
				statement.execute("DELETE FROM nutrition_food_measures")
				statement.execute("DELETE FROM nutrition_foods")
			}
		}
	}

	fun loadFoodsForMatching(): List<FdcFoundationFood> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT source_name, source_id, display_name
				FROM nutrition_foods
				ORDER BY source_name, display_name
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(
								FdcFoundationFood(
									sourceName = resultSet.getString("source_name"),
									fdcId = resultSet.getString("source_id").toLong(),
									description = resultSet.getString("display_name"),
									nutrients = emptyList(),
									portions = emptyList(),
								),
							)
						}
					}
				}
			}
		}

	fun findFoodId(sourceName: String, fdcId: Long): Int? =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT id
				FROM nutrition_foods
				WHERE source_name = ? AND source_id = ?
				""".trimIndent(),
			).use { statement ->
				statement.setString(1, sourceName)
				statement.setString(2, fdcId.toString())
				statement.executeQuery().use { resultSet ->
					readFirstIntColumn(resultSet, columnName = "id")
				}
			}
		}

	private fun readFirstIntColumn(
		resultSet: java.sql.ResultSet,
		columnName: String,
	): Int? =
		if (resultSet.next()) {
			resultSet.getInt(columnName)
		} else {
			null
		}

	fun upsertFood(
		food: FdcFoundationFood,
		nutrients: FdcNutrientsPer100g,
		sourceMetadata: String,
	): Int {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO nutrition_foods (
					source_name,
					source_id,
					display_name,
					normalized_name,
					calories_per_100g,
					protein_per_100g,
					carbohydrates_per_100g,
					fat_per_100g,
					fiber_per_100g,
					sugar_per_100g,
					sodium_per_100g,
					source_metadata,
					updated_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
				ON CONFLICT (source_name, source_id) DO UPDATE SET
					display_name = EXCLUDED.display_name,
					normalized_name = EXCLUDED.normalized_name,
					calories_per_100g = EXCLUDED.calories_per_100g,
					protein_per_100g = EXCLUDED.protein_per_100g,
					carbohydrates_per_100g = EXCLUDED.carbohydrates_per_100g,
					fat_per_100g = EXCLUDED.fat_per_100g,
					fiber_per_100g = EXCLUDED.fiber_per_100g,
					sugar_per_100g = EXCLUDED.sugar_per_100g,
					sodium_per_100g = EXCLUDED.sodium_per_100g,
					source_metadata = EXCLUDED.source_metadata,
					updated_at = CURRENT_TIMESTAMP
				RETURNING id
				""".trimIndent(),
			).use { statement ->
				statement.setString(UpsertFoodBindIndex.SOURCE_NAME, food.sourceName)
				statement.setString(UpsertFoodBindIndex.SOURCE_ID, food.fdcId.toString())
				statement.setString(UpsertFoodBindIndex.DISPLAY_NAME, food.description)
				statement.setString(UpsertFoodBindIndex.NORMALIZED_NAME, food.normalizedDescription)
				statement.setBigDecimal(UpsertFoodBindIndex.CALORIES, nutrients.calories)
				statement.setBigDecimal(UpsertFoodBindIndex.PROTEIN, nutrients.protein)
				statement.setBigDecimal(UpsertFoodBindIndex.CARBOHYDRATES, nutrients.carbohydrates)
				statement.setBigDecimal(UpsertFoodBindIndex.FAT, nutrients.fat)
				statement.setBigDecimal(UpsertFoodBindIndex.FIBER, nutrients.fiber)
				statement.setBigDecimal(UpsertFoodBindIndex.SUGAR, nutrients.sugar)
				statement.setBigDecimal(UpsertFoodBindIndex.SODIUM, nutrients.sodium)
				statement.setString(UpsertFoodBindIndex.SOURCE_METADATA, sourceMetadata)
				statement.executeQuery().use { resultSet ->
					resultSet.next()
					return resultSet.getInt("id")
				}
			}
		}
	}

	fun upsertAlias(foodId: Int, alias: String) {
		val normalizedAlias = NutritionNameNormalizer.normalize(alias)
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO nutrition_food_aliases (food_id, alias, normalized_alias)
				VALUES (?, ?, ?)
				ON CONFLICT (normalized_alias) DO UPDATE SET
					food_id = EXCLUDED.food_id,
					alias = EXCLUDED.alias
				""".trimIndent(),
			).use { statement ->
				statement.setInt(UpsertAliasBindIndex.FOOD_ID, foodId)
				statement.setString(UpsertAliasBindIndex.ALIAS, alias)
				statement.setString(UpsertAliasBindIndex.NORMALIZED_ALIAS, normalizedAlias)
				statement.executeUpdate()
			}
		}
	}

	fun upsertMeasure(foodId: Int, measureName: String, gramsPerMeasure: BigDecimal) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				INSERT INTO nutrition_food_measures (food_id, measure_name, grams_per_measure)
				VALUES (?, ?, ?)
				ON CONFLICT (food_id, measure_name) DO UPDATE SET
					grams_per_measure = EXCLUDED.grams_per_measure
				""".trimIndent(),
			).use { statement ->
				statement.setInt(UpsertMeasureBindIndex.FOOD_ID, foodId)
				statement.setString(UpsertMeasureBindIndex.MEASURE_NAME, measureName)
				statement.setBigDecimal(UpsertMeasureBindIndex.GRAMS_PER_MEASURE, gramsPerMeasure)
				statement.executeUpdate()
			}
		}
	}

	fun countFoods(): Int =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery("SELECT COUNT(*) AS total FROM nutrition_foods").use { resultSet ->
					resultSet.next()
					resultSet.getInt("total")
				}
			}
		}

	fun countAliases(): Int =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery("SELECT COUNT(*) AS total FROM nutrition_food_aliases").use { resultSet ->
					resultSet.next()
					resultSet.getInt("total")
				}
			}
		}
}
