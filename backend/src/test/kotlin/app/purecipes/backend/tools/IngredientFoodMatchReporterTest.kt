package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.sql.Statement
import javax.sql.DataSource
import kotlin.test.Test

class IngredientFoodMatchReporterTest {

	@Test
	fun collectClassifiesParseMatchAndGramGaps() {
		val db = createInMemoryDb("ingredient_food_match_gaps")
		val dataSource = db.dataSource
		val foodId = insertFood(dataSource, sourceId = "1", displayName = "Oil, olive, extra virgin")
		val recipeId = insertRecipe(
			dataSource = dataSource,
			title = "Gap recipe",
			ingredients = listOf(
				ReportTestIngredient("never parsed line"),
				ReportTestIngredient("Salt to taste"),
				ReportTestIngredient("2 cups mystery spice"),
				ReportTestIngredient("1 cup olive oil"),
				ReportTestIngredient("1 cup sugar"),
				ReportTestIngredient("1 tbsp olive"),
			),
		)
		val ingredientIds = loadIngredientIds(dataSource, recipeId)

		insertMeasurement(
			dataSource,
			MeasurementInsert(
				ingredientId = ingredientIds[1],
				rawText = "Salt to taste",
				parsedName = "Salt to taste",
				isMeasurable = false,
			),
		)
		insertMeasurement(
			dataSource,
			MeasurementInsert(
				ingredientId = ingredientIds[2],
				rawText = "2 cups mystery spice",
				quantity = BigDecimal("2"),
				unit = "cup",
				parsedName = "mystery spice",
				isMeasurable = true,
			),
		)
		insertMeasurement(
			dataSource,
			MeasurementInsert(
				ingredientId = ingredientIds[3],
				rawText = "1 cup olive oil",
				quantity = BigDecimal.ONE,
				unit = "cup",
				parsedName = "olive oil",
				isMeasurable = true,
			),
		)
		insertMatch(
			dataSource,
			MatchInsert(
				ingredientId = ingredientIds[3],
				parsedName = "olive oil",
				unit = "cup",
				foodId = foodId,
				confidence = BigDecimal("0.90"),
				matchSource = "name",
			),
		)
		insertMeasurement(
			dataSource,
			MeasurementInsert(
				ingredientId = ingredientIds[4],
				rawText = "1 cup sugar",
				quantity = BigDecimal.ONE,
				unit = "cup",
				parsedName = "sugar",
				isMeasurable = true,
			),
		)
		insertMatch(
			dataSource,
			MatchInsert(
				ingredientId = ingredientIds[4],
				parsedName = "sugar",
				unit = "cup",
				foodId = foodId,
				confidence = BigDecimal("1.00"),
				matchSource = "alias",
			),
		)
		insertContribution(dataSource, ingredientIds[4], grams = BigDecimal("200"), gramsSource = "density")
		insertMeasurement(
			dataSource,
			MeasurementInsert(
				ingredientId = ingredientIds[5],
				rawText = "1 tbsp olive",
				quantity = BigDecimal.ONE,
				unit = "tbsp",
				parsedName = "olive",
				isMeasurable = true,
			),
		)
		insertMatch(
			dataSource,
			MatchInsert(
				ingredientId = ingredientIds[5],
				parsedName = "olive",
				unit = "tbsp",
				foodId = foodId,
				confidence = BigDecimal("0.75"),
				matchSource = "name",
			),
		)
		insertContribution(dataSource, ingredientIds[5], grams = BigDecimal("14"), gramsSource = "measure")

		val report = IngredientFoodMatchReporter(dataSource).collect()

		report.countableLines shouldBe 6
		report.neverParsedCount shouldBe 1
		report.notMeasurableCount shouldBe 1
		report.noFoodCount shouldBe 1
		report.noGramsCount shouldBe 1
		report.matchedCount shouldBe 2
		report.measureGramCount shouldBe 1
		report.densityGramCount shouldBe 1
		report.weakMatchCount shouldBe 1
		report.neverParsedNames.map { it.label } shouldContain "never parsed line"
		report.notMeasurableNames.map { it.label } shouldContain "Salt to taste"
		report.unmatchedNames.map { it.label } shouldContain "mystery spice"
		report.unresolvedGramsNames.map { it.label } shouldContain "olive oil (cup)"
		report.weakMatches.map { it.label } shouldContain "olive -> Oil, olive, extra virgin"
		report.missingNutritionCount shouldBe 1
	}

	@Test
	fun collectSkipsIgnorableOptionalAndDuplicateAlternatives() {
		val db = createInMemoryDb("ingredient_food_match_skips")
		val dataSource = db.dataSource
		val recipeId = insertRecipe(
			dataSource = dataSource,
			title = "Skip recipe",
			ingredients = listOf(
				ReportTestIngredient("For the sauce:"),
				ReportTestIngredient("optional parsley", requirement = "OPTIONAL"),
				ReportTestIngredient("2 tbsp parsley", requirement = "ALTERNATIVE", alternativeGroupKey = 1),
				ReportTestIngredient("2 tbsp tarragon", requirement = "ALTERNATIVE", alternativeGroupKey = 1),
			),
		)
		insertCalculatedNutrition(dataSource, recipeId, isComplete = false)

		val report = IngredientFoodMatchReporter(dataSource).collect()

		report.countableLines shouldBe 1
		report.neverParsedCount shouldBe 1
		report.neverParsedNames.single().label shouldBe "2 tbsp parsley"
		report.calculatedNutritionCount shouldBe 1
		report.partialEstimateCount shouldBe 1
		report.completeEstimateCount shouldBe 0
	}

