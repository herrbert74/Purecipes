package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.sql.Statement
import java.sql.Types
import javax.sql.DataSource
import kotlin.io.path.writeText
import kotlin.test.Test

class IngredientDataRebuilderTest {

	@Test
	fun rebuildRepairsOverSplitAlternativeClusterAndPreservesRecipeId() {
		val db = createInMemoryDb("ingredient_rebuilder_cluster")
		val dataSource = db.dataSource

		val scrapedRecipeId = insertRecipeWithIngredients(
			dataSource = dataSource,
			sourceUrl = "https://www.seriouseats.com/juicy-less-meat-meatloaf-recipe-8782274",
			groupName = null,
			items = listOf(
				ScrapedIngredientItem(
					text = "1 pound (450 g) ground beef, pork, veal,",
					requirement = "ALTERNATIVE",
					alternativeGroupKey = 1,
				),
				ScrapedIngredientItem(
					text = "1 pound any combination",
					requirement = "ALTERNATIVE",
					alternativeGroupKey = 1,
				),
			),
		)
		val userRecipeId = insertRecipeWithIngredients(
			dataSource = dataSource,
			sourceUrl = null,
			groupName = null,
			items = listOf(
				ScrapedIngredientItem(
					text = "1 pound any combination",
					requirement = "ALTERNATIVE",
					alternativeGroupKey = 1,
				),
			),
		)

		val recalculatedRecipeIds = mutableListOf<Int>()
		val summary = IngredientDataRebuilder(
			dataSource = dataSource,
			recalculateNutrition = recalculatedRecipeIds::addAll,
		).rebuild(jsonArchiveDir = null)

		summary shouldBe IngredientDataRebuildSummary(
			scrapedRecipeCount = 1,
			matchedJsonRecipeCount = 0,
			updatedLineRecipeCount = 1,
			syncedJsonFileCount = 0,
			recalculatedNutritionRecipeCount = 1,
		)
		recalculatedRecipeIds shouldBe listOf(scrapedRecipeId)

		loadRecipeIds(dataSource) shouldBe listOf(scrapedRecipeId, userRecipeId)

		val snapshot = IngredientSnapshotExporter(dataSource).collect()
		snapshot.lines shouldHaveSize 1
		snapshot.lines.single() shouldBe IngredientLineSnapshotRow(
			sourceUrl = "https://www.seriouseats.com/juicy-less-meat-meatloaf-recipe-8782274",
			groupIndex = 0,
			lineIndex = 0,
			rawText = "1 pound (450 g) ground beef, pork, veal, or any combination",
			kind = "ingredient",
			groupName = "",
			quantity = "1",
			unit = "lb",
			lookupName = "beef",
			requirement = "REQUIRED",
			alternativeGroupKey = "",
		)
		snapshot.names.single().lookupName shouldBe "beef"
		snapshot.names.single().pantryItem shouldBe "Beef"
		loadIngredientTexts(dataSource, userRecipeId) shouldBe listOf("1 pound any combination")
	}

