package app.purecipes.backend

import app.purecipes.backend.db.Db
import org.h2.jdbcx.JdbcDataSource

internal fun createInMemoryDb(namePrefix: String): Db {
	val dbName = "${namePrefix}_${System.nanoTime()}"
	val dataSource = JdbcDataSource().apply {
		setURL("jdbc:h2:mem:$dbName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
		user = "sa"
		password = ""
	}
	return Db.fromDataSource(dataSource)
}
