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

class RecipeNutritionEstimatorTest {

	@Test
	fun estimateReturnsNutritionSummaryForMatchedIngredient() {
		assumeTrue(isDockerAvailable())

		val container = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_recipe_nutrition_estimator_test")
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

			val summary = RecipeNutritionEstimator(dataSource).estimate(listOf("1 cup sugar"))
			summary shouldNotBe null
			summary?.calories shouldNotBe null
			summary?.matchedIngredientCount shouldBe 1
			summary?.totalIngredientCount shouldBe 1
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
