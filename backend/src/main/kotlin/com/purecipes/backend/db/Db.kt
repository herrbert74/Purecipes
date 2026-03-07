package com.purecipes.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

private const val DEFAULT_PURECIPES_DB_POOL_SIZE = 5

class Db private constructor(
	val dataSource: DataSource,
) {

	companion object {

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
			return Db(HikariDataSource(config))
		}
	}
}
