package com.purecipes.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

private const val DEFAULT_PURECIPES_DB_POOL_SIZE = 5

class Db private constructor(
	val dataSource: DataSource,
) {

	companion object {

		internal fun fromDataSource(dataSource: DataSource): Db {
			ensureSchema(dataSource)
			return Db(dataSource)
		}

		fun create(): Db {
			val url = System.getenv("PURECIPES_DB_URL") ?: "jdbc:postgresql://localhost:5432/purecipes"
			val user = System.getenv("PURECIPES_DB_USER") ?: "postgres"
			val password = System.getenv("PURECIPES_DB_PASSWORD") ?: "postgres"

			val config = HikariConfig().apply {
				jdbcUrl = url
				username = user
				this.password = password
				maximumPoolSize =
					(System.getenv("PURECIPES_DB_POOL_SIZE")?.toIntOrNull() ?: DEFAULT_PURECIPES_DB_POOL_SIZE)
				isAutoCommit = true
				validate()
			}
			val dataSource = HikariDataSource(config)
			ensureSchema(dataSource)
			return Db(dataSource)
		}

		private fun ensureSchema(dataSource: DataSource) {
			dataSource.connection.use { connection ->
				connection.createStatement().use { statement ->
					statement.execute(APP_USERS_TABLE_SQL)
					statement.execute(AUTH_SESSIONS_TABLE_SQL)
					statement.execute(RECIPES_TABLE_SQL)
					statement.execute(RECIPES_ADD_DESCRIPTION_SQL)
					statement.execute(RECIPES_ADD_CREATED_BY_USER_ID_SQL)
					statement.execute(INGREDIENT_GROUPS_TABLE_SQL)
					statement.execute(INGREDIENTS_TABLE_SQL)
					statement.execute(INSTRUCTION_STEPS_TABLE_SQL)
					statement.execute(FAVORITES_TABLE_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_TABLE_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_PREFERRED_SYSTEM_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_FORMAT_HANDLING_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_DETECTED_COUNTRY_CODE_SQL)
					statement.execute(MEASUREMENT_PREFERENCE_SEEN_RECIPES_TABLE_SQL)
					statement.execute(FAVORITES_USER_CREATED_AT_INDEX_SQL)
				}
			}
		}
	}
}
