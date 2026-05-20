package app.purecipes.backend.feature.recipe

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.NutritionFoodSeedRepository
import app.purecipes.backend.feature.nutrition.NutritionSeedImporter
import app.purecipes.backend.feature.nutrition.RecipeNutritionService
import app.purecipes.backend.feature.nutrition.insertRecipeWithIngredients
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionConfidence
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

class RecipeNutritionDetailsLoaderTest {

	@Test
	fun loadMapsCalculatedNutritionFromDatabase() {
		assumeTrue(isDockerAvailable())

		val container = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_recipe_nutrition_details_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.getJdbcUrl()
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			Db.fromDataSource(dataSource)
			NutritionSeedImporter(NutritionFoodSeedRepository(dataSource)).importFdcJson(
				fdcJsonFile = java.io.File("src/test/resources/nutrition/foundation_food_sample.json"),
				replaceExisting = true,
				dryRun = false,
				seedCatalogueAliases = true,
			)

			val recipeId = insertRecipeWithIngredients(
				dataSource = dataSource,
				ingredients = listOf("1 cup sugar"),
				yields = "4 servings",
			)
			RecipeNutritionService(dataSource).calculateAndPersist(recipeId)

			val nutrition = RecipeRepository(dataSource).getRecipeDetails(recipeId)?.nutrition
			nutrition shouldNotBe null
			nutrition?.recipeTotals?.calories shouldNotBe null
			nutrition?.recipeTotals?.matchedIngredientCount shouldBe 1
			nutrition?.recipeTotals?.totalIngredientCount shouldBe 1
			nutrition?.recipeTotals?.calculationSource shouldBe NutritionCalculationSource.CALCULATED
			nutrition?.recipeTotals?.confidence shouldBe NutritionConfidence.COMPLETE
			nutrition?.recipeTotals?.isComplete shouldBe true
			nutrition?.perServing?.calories shouldNotBe null
			nutrition?.ingredients?.size shouldBe 1
			nutrition?.ingredients?.first()?.isMatched shouldBe true
		} finally {
			dataSource.close()
			container.close()
		}
	}

	@Test
	fun loadMapsScrapedNutritionWhenCaloriesPresent() {
		assumeTrue(isDockerAvailable())

		val container = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_recipe_scraped_nutrition_details_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.getJdbcUrl()
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			Db.fromDataSource(dataSource)
			val recipeId = insertRecipeWithIngredients(
				dataSource = dataSource,
				ingredients = listOf("1 cup sugar"),
			)
			dataSource.connection.use { connection ->
				connection.prepareStatement(
					"""
					INSERT INTO nutrition (recipe_id, calories, protein)
					VALUES (?, ?, ?)
					""".trimIndent(),
				).use { statement ->
					statement.setInt(1, recipeId)
					statement.setBigDecimal(2, java.math.BigDecimal("500"))
					statement.setBigDecimal(3, java.math.BigDecimal("12"))
					statement.executeUpdate()
				}
			}

			val nutrition = RecipeRepository(dataSource).getRecipeDetails(recipeId)?.nutrition
			nutrition shouldNotBe null
			nutrition?.recipeTotals?.calories shouldBe 500.0
			nutrition?.recipeTotals?.protein shouldBe 12.0
			nutrition?.recipeTotals?.calculationSource shouldBe NutritionCalculationSource.SCRAPED
			nutrition?.recipeTotals?.matchedIngredientCount shouldBe null
			nutrition?.recipeTotals?.confidence shouldBe null
			nutrition?.recipeTotals?.isComplete shouldBe false
		} finally {
			dataSource.close()
			container.close()
		}
	}

	private fun isDockerAvailable(): Boolean =
		try {
			DockerClientFactory.instance().isDockerAvailable
		} catch (_: Exception) {
			false
		}
}
