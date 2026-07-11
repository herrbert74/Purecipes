package app.purecipes.backend.feature.subscription

import javax.sql.DataSource

class UserPremiumRepository(
	private val dataSource: DataSource,
) {

	fun isPremium(userId: Long): Boolean {
		dataSource.connection.use { connection ->
			connection.prepareStatement(IS_PREMIUM_SQL).use { statement ->
				statement.setLong(FIRST_PARAMETER_INDEX, userId)
				statement.executeQuery().use { resultSet ->
					if (!resultSet.next()) {
						return false
					}
					return resultSet.getBoolean("is_premium")
				}
			}
		}
	}

	fun setPremium(userId: Long, isPremium: Boolean) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(SET_PREMIUM_SQL).use { statement ->
				statement.setBoolean(FIRST_PARAMETER_INDEX, isPremium)
				statement.setLong(SECOND_PARAMETER_INDEX, userId)
				statement.executeUpdate()
			}
		}
	}

	private companion object {

		private const val FIRST_PARAMETER_INDEX = 1
		private const val SECOND_PARAMETER_INDEX = 2

		private const val IS_PREMIUM_SQL = """
			SELECT is_premium
			FROM app_users
			WHERE id = ?
		"""

		private const val SET_PREMIUM_SQL = """
			UPDATE app_users
			SET is_premium = ?, updated_at = CURRENT_TIMESTAMP
			WHERE id = ?
		"""
	}
}
