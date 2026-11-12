package app.purecipes.backend.tools

import java.math.BigDecimal
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

internal data class NutritionFoodIdentity(
	val sourceName: String,
	val sourceId: String,
)

internal data class NutritionFoodCatalogueRow(
	val identity: NutritionFoodIdentity,
	val displayName: String,
	val normalizedName: String,
	val caloriesPer100g: BigDecimal?,
	val proteinPer100g: BigDecimal?,
	val carbohydratesPer100g: BigDecimal?,
	val fatPer100g: BigDecimal?,
	val fiberPer100g: BigDecimal?,
	val sugarPer100g: BigDecimal?,
	val sodiumPer100g: BigDecimal?,
	val sourceMetadata: String?,
)

internal data class NutritionFoodAliasCatalogueRow(
	val foodIdentity: NutritionFoodIdentity,
	val alias: String,
	val normalizedAlias: String,
)

internal data class NutritionFoodMeasureCatalogueRow(
	val foodIdentity: NutritionFoodIdentity,
	val measureName: String,
	val gramsPerMeasure: BigDecimal,
)

internal data class NutritionCatalogueSyncSummary(
	val sourceFoodCount: Int,
	val insertedFoodCount: Int,
	val updatedFoodCount: Int,
	val sourceAliasCount: Int,
	val insertedAliasCount: Int,
	val updatedAliasCount: Int,
	val sourceMeasureCount: Int,
	val insertedMeasureCount: Int,
	val updatedMeasureCount: Int,
	val dryRun: Boolean,
)

private object FoodRowBindIndex {

	const val DISPLAY_NAME = 1
	const val NORMALIZED_NAME = 2
	const val CALORIES = 3
	const val PROTEIN = 4
	const val CARBOHYDRATES = 5
	const val FAT = 6
	const val FIBER = 7
	const val SUGAR = 8
	const val SODIUM = 9
	const val SOURCE_METADATA = 10
	const val SOURCE_NAME = 11
	const val SOURCE_ID = 12
}

private object AliasRowBindIndex {

	const val FOOD_ID = 1
	const val ALIAS = 2
	const val NORMALIZED_ALIAS = 3
}

private object MeasureRowBindIndex {

	const val GRAMS_PER_MEASURE = 1
	const val FOOD_ID = 2
	const val MEASURE_NAME = 3
}