	@Test
	fun rebuildUsesRawJsonArchiveBySourceUrlWhileKeepingDatabaseRecipeId() {
		val db = createInMemoryDb("ingredient_rebuilder_json")
		val dataSource = db.dataSource

		val existingRecipeId = insertRecipeWithIngredients(
			dataSource = dataSource,
			sourceUrl = "https://example.com/recipes/meatloaf",
			groupName = null,
			items = listOf(
				ScrapedIngredientItem(text = "stale ingredient line"),
			),
		)

		val tempDir = Files.createTempDirectory("purecipes-json-archive")
		try {
			tempDir.resolve("99999-meatloaf.json").writeText(
				"""
				{
					"title": "Meatloaf",
					"source_url": "https://example.com/recipes/meatloaf",
					"ingredient_groups": [
						{
							"name": "For the loaf:",
							"ingredients": [
								{
									"text": "1 pound (450 g) ground beef, pork, veal,",
									"requirement": "ALTERNATIVE",
									"alternativeGroupKey": 1
								},
								{
									"text": "1 pound any combination",
									"requirement": "ALTERNATIVE",
									"alternativeGroupKey": 1
								}
							]
						}
					]
				}
				""".trimIndent(),
			)

			val recalculatedRecipeIds = mutableListOf<Int>()
			val summary = IngredientDataRebuilder(
				dataSource = dataSource,
				recalculateNutrition = recalculatedRecipeIds::addAll,
			).rebuild(
				jsonArchiveDir = tempDir,
				useJsonAsInput = true,
				syncJsonArchive = true,
			)

			summary shouldBe IngredientDataRebuildSummary(
				scrapedRecipeCount = 1,
				matchedJsonRecipeCount = 1,
				updatedLineRecipeCount = 1,
				syncedJsonFileCount = 1,
				recalculatedNutritionRecipeCount = 1,
			)
			recalculatedRecipeIds shouldBe listOf(existingRecipeId)
			loadRecipeIds(dataSource) shouldBe listOf(existingRecipeId)
			loadIngredientTexts(dataSource, existingRecipeId) shouldBe listOf(
				"1 pound (450 g) ground beef, pork, veal, or any combination",
			)
		} finally {
			tempDir.toFile().deleteRecursively()
		}
	}

	private fun insertRecipeWithIngredients(
		dataSource: DataSource,
		sourceUrl: String?,
		groupName: String?,
		items: List<ScrapedIngredientItem>,
	): Int =
		dataSource.connection.use { connection ->
			val recipeId = connection.prepareStatement(
				"""
				INSERT INTO recipes (title, instructions, source_url)
				VALUES ('Test Recipe', 'Mix and bake.', ?)
				""".trimIndent(),
				Statement.RETURN_GENERATED_KEYS,
			).use { statement ->
				statement.setString(1, sourceUrl)
				statement.executeUpdate()
				statement.generatedKeys.use { keys ->
					check(keys.next())
					keys.getInt(1)
				}
			}

			val groupId = connection.prepareStatement(
				"""
				INSERT INTO ingredient_groups (recipe_id, name, order_index)
				VALUES (?, ?, 0)
				""".trimIndent(),
				Statement.RETURN_GENERATED_KEYS,
			).use { statement ->
				statement.setInt(1, recipeId)
				statement.setString(2, groupName)
				statement.executeUpdate()
				statement.generatedKeys.use { keys ->
					check(keys.next())
					keys.getInt(1)
				}
			}

			connection.prepareStatement(
				"""
				INSERT INTO ingredients (
					ingredient_group_id,
					ingredient,
					order_index,
					requirement,
					alternative_group_key
				)
				VALUES (?, ?, ?, ?, ?)
				""".trimIndent(),
			).use { statement ->
				items.forEachIndexed { index, item ->
					statement.setInt(1, groupId)
					statement.setString(2, item.text)
					statement.setInt(3, index)
					statement.setString(4, item.requirement)
					if (item.alternativeGroupKey == null) {
						statement.setNull(5, Types.INTEGER)
					} else {
						statement.setInt(5, item.alternativeGroupKey)
					}
					statement.addBatch()
				}
				statement.executeBatch()
			}
			recipeId
		}

	private fun loadRecipeIds(dataSource: DataSource): List<Int> =
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT id FROM recipes ORDER BY id").use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.getInt("id"))
						}
					}
				}
			}
		}

	private fun loadIngredientTexts(dataSource: DataSource, recipeId: Int): List<String> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT i.ingredient
				FROM ingredient_groups ig
				JOIN ingredients i ON i.ingredient_group_id = ig.id
				WHERE ig.recipe_id = ?
				ORDER BY ig.order_index, i.order_index
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, recipeId)
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.getString("ingredient"))
						}
					}
				}
			}
		}
}
