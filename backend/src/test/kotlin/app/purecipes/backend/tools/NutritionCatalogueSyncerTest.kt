package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import app.purecipes.backend.feature.nutrition.NutritionLookupRepository
import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.sql.Statement
import javax.sql.DataSource
import kotlin.test.Test

class NutritionCatalogueSyncerTest {

	@Test
	fun syncTransfersFoodsAliasesAndMeasuresAcrossMismatchedFoodIdsAndPreservesExistingTargetIds() {
		val sourceDb = createInMemoryDb("nutrition_sync_source")
		val targetDb = createInMemoryDb("nutrition_sync_target")

		val targetPreexistingDummyId = insertSampleFood(
			dataSource = targetDb.dataSource,
			sourceName = "fdc_sr_legacy",
			sourceId = "999999",
			description = "Target-only padding food",
			calories = "10.00",
		)
		val targetOliveOilId = insertSampleFood(
			dataSource = targetDb.dataSource,
			sourceName = "fdc_foundation",
			sourceId = "171413",
			description = "Oil, olive, stale name",
			calories = "800.00",
		)
		insertSampleAlias(targetDb.dataSource, targetOliveOilId, "evoo")
		insertSampleMeasure(targetDb.dataSource, targetOliveOilId, "tbsp", BigDecimal("13.0000"))

		val sourceOliveOilId = insertSampleFood(
			dataSource = sourceDb.dataSource,
			sourceName = "fdc_foundation",
			sourceId = "171413",
			description = "Oil, olive, extra virgin",
			calories = "884.00",
		)
		insertSampleAlias(sourceDb.dataSource, sourceOliveOilId, "olive oil")
		insertSampleAlias(sourceDb.dataSource, sourceOliveOilId, "EVOO")
		insertSampleMeasure(sourceDb.dataSource, sourceOliveOilId, "tbsp", BigDecimal("13.5000"))
		insertSampleMeasure(sourceDb.dataSource, sourceOliveOilId, "cup", BigDecimal("216.0000"))

		val sourceHalloumiId = insertSampleFood(
			dataSource = sourceDb.dataSource,
			sourceName = "fdc_branded",
			sourceId = "250001",
			description = "Cheese, halloumi",
			calories = "321.00",
		)
		insertSampleAlias(sourceDb.dataSource, sourceHalloumiId, "halloumi")
		insertSampleMeasure(sourceDb.dataSource, sourceHalloumiId, "oz", BigDecimal("28.3495"))

		sourceOliveOilId shouldBe targetPreexistingDummyId
		sourceOliveOilId shouldNotBe targetOliveOilId

		val syncer = NutritionCatalogueSyncer(
			sourceDataSource = sourceDb.dataSource,
			targetDataSource = targetDb.dataSource,
		)

		val dryRunSummary = syncer.sync(dryRun = true)
		dryRunSummary.dryRun shouldBe true
		dryRunSummary.insertedFoodCount shouldBe 1
		dryRunSummary.updatedFoodCount shouldBe 1
		dryRunSummary.insertedAliasCount shouldBe 2
		dryRunSummary.updatedAliasCount shouldBe 1
		dryRunSummary.insertedMeasureCount shouldBe 2
		dryRunSummary.updatedMeasureCount shouldBe 1

		val applySummary = syncer.sync(dryRun = false)
		applySummary.dryRun shouldBe false
		applySummary.insertedFoodCount shouldBe 1
		applySummary.updatedFoodCount shouldBe 1
		applySummary.insertedAliasCount shouldBe 2
		applySummary.updatedAliasCount shouldBe 1
		applySummary.insertedMeasureCount shouldBe 2
		applySummary.updatedMeasureCount shouldBe 1

		findFoodId(targetDb.dataSource, "fdc_foundation", "171413") shouldBe targetOliveOilId

		val lookupIndex = NutritionLookupRepository(
			dataSource = targetDb.dataSource,
			foodPinsProvider = { emptyMap() },
		).loadIndex()

		val syncedOliveOilMatch = lookupIndex.findFood("olive oil")
		syncedOliveOilMatch shouldNotBe null
		val syncedOliveOil = lookupIndex.food(syncedOliveOilMatch!!.foodId)
		syncedOliveOil?.id shouldBe targetOliveOilId
		syncedOliveOil?.displayName shouldBe "Oil, olive, extra virgin"
		syncedOliveOil!!.nutrients.calories.shouldBeEqualComparingTo(BigDecimal("884.00"))
		val oliveOilMeasures = lookupIndex.measuresForFood(targetOliveOilId)
		oliveOilMeasures["tbsp"]!!.shouldBeEqualComparingTo(BigDecimal("13.5000"))
		oliveOilMeasures["cup"]!!.shouldBeEqualComparingTo(BigDecimal("216.0000"))

		val syncedHalloumiMatch = lookupIndex.findFood("halloumi")
		syncedHalloumiMatch shouldNotBe null
		val syncedHalloumi = lookupIndex.food(syncedHalloumiMatch!!.foodId)
		syncedHalloumi?.displayName shouldBe "Cheese, halloumi"
		val halloumiMeasures = lookupIndex.measuresForFood(syncedHalloumiMatch.foodId)
		halloumiMeasures["oz"]!!.shouldBeEqualComparingTo(BigDecimal("28.3495"))

		val idempotentSummary = syncer.sync(dryRun = false)
		idempotentSummary.insertedFoodCount shouldBe 0
		idempotentSummary.updatedFoodCount shouldBe 0
		idempotentSummary.insertedAliasCount shouldBe 0
		idempotentSummary.updatedAliasCount shouldBe 0
		idempotentSummary.insertedMeasureCount shouldBe 0
		idempotentSummary.updatedMeasureCount shouldBe 0
	}