internal class NutritionCatalogueSyncer(
	private val sourceDataSource: DataSource,
	private val targetDataSource: DataSource,
) {

	fun sync(dryRun: Boolean = false): NutritionCatalogueSyncSummary {
		val sourceFoods = loadFoods(sourceDataSource)
		val sourceAliases = loadAliases(sourceDataSource)
		val sourceMeasures = loadMeasures(sourceDataSource)

		val targetFoodsByIdentity = loadFoods(targetDataSource).associateBy(NutritionFoodCatalogueRow::identity)
		val targetAliasesByNormalized = loadAliases(targetDataSource)
			.associateBy(NutritionFoodAliasCatalogueRow::normalizedAlias)
		val targetMeasuresByKey = loadMeasures(targetDataSource)
			.associateBy { row -> row.foodIdentity to row.measureName }

		val foodsToInsert = sourceFoods.filter { row -> row.identity !in targetFoodsByIdentity }
		val foodsToUpdate = sourceFoods.filter { row ->
			val existing = targetFoodsByIdentity[row.identity]
			existing != null && existing != row
		}
		val aliasesToInsert = sourceAliases.filter { row -> row.normalizedAlias !in targetAliasesByNormalized }
		val aliasesToUpdate = sourceAliases.filter { row ->
			val existing = targetAliasesByNormalized[row.normalizedAlias]
			existing != null && existing != row
		}
		val measuresToInsert = sourceMeasures.filter { row ->
			(row.foodIdentity to row.measureName) !in targetMeasuresByKey
		}
		val measuresToUpdate = sourceMeasures.filter { row ->
			val existing = targetMeasuresByKey[row.foodIdentity to row.measureName]
			existing != null && !sameMeasureRow(existing, row)
		}

		if (!dryRun) {
			applyChanges(
				foodsToInsert = foodsToInsert,
				foodsToUpdate = foodsToUpdate,
				aliasesToInsert = aliasesToInsert,
				aliasesToUpdate = aliasesToUpdate,
				measuresToInsert = measuresToInsert,
				measuresToUpdate = measuresToUpdate,
			)
		}

		return NutritionCatalogueSyncSummary(
			sourceFoodCount = sourceFoods.size,
			insertedFoodCount = foodsToInsert.size,
			updatedFoodCount = foodsToUpdate.size,
			sourceAliasCount = sourceAliases.size,
			insertedAliasCount = aliasesToInsert.size,
			updatedAliasCount = aliasesToUpdate.size,
			sourceMeasureCount = sourceMeasures.size,
			insertedMeasureCount = measuresToInsert.size,
			updatedMeasureCount = measuresToUpdate.size,
			dryRun = dryRun,
		)
	}

	private fun sameMeasureRow(
		first: NutritionFoodMeasureCatalogueRow,
		second: NutritionFoodMeasureCatalogueRow,
	): Boolean =
		first.foodIdentity == second.foodIdentity &&
			first.measureName == second.measureName &&
			first.gramsPerMeasure.compareTo(second.gramsPerMeasure) == 0

	private fun applyChanges(
		foodsToInsert: List<NutritionFoodCatalogueRow>,
		foodsToUpdate: List<NutritionFoodCatalogueRow>,
		aliasesToInsert: List<NutritionFoodAliasCatalogueRow>,
		aliasesToUpdate: List<NutritionFoodAliasCatalogueRow>,
		measuresToInsert: List<NutritionFoodMeasureCatalogueRow>,
		measuresToUpdate: List<NutritionFoodMeasureCatalogueRow>,
	) {
		targetDataSource.connection.use { connection ->
			val previousAutoCommit = connection.autoCommit
			connection.autoCommit = false
			try {
				insertFoods(connection, foodsToInsert)
				updateFoods(connection, foodsToUpdate)
				val targetFoodIdsByIdentity = loadFoodIdsByIdentity(connection)
				insertAliases(connection, aliasesToInsert, targetFoodIdsByIdentity)
				updateAliases(connection, aliasesToUpdate, targetFoodIdsByIdentity)
				insertMeasures(connection, measuresToInsert, targetFoodIdsByIdentity)
				updateMeasures(connection, measuresToUpdate, targetFoodIdsByIdentity)
				connection.commit()
			} catch (exception: SQLException) {
				connection.rollback()
				throw exception
			} finally {
				connection.autoCommit = previousAutoCommit
			}
		}
	}

	private fun insertFoods(connection: Connection, rows: List<NutritionFoodCatalogueRow>) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			INSERT INTO nutrition_foods (
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
				source_name,
				source_id,
				updated_at
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				bindFoodStatement(statement, row)
				statement.executeUpdate()
			}
		}
	}

	private fun updateFoods(connection: Connection, rows: List<NutritionFoodCatalogueRow>) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			UPDATE nutrition_foods SET
				display_name = ?,
				normalized_name = ?,
				calories_per_100g = ?,
				protein_per_100g = ?,
				carbohydrates_per_100g = ?,
				fat_per_100g = ?,
				fiber_per_100g = ?,
				sugar_per_100g = ?,
				sodium_per_100g = ?,
				source_metadata = ?,
				updated_at = CURRENT_TIMESTAMP
			WHERE source_name = ? AND source_id = ?
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				bindFoodStatement(statement, row)
				statement.executeUpdate()
			}
		}
	}

	private fun bindFoodStatement(statement: PreparedStatement, row: NutritionFoodCatalogueRow) {
		statement.setString(FoodRowBindIndex.DISPLAY_NAME, row.displayName)
		statement.setString(FoodRowBindIndex.NORMALIZED_NAME, row.normalizedName)
		statement.setBigDecimal(FoodRowBindIndex.CALORIES, row.caloriesPer100g)
		statement.setBigDecimal(FoodRowBindIndex.PROTEIN, row.proteinPer100g)
		statement.setBigDecimal(FoodRowBindIndex.CARBOHYDRATES, row.carbohydratesPer100g)
		statement.setBigDecimal(FoodRowBindIndex.FAT, row.fatPer100g)
		statement.setBigDecimal(FoodRowBindIndex.FIBER, row.fiberPer100g)
		statement.setBigDecimal(FoodRowBindIndex.SUGAR, row.sugarPer100g)
		statement.setBigDecimal(FoodRowBindIndex.SODIUM, row.sodiumPer100g)
		statement.setString(FoodRowBindIndex.SOURCE_METADATA, row.sourceMetadata)
		statement.setString(FoodRowBindIndex.SOURCE_NAME, row.identity.sourceName)
		statement.setString(FoodRowBindIndex.SOURCE_ID, row.identity.sourceId)
	}

	private fun insertAliases(
		connection: Connection,
		rows: List<NutritionFoodAliasCatalogueRow>,
		foodIdsByIdentity: Map<NutritionFoodIdentity, Int>,
	) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			INSERT INTO nutrition_food_aliases (food_id, alias, normalized_alias)
			VALUES (?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				val foodId = foodIdsByIdentity[row.foodIdentity] ?: return@forEach
				statement.setInt(AliasRowBindIndex.FOOD_ID, foodId)
				statement.setString(AliasRowBindIndex.ALIAS, row.alias)
				statement.setString(AliasRowBindIndex.NORMALIZED_ALIAS, row.normalizedAlias)
				statement.executeUpdate()
			}
		}
	}

	private fun updateAliases(
		connection: Connection,
		rows: List<NutritionFoodAliasCatalogueRow>,
		foodIdsByIdentity: Map<NutritionFoodIdentity, Int>,
	) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			UPDATE nutrition_food_aliases
			SET food_id = ?, alias = ?
			WHERE normalized_alias = ?
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				val foodId = foodIdsByIdentity[row.foodIdentity] ?: return@forEach
				statement.setInt(AliasRowBindIndex.FOOD_ID, foodId)
				statement.setString(AliasRowBindIndex.ALIAS, row.alias)
				statement.setString(AliasRowBindIndex.NORMALIZED_ALIAS, row.normalizedAlias)
				statement.executeUpdate()
			}
		}
	}

	private fun insertMeasures(
		connection: Connection,
		rows: List<NutritionFoodMeasureCatalogueRow>,
		foodIdsByIdentity: Map<NutritionFoodIdentity, Int>,
	) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			INSERT INTO nutrition_food_measures (grams_per_measure, food_id, measure_name)
			VALUES (?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				val foodId = foodIdsByIdentity[row.foodIdentity] ?: return@forEach
				statement.setBigDecimal(MeasureRowBindIndex.GRAMS_PER_MEASURE, row.gramsPerMeasure)
				statement.setInt(MeasureRowBindIndex.FOOD_ID, foodId)
				statement.setString(MeasureRowBindIndex.MEASURE_NAME, row.measureName)
				statement.executeUpdate()
			}
		}
	}

	private fun updateMeasures(
		connection: Connection,
		rows: List<NutritionFoodMeasureCatalogueRow>,
		foodIdsByIdentity: Map<NutritionFoodIdentity, Int>,
	) {
		if (rows.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"""
			UPDATE nutrition_food_measures
			SET grams_per_measure = ?
			WHERE food_id = ? AND measure_name = ?
			""".trimIndent(),
		).use { statement ->
			rows.forEach { row ->
				val foodId = foodIdsByIdentity[row.foodIdentity] ?: return@forEach
				statement.setBigDecimal(MeasureRowBindIndex.GRAMS_PER_MEASURE, row.gramsPerMeasure)
				statement.setInt(MeasureRowBindIndex.FOOD_ID, foodId)
				statement.setString(MeasureRowBindIndex.MEASURE_NAME, row.measureName)
				statement.executeUpdate()
			}
		}
	}

	private fun loadFoodIdsByIdentity(connection: Connection): Map<NutritionFoodIdentity, Int> =
		connection.createStatement().use { statement ->
			statement.executeQuery(
				"""
				SELECT id, source_name, source_id
				FROM nutrition_foods
				""".trimIndent(),
			).use { resultSet ->
				buildMap {
					while (resultSet.next()) {
						val identity = NutritionFoodIdentity(
							sourceName = resultSet.getString("source_name"),
							sourceId = resultSet.getString("source_id"),
						)
						put(identity, resultSet.getInt("id"))
					}
				}
			}
		}

	private fun loadFoods(dataSource: DataSource): List<NutritionFoodCatalogueRow> =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery(
					"""
					SELECT
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
						source_metadata
					FROM nutrition_foods
					ORDER BY source_name, source_id
					""".trimIndent(),
				).use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(readFoodRow(resultSet))
						}
					}
				}
			}
		}

	private fun readFoodRow(resultSet: ResultSet): NutritionFoodCatalogueRow =
		NutritionFoodCatalogueRow(
			identity = NutritionFoodIdentity(
				sourceName = resultSet.getString("source_name"),
				sourceId = resultSet.getString("source_id"),
			),
			displayName = resultSet.getString("display_name"),
			normalizedName = resultSet.getString("normalized_name"),
			caloriesPer100g = resultSet.getBigDecimal("calories_per_100g"),
			proteinPer100g = resultSet.getBigDecimal("protein_per_100g"),
			carbohydratesPer100g = resultSet.getBigDecimal("carbohydrates_per_100g"),
			fatPer100g = resultSet.getBigDecimal("fat_per_100g"),
			fiberPer100g = resultSet.getBigDecimal("fiber_per_100g"),
			sugarPer100g = resultSet.getBigDecimal("sugar_per_100g"),
			sodiumPer100g = resultSet.getBigDecimal("sodium_per_100g"),
			sourceMetadata = resultSet.getString("source_metadata"),
		)

	private fun loadAliases(dataSource: DataSource): List<NutritionFoodAliasCatalogueRow> =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery(
					"""
					SELECT
						nf.source_name,
						nf.source_id,
						nfa.alias,
						nfa.normalized_alias
					FROM nutrition_food_aliases nfa
					JOIN nutrition_foods nf ON nf.id = nfa.food_id
					ORDER BY nfa.normalized_alias
					""".trimIndent(),
				).use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(
								NutritionFoodAliasCatalogueRow(
									foodIdentity = NutritionFoodIdentity(
										sourceName = resultSet.getString("source_name"),
										sourceId = resultSet.getString("source_id"),
									),
									alias = resultSet.getString("alias"),
									normalizedAlias = resultSet.getString("normalized_alias"),
								),
							)
						}
					}
				}
			}
		}

	private fun loadMeasures(dataSource: DataSource): List<NutritionFoodMeasureCatalogueRow> =
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery(
					"""
					SELECT
						nf.source_name,
						nf.source_id,
						nfm.measure_name,
						nfm.grams_per_measure
					FROM nutrition_food_measures nfm
					JOIN nutrition_foods nf ON nf.id = nfm.food_id
					ORDER BY nf.source_name, nf.source_id, nfm.measure_name
					""".trimIndent(),
				).use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(
								NutritionFoodMeasureCatalogueRow(
									foodIdentity = NutritionFoodIdentity(
										sourceName = resultSet.getString("source_name"),
										sourceId = resultSet.getString("source_id"),
									),
									measureName = resultSet.getString("measure_name"),
									gramsPerMeasure = resultSet.getBigDecimal("grams_per_measure"),
								),
							)
						}
					}
				}
			}
		}
}
