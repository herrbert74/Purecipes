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
					statement.execute(FAVORITES_TABLE_SQL)
					statement.execute(FAVORITES_USER_CREATED_AT_INDEX_SQL)
				}
			}
		}
	}
}
