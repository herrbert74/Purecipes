package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal
import java.sql.Statement
import javax.sql.DataSource
import kotlin.test.Test

class IngredientSnapshotExporterTest {

	@Test
	fun collectBuildsSortedLinesAndNamesWithPantryAndFoodMatches() {
		val db = createInMemoryDb("ingredient_snapshot_collect")
		val dataSource = db.dataSource
		val oliveOilFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_foundation",
			sourceId = "171413",
			displayName = "Oil, olive, extra virgin",
		)
		val recipeId = insertScrapedRecipe(
			dataSource = dataSource,
			sourceUrl = "https://example.com/recipes/salad",
			groupName = "Dressing",
			ingredients = listOf(
				SnapshotTestIngredient("For the dressing:"),
				SnapshotTestIngredient("Large mixing bowl"),
				SnapshotTestIngredient("Preheat oven to 180C"),
				SnapshotTestIngredient("2 tbsp extra-virgin olive oil"),
				SnapshotTestIngredient("1 tsp olive oil", requirement = "OPTIONAL"),
			),
		)
		insertScrapedRecipe(
			dataSource = dataSource,
			sourceUrl = null,
			groupName = null,
			ingredients = listOf(SnapshotTestIngredient("1 cup flour")),
		)
		val ingredientIds = loadIngredientIds(dataSource, recipeId)
		insertMeasurementAndMatch(
			dataSource = dataSource,
			ingredientId = ingredientIds[3],
			rawText = "2 tbsp extra-virgin olive oil",
			quantity = BigDecimal("2.0000"),
			unit = "tbsp",
			parsedName = "olive oil",
			foodId = oliveOilFoodId,
			matchSource = "alias",
		)
		insertMeasurementAndMatch(
			dataSource = dataSource,
			ingredientId = ingredientIds[4],
			rawText = "1 tsp olive oil",
			quantity = BigDecimal("1.0000"),
			unit = "tsp",
			parsedName = "olive oil",
			foodId = oliveOilFoodId,
			matchSource = "alias",
		)

		val snapshot = IngredientSnapshotExporter(dataSource).collect()

		snapshot.lines shouldHaveSize 5
		snapshot.lines.map(IngredientLineSnapshotRow::kind) shouldBe listOf(
			"heading",
			"equipment",
			"note",
			"ingredient",
			"ingredient",
		)
		snapshot.lines[0].lookupName shouldBe ""
		snapshot.lines[3] shouldBe IngredientLineSnapshotRow(
			sourceUrl = "https://example.com/recipes/salad",
			groupIndex = 0,
			lineIndex = 3,
			rawText = "2 tbsp extra-virgin olive oil",
			kind = "ingredient",
			groupName = "Dressing",
			quantity = "2",
			unit = "tbsp",
			lookupName = "olive oil",
			requirement = "REQUIRED",
			alternativeGroupKey = "",
		)

