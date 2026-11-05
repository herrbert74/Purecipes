package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.sql.Statement
import javax.sql.DataSource
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test

class IngredientSuggestionToolTest {

	@Test
	fun generateSuggestionsFindsSynonymAndSuffixCandidatesWithSampleContext() {
		val db = createInMemoryDb("ingredient_suggestions_lexical")
		val dataSource = db.dataSource
		val yogurtFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_foundation",
			sourceId = "170903",
			displayName = "Yogurt, Greek, plain, whole milk",
			normalizedName = "yogurt greek plain whole milk",
		)
		insertAlias(dataSource, "greek yogurt", yogurtFoodId)
		val tomatoSauceFoodId = insertFood(
			dataSource = dataSource,
			sourceName = "fdc_sr_legacy",
			sourceId = "170495",
			displayName = "Tomato sauce, plain",
			normalizedName = "tomato sauce plain",
		)
		insertAlias(dataSource, "tomato sauce", tomatoSauceFoodId)

		insertRecipeLine(
			dataSource = dataSource,
			title = "Greek Lamb Flatbreads",
			sourceUrl = "https://example.com/recipes/flatbreads",
			rawText = "200 g Greek yoghurt",
		)
		insertRecipeLine(
			dataSource = dataSource,
			title = "Eggplant Parmigiana",
			sourceUrl = "https://example.com/recipes/parmigiana",
			rawText = "250 ml basic tomato sauce",
		)

		val snapshot = IngredientSnapshotData(
			names = listOf(
				IngredientNameSnapshotRow(
					lookupName = "basic tomato sauce",
					status = "auto",
					pantryItem = "",
					fdcSource = "",
					fdcId = "",
					fdcDescription = "",
					matchSource = "",
					lineCount = 1,
				),
				IngredientNameSnapshotRow(
					lookupName = "greek yoghurt",
					status = "auto",
					pantryItem = "Greek Yoghurt",
					fdcSource = "",
					fdcId = "",
					fdcDescription = "",
					matchSource = "",
					lineCount = 16,
				),
			),
			lines = emptyList(),
		)

		val suggestions = IngredientSuggestionTool(dataSource).generateSuggestions(snapshot = snapshot)
		suggestions shouldHaveSize 2
		val byName = suggestions.associateBy(IngredientSuggestionRow::lookupName)

		val tomatoSuggestion = byName.getValue("basic tomato sauce")
		tomatoSuggestion.fdcSource shouldBe "fdc_sr_legacy"
		tomatoSuggestion.fdcId shouldBe "170495"
		tomatoSuggestion.matchSource shouldBe "suffix_alias"
		tomatoSuggestion.sampleRecipeTitle shouldBe "Eggplant Parmigiana"
		tomatoSuggestion.sampleRawText shouldBe "250 ml basic tomato sauce"