	@Test
	fun collectListsScrapedNutritionRecipes() {
		val db = createInMemoryDb("ingredient_food_match_scraped")
		val dataSource = db.dataSource
		val recipeId = insertRecipe(
			dataSource = dataSource,
			title = "Scraped pie",
			ingredients = listOf(ReportTestIngredient("1 cup sugar")),
		)
		insertScrapedNutrition(dataSource, recipeId)

		val report = IngredientFoodMatchReporter(dataSource).collect()

		report.scrapedNutritionCount shouldBe 1
		report.scrapedRecipes.single() shouldBe "Recipe $recipeId: Scraped pie"
		report.partialEstimateCount shouldBe 1
	}

	@Test
	fun formatIncludesSummaryHeadings() {
		val db = createInMemoryDb("ingredient_food_match_format")
		val text = IngredientFoodMatchReporter(db.dataSource).format(
			IngredientFoodMatchReporter(db.dataSource).collect(),
		)

		text.lines().take(10) shouldBe listOf(
			"Ingredient food-table matching report",
			"Countable ingredient lines: 0",
			"Never parsed: 0",
			"Not measurable: 0",
			"No food match: 0",
			"No gram weight: 0",
			"Matched: 0",
			"  Gram source mass: 0",
			"  Gram source measure: 0",
			"  Gram source density fallback: 0",
		)
	}
}

private data class ReportTestIngredient(
	val text: String,
	val requirement: String = "REQUIRED",
	val alternativeGroupKey: Int? = null,
)

private data class MeasurementInsert(
	val ingredientId: Int,
	val rawText: String,
	val parsedName: String,
	val isMeasurable: Boolean,
	val quantity: BigDecimal? = null,
	val unit: String? = null,
)

private data class MatchInsert(
	val ingredientId: Int,
	val parsedName: String,
	val unit: String,
	val foodId: Int,
	val confidence: BigDecimal,
	val matchSource: String,
)

private fun insertRecipe(
	dataSource: DataSource,
	title: String,
	ingredients: List<ReportTestIngredient>,
): Int =
	dataSource.connection.use { connection ->
		val recipeId = connection.prepareStatement(
			"""
			INSERT INTO recipes (title, created_at)
			VALUES (?, CURRENT_TIMESTAMP)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setString(1, title)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next()) { "Recipe insert did not return generated id" }
				keys.getInt(1)
			}
		}

		val groupId = connection.prepareStatement(
			"""
			INSERT INTO ingredient_groups (recipe_id, name, order_index)
			VALUES (?, NULL, 0)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next()) { "Ingredient group insert did not return generated id" }
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
			ingredients.forEachIndexed { index, ingredient ->
				statement.setInt(1, groupId)
				statement.setString(2, ingredient.text)
				statement.setInt(3, index)
				statement.setString(4, ingredient.requirement)
				if (ingredient.alternativeGroupKey == null) {
					statement.setObject(5, null)
				} else {
					statement.setInt(5, ingredient.alternativeGroupKey)
				}
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
			statement.executeQuery().use { resultSet ->
				buildList {
					while (resultSet.next()) {
						add(resultSet.getInt("id"))
					}
				}
			}
		}
	}

private fun insertFood(
	dataSource: DataSource,
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
			VALUES ('Foundation', ?, ?, ?, 100)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setString(1, sourceId)
			statement.setString(2, displayName)
			statement.setString(3, displayName.lowercase())
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next()) { "Food insert did not return generated id" }
				keys.getInt(1)
			}
		}
	}

private fun insertMeasurement(
	dataSource: DataSource,
	measurement: MeasurementInsert,
): Unit =
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
			VALUES (?, ?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, measurement.ingredientId)
			statement.setString(2, measurement.rawText)
			statement.setBigDecimal(3, measurement.quantity)
			statement.setString(4, measurement.unit)
			statement.setString(5, measurement.parsedName)
			statement.setBoolean(6, measurement.isMeasurable)
			statement.executeUpdate()
		}
	}

private fun insertMatch(
	dataSource: DataSource,
	match: MatchInsert,
): Unit =
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
				match_source
			)
			VALUES (?, ?, 1, ?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, match.ingredientId)
			statement.setString(2, match.parsedName)
			statement.setString(3, match.unit)
			statement.setString(4, match.parsedName)
			statement.setInt(5, match.foodId)
			statement.setBigDecimal(6, match.confidence)
			statement.setString(7, match.matchSource)
			statement.executeUpdate()
		}
	}

private fun insertContribution(
	dataSource: DataSource,
	ingredientId: Int,
	grams: BigDecimal,
	gramsSource: String? = null,
): Unit =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			INSERT INTO ingredient_nutrition_contributions (ingredient_id, grams_resolved, grams_source)
			VALUES (?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, ingredientId)
			statement.setBigDecimal(2, grams)
			statement.setString(3, gramsSource)
			statement.executeUpdate()
		}
	}

private fun insertCalculatedNutrition(
	dataSource: DataSource,
	recipeId: Int,
	isComplete: Boolean,
): Unit =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			INSERT INTO nutrition (recipe_id, calories, calculation_source, is_complete)
			VALUES (?, 100, 'calculated', ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.setBoolean(2, isComplete)
			statement.executeUpdate()
		}
	}

private fun insertScrapedNutrition(dataSource: DataSource, recipeId: Int): Unit =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			INSERT INTO nutrition (recipe_id, calories, calculation_source)
			VALUES (?, 256, 'scraped')
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
		}
	}