		snapshot.names shouldBe listOf(
			IngredientNameSnapshotRow(
				lookupName = "olive oil",
				status = "auto",
				pantryItem = "Olive Oil",
				fdcSource = "fdc_foundation",
				fdcId = "171413",
				fdcDescription = "Oil, olive, extra virgin",
				matchSource = "alias",
				lineCount = 2,
			),
		)
	}

	@Test
	fun collectPreservesPinnedRowsAndUpdatesLineCount() {
		val db = createInMemoryDb("ingredient_snapshot_pinned")
		val dataSource = db.dataSource
		val recipeId = insertScrapedRecipe(
			dataSource = dataSource,
			sourceUrl = "https://example.com/recipes/toast",
			groupName = null,
			ingredients = listOf(SnapshotTestIngredient("100 g halloumi")),
		)
		val ingredientIds = loadIngredientIds(dataSource, recipeId)
		val autoFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_branded",
			sourceId = "999",
			displayName = "Branded halloumi",
		)
		insertMeasurementAndMatch(
			dataSource = dataSource,
			ingredientId = ingredientIds[0],
			rawText = "100 g halloumi",
			quantity = BigDecimal("100"),
			unit = "g",
			parsedName = "halloumi",
			foodId = autoFoodId,
			matchSource = "tokens",
		)

		val existingPinned = listOf(
			IngredientNameSnapshotRow(
				lookupName = "halloumi",
				status = "pinned",
				pantryItem = "Halloumi",
				fdcSource = "fdc_foundation",
				fdcId = "12345",
				fdcDescription = "Cheese, halloumi",
				matchSource = "pinned",
				lineCount = 0,
			),
		)

		val snapshot = IngredientSnapshotExporter(dataSource).collect(existingNames = existingPinned)

		snapshot.names shouldBe listOf(
			existingPinned.single().copy(lineCount = 1),
		)
	}

	@Test
	fun formatAndParseRoundTripAndDiffDetectsChanges() {
		val beforeName = IngredientNameSnapshotRow(
			lookupName = "olive oil",
			status = "auto",
			pantryItem = "Olive Oil",
			fdcSource = "fdc_foundation",
			fdcId = "171413",
			fdcDescription = "Oil, olive, extra virgin",
			matchSource = "alias",
			lineCount = 1,
		)
		val beforeLine = IngredientLineSnapshotRow(
			sourceUrl = "https://example.com/recipes/salad",
			groupIndex = 0,
			lineIndex = 0,
			rawText = "2 tbsp olive oil",
			kind = "ingredient",
			groupName = "",
			quantity = "2",
			unit = "tbsp",
			lookupName = "olive oil",
			requirement = "REQUIRED",
			alternativeGroupKey = "",
		)
		val namesTsv = IngredientSnapshotExporter.formatNamesTsv(listOf(beforeName))
		val linesTsv = IngredientSnapshotExporter.formatLinesTsv(listOf(beforeLine))

		IngredientSnapshotExporter.parseNamesTsv(namesTsv) shouldBe listOf(beforeName)
		IngredientSnapshotExporter.parseLinesTsv(linesTsv) shouldBe listOf(beforeLine)

		val expected = IngredientSnapshotData(names = listOf(beforeName), lines = listOf(beforeLine))
		val actual = IngredientSnapshotData(
			names = listOf(beforeName.copy(fdcId = "999999", fdcDescription = "Oil, other")),
			lines = listOf(beforeLine.copy(quantity = "3")),
		)

		val diff = IngredientSnapshotExporter.diff(expected = expected, actual = actual)

		diff.isEmpty shouldBe false
		diff.changedNames shouldHaveSize 1
		diff.changedLines shouldHaveSize 1
		val summary = diff.formatSummary()
		summary shouldContain "Names: +0 added, -0 removed, ~1 changed"
		summary shouldContain "Lines: +0 added, -0 removed, ~1 changed"
		summary shouldContain "olive oil"
	}

	@Test
	fun seedPinsPromotesConfirmedMatchesAndLeavesTokenGuessesAuto() {
		val db = createInMemoryDb("ingredient_snapshot_seed_pins")
		val dataSource = db.dataSource
		val oliveOilFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_sr_legacy",
			sourceId = "171413",
			displayName = "Oil, olive, salad or cooking",
		)
		val brandedGuessFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_branded",
			sourceId = "999999",
			displayName = "RANDOM BRANDED SAUCE",
		)
		val recipeId = insertScrapedRecipe(
			dataSource = dataSource,
			sourceUrl = "https://example.com/recipes/pins",
			groupName = null,
			ingredients = listOf(
				SnapshotTestIngredient("2 tbsp olive oil"),
				SnapshotTestIngredient("1 tbsp mystery sauce"),
			),
		)
		val ingredientIds = loadIngredientIds(dataSource, recipeId)
		insertMeasurementAndMatch(
			dataSource = dataSource,
			ingredientId = ingredientIds[0],
			rawText = "2 tbsp olive oil",
			quantity = BigDecimal("2.0000"),
			unit = "tbsp",
			parsedName = "olive oil",
			foodId = oliveOilFoodId,
			matchSource = "alias",
		)
		insertMeasurementAndMatch(
			dataSource = dataSource,
			ingredientId = ingredientIds[1],
			rawText = "1 tbsp mystery sauce",
			quantity = BigDecimal("1.0000"),
			unit = "tbsp",
			parsedName = "mystery sauce",
			foodId = brandedGuessFoodId,
			matchSource = "tokens",
		)

		val seeded = IngredientSnapshotExporter(dataSource).collect(seedPins = true)
		val byName = seeded.names.associateBy(IngredientNameSnapshotRow::lookupName)

		byName.getValue("olive oil").status shouldBe "pinned"
		byName.getValue("olive oil").matchSource shouldBe "pinned"
		byName.getValue("mystery sauce").status shouldBe "auto"
		byName.getValue("mystery sauce").matchSource shouldBe "tokens"
	}
}