		val yogurtSuggestion = byName.getValue("greek yoghurt")
		yogurtSuggestion.fdcSource shouldBe "fdc_foundation"
		yogurtSuggestion.fdcId shouldBe "170903"
		yogurtSuggestion.pantryItem shouldBe "Greek Yoghurt"
		yogurtSuggestion.matchSource shouldBe "synonym_alias"
		yogurtSuggestion.sampleRecipeTitle shouldBe "Greek Lamb Flatbreads"
	}

	@Test
	fun generateSuggestionsUsesRerankerAndAcceptsPinnedRowsIntoNamesTsv() {
		val db = createInMemoryDb("ingredient_suggestions_rerank_accept")
		val dataSource = db.dataSource
		insertFood(
			dataSource = dataSource,
			sourceName = "fdc_sr_legacy",
			sourceId = "173468",
			displayName = "Salt, table",
			normalizedName = "salt table",
		)
		insertFood(
			dataSource = dataSource,
			sourceName = "fdc_foundation",
			sourceId = "746775",
			displayName = "Salt, sea, coarse",
			normalizedName = "salt sea coarse",
		)
		insertRecipeLine(
			dataSource = dataSource,
			title = "Soft Pretzels",
			sourceUrl = "https://example.com/recipes/pretzels",
			rawText = "1 tbsp coarse pretzel salt",
		)

		val initialNames = listOf(
			IngredientNameSnapshotRow(
				lookupName = "coarse pretzel salt",
				status = "auto",
				pantryItem = "",
				fdcSource = "",
				fdcId = "",
				fdcDescription = "",
				matchSource = "",
				lineCount = 2,
			),
		)
		val reranker = IngredientSuggestionReranker { input ->
			val seaSalt = input.foodCandidates.first { it.fdcId == "746775" }
			IngredientSemanticRerankDecision(
				fdcSource = seaSalt.fdcSource,
				fdcId = seaSalt.fdcId,
				fdcDescription = seaSalt.fdcDescription,
				pantryItem = "Salt",
				confidence = "0.96",
				rationale = "Coarse sea salt matches pretzel topping salt",
			)
		}

		val suggestions = IngredientSuggestionTool(dataSource, reranker).generateSuggestions(
			snapshot = IngredientSnapshotData(names = initialNames, lines = emptyList()),
		)
		suggestions shouldHaveSize 1
		val suggestion = suggestions.single()
		suggestion.fdcSource shouldBe "fdc_foundation"
		suggestion.fdcId shouldBe "746775"
		suggestion.matchSource shouldBe "embedding"
		suggestion.pantryItem shouldBe "Salt"

		val tempDir = Files.createTempDirectory("purecipes-suggestions-accept")
		try {
			val namesFile = tempDir.resolve(IngredientSnapshotExporter.NAMES_FILE_NAME)
			namesFile.writeText(IngredientSnapshotExporter.formatNamesTsv(initialNames))
			val pinnedSuggestions = listOf(suggestion.copy(status = IngredientSnapshotExporter.STATUS_PINNED))
			IngredientSuggestionTool.writeSuggestionsFile(tempDir, pinnedSuggestions)

			val acceptedCount = IngredientSuggestionTool.acceptPinnedSuggestions(tempDir)
			acceptedCount shouldBe 1

			val updatedNames = IngredientSnapshotExporter.parseNamesTsv(namesFile.readText())
			updatedNames shouldBe listOf(
				IngredientNameSnapshotRow(
					lookupName = "coarse pretzel salt",
					status = "pinned",
					pantryItem = "Salt",
					fdcSource = "fdc_foundation",
					fdcId = "746775",
					fdcDescription = "Salt, sea, coarse",
					matchSource = "pinned",
					lineCount = 2,
				),
			)
		} finally {
			tempDir.toFile().deleteRecursively()
		}
	}
}

private fun insertFood(
	dataSource: DataSource,
	sourceName: String,
	sourceId: String,
	displayName: String,
	normalizedName: String,
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
			statement.setString(4, normalizedName)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next())
				keys.getInt(1)
			}
		}
	}

private fun insertAlias(
	dataSource: DataSource,
	normalizedAlias: String,
	foodId: Int,
) {
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
			INSERT INTO nutrition_food_aliases (alias, normalized_alias, food_id)
			VALUES (?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setString(1, normalizedAlias)
			statement.setString(2, normalizedAlias)
			statement.setInt(3, foodId)
			statement.executeUpdate()
		}
	}
}

private fun insertRecipeLine(
	dataSource: DataSource,
	title: String,
	sourceUrl: String,
	rawText: String,
) {
	dataSource.connection.use { connection ->
		val recipeId = connection.prepareStatement(
			"""
			INSERT INTO recipes (title, source_url, created_at)
			VALUES (?, ?, CURRENT_TIMESTAMP)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setString(1, title)
			statement.setString(2, sourceUrl)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next())
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
				check(keys.next())
				keys.getInt(1)
			}
		}
		connection.prepareStatement(
			"""
			INSERT INTO ingredients (ingredient_group_id, ingredient, order_index, requirement)
			VALUES (?, ?, 0, 'REQUIRED')
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, groupId)
			statement.setString(2, rawText)
			statement.executeUpdate()
		}
	}
}
