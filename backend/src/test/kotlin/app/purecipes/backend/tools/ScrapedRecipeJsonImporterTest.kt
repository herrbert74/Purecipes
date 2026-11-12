package app.purecipes.backend.tools

import app.purecipes.backend.createInMemoryDb
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test

class ScrapedRecipeJsonImporterTest {

	@Test
	fun importDirectoryInsertsNewRecipesSkipsExistingByDefaultAndUpsertsWhenRequested() {
		val db = createInMemoryDb("scraped_json_importer")
		val dataSource = db.dataSource
		val archiveDir = Files.createTempDirectory("scraped_json_archive")
		val siteDir = Files.createDirectories(archiveDir.resolve("allrecipes"))

		val jsonFile = siteDir.resolve("101-lemon-chicken.json")
		jsonFile.writeText(
			"""
			{
			  "title": "Roasted Lemon Chicken",
			  "description": "Crispy oven chicken",
			  "source_url": "https://example.com/recipes/lemon-chicken",
			  "ingredient_groups": [
			    {
			      "name": "",
			      "ingredients": [
			        "500g chicken thighs",
			        "1 tbsp olive oil (optional)"
			      ]
			    }
			  ],
			  "instruction_list": [
			    "Preheat oven to 200C.",
			    "Roast chicken for 35 minutes."
			  ],
			  "total_time": "45",
			  "prep_time": "10",
			  "cook_time": "35",
			  "yields": "4 servings",
			  "image": "https://example.com/chicken.jpg",
			  "language": "en",
			  "cuisine": "MEDITERRANEAN",
			  "category": "Dinner, Easy, Roast",
			  "nutrients": {
			    "calories": "350 kcal",
			    "protein": "28 g"
			  }
			}
			""".trimIndent(),
		)

		val calculatedIds = mutableListOf<Int>()
		val importer = ScrapedRecipeJsonImporter(
			dataSource = dataSource,
			calculateNutrition = calculatedIds::addAll,
		)

		val firstImport = importer.importDirectory(
			jsonArchiveDir = archiveDir,
			upsertExisting = false,
			calculateImportedNutrition = true,
		)
		firstImport.totalFileCount shouldBe 1
		firstImport.importedCount shouldBe 1
		firstImport.skippedExistingCount shouldBe 0
		firstImport.errorCount shouldBe 0
		val recipeId = firstImport.importedRecipeIds.single()
		calculatedIds shouldBe listOf(recipeId)

		val secondImport = importer.importDirectory(
			jsonArchiveDir = archiveDir,
			upsertExisting = false,
			calculateImportedNutrition = false,
		)
		secondImport.importedCount shouldBe 0
		secondImport.skippedExistingCount shouldBe 1

		jsonFile.writeText(
			jsonFile.readText().replace("Roasted Lemon Chicken", "Updated Lemon Chicken"),
		)

		val upsertImport = importer.importDirectory(
			jsonArchiveDir = archiveDir,
			upsertExisting = true,
			calculateImportedNutrition = false,
		)
		upsertImport.importedCount shouldBe 1
		upsertImport.importedRecipeIds shouldBe listOf(recipeId)

		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.executeQuery(
					"SELECT title, meal_type, difficulty, cooking_method, measurement_system " +
						"FROM recipes WHERE id = $recipeId",
				).use { rs ->
					rs.next() shouldBe true
					rs.getString("title") shouldBe "Updated Lemon Chicken"
					rs.getString("meal_type") shouldBe "DINNER"
					rs.getString("difficulty") shouldBe "EASY"
					rs.getString("cooking_method") shouldBe "ROAST"
					rs.getString("measurement_system") shouldBe "MIXED"
				}
				statement.executeQuery(
					"SELECT calories, protein, calculation_source, serving_count " +
						"FROM nutrition WHERE recipe_id = $recipeId",
				).use { rs ->
					rs.next() shouldBe true
					rs.getBigDecimal("calories").shouldBeEqualComparingTo(BigDecimal("1400.00"))
					rs.getBigDecimal("protein").shouldBeEqualComparingTo(BigDecimal("112.00"))
					rs.getString("calculation_source") shouldBe "scraped"
					rs.getBigDecimal("serving_count").shouldBeEqualComparingTo(BigDecimal("4.00"))
				}
			}
		}
	}
}
