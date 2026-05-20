package app.purecipes.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource
import kotlin.test.Test

class NutritionSchemaIntegrationTest {

	@Test
	fun ensureSchemaCreatesNutritionTables() {
		assumeTrue(isDockerAvailable())

		val container = createPostgresContainer()
		val dataSource = createDataSource(container)

		try {
			Db.fromDataSource(dataSource)

			val existingTables = loadPublicTableNames(dataSource)
			NUTRITION_TABLE_NAMES.forEach { tableName ->
				existingTables.contains(tableName) shouldBe true
			}

			val nutritionColumns = loadTableColumnNames(dataSource, tableName = "nutrition")
			NUTRITION_METADATA_COLUMN_NAMES.forEach { columnName ->
				nutritionColumns.contains(columnName) shouldBe true
			}
		} finally {
			dataSource.close()
			container.close()
		}
	}

	private fun createPostgresContainer(): PostgreSQLContainer =
		PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_nutrition_schema_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

	private fun createDataSource(container: PostgreSQLContainer): HikariDataSource =
		HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.getJdbcUrl()
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

	private fun loadPublicTableNames(dataSource: DataSource): Set<String> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT table_name
				FROM information_schema.tables
				WHERE table_schema = 'public'
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildSet {
						while (resultSet.next()) {
							add(resultSet.getString("table_name"))
						}
					}
				}
			}
		}

	private fun loadTableColumnNames(dataSource: DataSource, tableName: String): Set<String> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT column_name
				FROM information_schema.columns
				WHERE table_schema = 'public' AND table_name = ?
				""".trimIndent(),
			).use { statement ->
				statement.setString(1, tableName)
				statement.executeQuery().use { resultSet ->
					buildSet {
						while (resultSet.next()) {
							add(resultSet.getString("column_name"))
						}
					}
				}
			}
		}

	private fun isDockerAvailable(): Boolean =
		try {
			DockerClientFactory.instance().isDockerAvailable
		} catch (_: Exception) {
			false
		}

	private companion object {
		val NUTRITION_TABLE_NAMES = setOf(
			"nutrition_foods",
			"nutrition_food_aliases",
			"nutrition_food_measures",
			"ingredient_measurements",
			"ingredient_nutrition_matches",
			"nutrition",
		)

		val NUTRITION_METADATA_COLUMN_NAMES = setOf(
			"matched_ingredient_count",
			"total_ingredient_count",
			"calculation_source",
			"confidence",
			"is_complete",
			"updated_at",
		)
	}
}
