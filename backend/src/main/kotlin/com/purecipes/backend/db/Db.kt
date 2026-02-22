package com.purecipes.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

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
				maximumPoolSize = (System.getenv("PURECIPES_DB_POOL_SIZE")?.toIntOrNull() ?: 5)
				isAutoCommit = true
				validate()
			}
			return Db(HikariDataSource(config))
		}
	}
}
