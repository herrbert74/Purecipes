package app.purecipes.backend

import app.purecipes.backend.db.Db

internal fun Db.insertAccount(
	email: String,
	provider: String = "GOOGLE",
	externalUserId: String = "external-user",
	displayName: String = "Owner",
): Long {
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO app_users (provider, external_user_id, email, display_name)
				VALUES (?, ?, ?, ?)
			""".trimIndent(),
			arrayOf("id"),
		).use { statement ->
			statement.setString(1, provider)
			statement.setString(2, externalUserId)
			statement.setString(3, email)
			statement.setString(4, displayName)
			statement.executeUpdate()
			statement.generatedKeys.use { resultSet ->
				resultSet.next()
				return resultSet.getLong(1)
			}
		}
	}
}

internal fun Db.executeSql(sql: String) {
	dataSource.connection.use { connection ->
		connection.createStatement().use { statement ->
			statement.execute(sql)
		}
	}
}

internal fun Db.countRows(sql: String): Int = dataSource.connection.use { connection ->
	connection.createStatement().use { statement ->
		statement.executeQuery(sql).use { resultSet ->
			resultSet.next()
			resultSet.getInt(1)
		}
	}
}

internal fun Db.queryLong(sql: String): Long = dataSource.connection.use { connection ->
	connection.createStatement().use { statement ->
		statement.executeQuery(sql).use { resultSet ->
			resultSet.next()
			resultSet.getLong(1)
		}
	}
}

internal fun Db.queryString(sql: String): String = dataSource.connection.use { connection ->
	connection.createStatement().use { statement ->
		statement.executeQuery(sql).use { resultSet ->
			resultSet.next()
			resultSet.getString(1)
		}
	}
}
