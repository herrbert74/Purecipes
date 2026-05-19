package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.db.Db
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.io.File
import kotlin.test.Test

class NutritionSeedImporterIntegrationTest {

	@Test
	fun importFoundationFoodsWritesFoodsAliasesAndMeasures() {
		assumeTrue(isDockerAvailable())

		val container = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_nutrition_seed_test")
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
			val repository = NutritionFoodSeedRepository(dataSource)
			val importer = NutritionSeedImporter(repository)
			val sampleFile = File("src/test/resources/nutrition/foundation_food_sample.json")

			val result = importer.importFdcJson(
				fdcJsonFile = sampleFile,
				replaceExisting = true,
				dryRun = false,
				seedCatalogueAliases = true,
			)

			result.foodsImported shouldBe 2
			result.foodsSkipped shouldBe 0
			result.measuresImported shouldBeGreaterThan 0
			result.catalogueAliasesImported shouldBeGreaterThan 0
			repository.countFoods() shouldBe 2
			repository.countAliases() shouldBeGreaterThan 0
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
