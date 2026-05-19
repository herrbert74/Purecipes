package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.db.Db
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

class RecipeNutritionServiceIntegrationTest {

	@Test
	fun calculateAndPersistStoresRecipeNutrition() {
		assumeTrue(isDockerAvailable())

		val container = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_recipe_nutrition_test")
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
			)

			val result = RecipeNutritionService(dataSource).calculateAndPersist(recipeId)
			result.totals shouldNotBe null
			result.totals?.matchedIngredientCount shouldBe 1
			readRecipeCalories(dataSource, recipeId) shouldNotBe null
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