	private fun insertSampleFood(
		dataSource: DataSource,
		sourceName: String,
		sourceId: String,
		description: String,
		calories: String,
	): Int =
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
					source_metadata
				) VALUES (?, ?, ?, ?, ?, 1.00, 2.00, 3.00, 0.50, 0.20, 5.00, '{}')
				""".trimIndent(),
				Statement.RETURN_GENERATED_KEYS,
			).use { statement ->
				statement.setString(1, sourceName)
				statement.setString(2, sourceId)
				statement.setString(3, description)
				statement.setString(4, NutritionNameNormalizer.normalize(description))
				statement.setBigDecimal(5, BigDecimal(calories))
				statement.executeUpdate()
				statement.generatedKeys.use { keys ->
					keys.next()
					keys.getInt(1)
				}
			}
		}

	private fun insertSampleAlias(dataSource: DataSource, foodId: Int, alias: String) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"INSERT INTO nutrition_food_aliases (food_id, alias, normalized_alias) VALUES (?, ?, ?)",
			).use { statement ->
				statement.setInt(1, foodId)
				statement.setString(2, alias)
				statement.setString(3, NutritionNameNormalizer.normalize(alias))
				statement.executeUpdate()
			}
		}
	}

	private fun insertSampleMeasure(
		dataSource: DataSource,
		foodId: Int,
		measureName: String,
		gramsPerMeasure: BigDecimal,
	) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"INSERT INTO nutrition_food_measures (food_id, measure_name, grams_per_measure) VALUES (?, ?, ?)",
			).use { statement ->
				statement.setInt(1, foodId)
				statement.setString(2, measureName)
				statement.setBigDecimal(3, gramsPerMeasure)
				statement.executeUpdate()
			}
		}
	}

	private fun findFoodId(dataSource: DataSource, sourceName: String, sourceId: String): Int =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"SELECT id FROM nutrition_foods WHERE source_name = ? AND source_id = ?",
			).use { statement ->
				statement.setString(1, sourceName)
				statement.setString(2, sourceId)
				statement.executeQuery().use { resultSet ->
					resultSet.next()
					resultSet.getInt("id")
				}
			}
		}
}