private data class SnapshotTestIngredient(
	val text: String,
	val requirement: String = "REQUIRED",
)

private fun insertScrapedRecipe(
	dataSource: DataSource,
	sourceUrl: String?,
	groupName: String?,
	ingredients: List<SnapshotTestIngredient>,
): Int =
	dataSource.connection.use { connection ->
		val recipeId = connection.prepareStatement(
			"""
			INSERT INTO recipes (title, source_url, created_at)
			VALUES ('Test recipe', ?, CURRENT_TIMESTAMP)
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
			INSERT INTO ingredients (ingredient_group_id, ingredient, order_index, requirement)
			VALUES (?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			ingredients.forEachIndexed { index, ingredient ->
				statement.setInt(1, groupId)
				statement.setString(2, ingredient.text)
				statement.setInt(3, index)
				statement.setString(4, ingredient.requirement)
				statement.addBatch()
			}
			statement.executeBatch()
		}
		recipeId
	}

private fun loadIngredientIds(dataSource: DataSource, recipeId: Int): List<Int> =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			SELECT i.id
			FROM ingredients i
			JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
			WHERE ig.recipe_id = ?
			ORDER BY i.order_index
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use { rs ->
				buildList {
					while (rs.next()) {
						add(rs.getInt("id"))
					}
				}
			}
		}
	}

private fun insertFood(
	dataSource: DataSource,
	sourceName: String,
	sourceId: String,
	displayName: String,
): Int =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			INSERT INTO nutrition_foods (
				source_name,
				source_id,
				display_name,
				normalized_name,
				calories_per_100g
			)
			VALUES (?, ?, ?, ?, 100)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setString(1, sourceName)
			statement.setString(2, sourceId)
			statement.setString(3, displayName)
			statement.setString(4, displayName.lowercase())
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next())
				keys.getInt(1)
			}
		}
	}

private fun insertMeasurementAndMatch(
	dataSource: DataSource,
	ingredientId: Int,
	rawText: String,
	quantity: BigDecimal,
	unit: String,
	parsedName: String,
	foodId: Int,
	matchSource: String,
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
			)
			VALUES (?, ?, ?, ?, ?, TRUE)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, ingredientId)
			statement.setString(2, rawText)
			statement.setBigDecimal(3, quantity)
			statement.setString(4, unit)
			statement.setString(5, parsedName)
			statement.executeUpdate()
		}

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
				match_source
			)
			VALUES (?, ?, ?, ?, ?, ?, 1.0, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, ingredientId)
			statement.setString(2, rawText)
			statement.setBigDecimal(3, quantity)
			statement.setString(4, unit)
			statement.setString(5, parsedName)
			statement.setInt(6, foodId)
			statement.setString(7, matchSource)
			statement.executeUpdate()
		}
	}
}
