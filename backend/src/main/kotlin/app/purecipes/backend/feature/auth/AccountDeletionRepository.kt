package app.purecipes.backend.feature.auth

import java.sql.Connection
import java.sql.ResultSet
import javax.sql.DataSource

const val RETAINED_RECIPE_OWNER_PROVIDER = "SYSTEM"
const val RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID = "orphaned-recipes"
const val RETAINED_RECIPE_OWNER_EMAIL = "orphaned-recipes@purecipes.invalid"
const val RETAINED_RECIPE_OWNER_DISPLAY_NAME = "Purecipes"

class AccountDeletionRepository(
	private val dataSource: DataSource,
) {

	fun deleteAccount(userId: Long): AccountDeletionResult {
		dataSource.connection.use { connection ->
			val previousAutoCommit = connection.autoCommit
			connection.autoCommit = false
			var committed = false
			try {
				val result = connection.deleteAccountInTransaction(userId)
				connection.commit()
				committed = true
				return result
			} finally {
				if (!committed) {
					connection.rollback()
				}
				connection.autoCommit = previousAutoCommit
			}
		}
	}

	fun findAccount(userId: Long): AccountRecord? = dataSource.connection.use { connection ->
		connection.findAccountById(userId)
	}

	fun findAccountsByEmail(email: String): List<AccountRecord> {
		val normalizedEmail = email.trim().lowercase()
		if (normalizedEmail.isBlank()) {
			return emptyList()
		}
		return dataSource.connection.use { connection ->
			connection.prepareStatement(FIND_ACCOUNTS_BY_EMAIL_SQL).use { statement ->
				statement.setString(FIRST_PARAMETER_INDEX, normalizedEmail)
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.toAccountRecord())
						}
					}
				}
			}
		}
	}

	fun summarizeAccountData(userId: Long): AccountDataSummary = dataSource.connection.use { connection ->
		AccountDataSummary(
			createdRecipeCount = connection.countRows(COUNT_CREATED_RECIPES_SQL, userId),
			favoriteCount = connection.countRows(COUNT_FAVORITES_SQL, userId),
			cookbookCount = connection.countRows(COUNT_COOKBOOKS_SQL, userId),
			activeSessionCount = connection.countRows(COUNT_ACTIVE_SESSIONS_SQL, userId),
			pantryIngredientCount = connection.countRows(COUNT_PANTRY_INGREDIENTS_SQL, userId),
			excludedIngredientCount = connection.countRows(COUNT_EXCLUDED_INGREDIENTS_SQL, userId),
		)
	}

	private fun Connection.deleteAccountInTransaction(userId: Long): AccountDeletionResult {
		val account = findAccountById(userId)
		return when {
			account == null -> AccountDeletionResult.AccountNotFound
			account.provider == RETAINED_RECIPE_OWNER_PROVIDER -> AccountDeletionResult.RetainedRecipeOwner
			else -> reassignRecipesAndDeleteAccount(userId)
		}
	}

	private fun Connection.reassignRecipesAndDeleteAccount(userId: Long): AccountDeletionResult {
		val reassignedRecipeCount = reassignCreatedRecipes(
			fromUserId = userId,
			toUserId = findOrCreateRetainedRecipeOwnerId(),
		)
		deleteAccountRow(userId)
		return AccountDeletionResult.Deleted(reassignedRecipeCount = reassignedRecipeCount)
	}

	private fun Connection.findAccountById(userId: Long): AccountRecord? =
		prepareStatement(FIND_ACCOUNT_BY_ID_SQL).use { statement ->
			statement.setLong(FIRST_PARAMETER_INDEX, userId)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.toAccountRecord() else null
			}
		}

	private fun Connection.findOrCreateRetainedRecipeOwnerId(): Long {
		prepareStatement(FIND_ACCOUNT_BY_PROVIDER_SQL).use { statement ->
			statement.setString(FIRST_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_PROVIDER)
			statement.setString(SECOND_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) {
					return resultSet.getLong("id")
				}
			}
		}

		prepareStatement(CREATE_RETAINED_RECIPE_OWNER_SQL, arrayOf("id")).use { statement ->
			statement.setString(FIRST_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_PROVIDER)
			statement.setString(SECOND_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID)
			statement.setString(THIRD_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_EMAIL)
			statement.setString(FOURTH_PARAMETER_INDEX, RETAINED_RECIPE_OWNER_DISPLAY_NAME)
			statement.executeUpdate()
			statement.generatedKeys.use { resultSet ->
				resultSet.next()
				return resultSet.getLong(FIRST_PARAMETER_INDEX)
			}
		}
	}

	private fun Connection.reassignCreatedRecipes(fromUserId: Long, toUserId: Long): Int =
		prepareStatement(REASSIGN_CREATED_RECIPES_SQL).use { statement ->
			statement.setLong(FIRST_PARAMETER_INDEX, toUserId)
			statement.setLong(SECOND_PARAMETER_INDEX, fromUserId)
			statement.executeUpdate()
		}

	private fun Connection.deleteAccountRow(userId: Long) {
		prepareStatement(DELETE_ACCOUNT_SQL).use { statement ->
			statement.setLong(FIRST_PARAMETER_INDEX, userId)
			statement.executeUpdate()
		}
	}

	private fun Connection.countRows(sql: String, userId: Long): Int =
		prepareStatement(sql).use { statement ->
			statement.setLong(FIRST_PARAMETER_INDEX, userId)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.getInt(FIRST_PARAMETER_INDEX) else 0
			}
		}

	private fun ResultSet.toAccountRecord(): AccountRecord = AccountRecord(
		id = getLong("id"),
		provider = getString("provider"),
		email = getString("email"),
		displayName = getString("display_name"),
	)

	private companion object {

		const val FIRST_PARAMETER_INDEX = 1
		const val SECOND_PARAMETER_INDEX = 2
		const val THIRD_PARAMETER_INDEX = 3
		const val FOURTH_PARAMETER_INDEX = 4

		const val FIND_ACCOUNT_BY_ID_SQL = """
			SELECT id, provider, email, display_name
			FROM app_users
			WHERE id = ?
		"""

		const val FIND_ACCOUNT_BY_PROVIDER_SQL = """
			SELECT id
			FROM app_users
			WHERE provider = ? AND external_user_id = ?
		"""

		const val FIND_ACCOUNTS_BY_EMAIL_SQL = """
			SELECT id, provider, email, display_name
			FROM app_users
			WHERE LOWER(email) = ?
			ORDER BY id
		"""

		const val CREATE_RETAINED_RECIPE_OWNER_SQL = """
			INSERT INTO app_users (provider, external_user_id, email, display_name)
			VALUES (?, ?, ?, ?)
		"""

		const val REASSIGN_CREATED_RECIPES_SQL = """
			UPDATE recipes
			SET created_by_user_id = ?
			WHERE created_by_user_id = ?
		"""

		const val DELETE_ACCOUNT_SQL = """
			DELETE FROM app_users
			WHERE id = ?
		"""

		const val COUNT_CREATED_RECIPES_SQL = """
			SELECT COUNT(*)
			FROM recipes
			WHERE created_by_user_id = ?
		"""

		const val COUNT_FAVORITES_SQL = """
			SELECT COUNT(*)
			FROM favorites
			WHERE user_id = ?
		"""

		const val COUNT_COOKBOOKS_SQL = """
			SELECT COUNT(*)
			FROM cookbooks
			WHERE user_id = ?
		"""

		const val COUNT_ACTIVE_SESSIONS_SQL = """
			SELECT COUNT(*)
			FROM auth_sessions
			WHERE user_id = ?
				AND revoked_at IS NULL
				AND expires_at > CURRENT_TIMESTAMP
		"""

		const val COUNT_PANTRY_INGREDIENTS_SQL = """
			SELECT COUNT(*)
			FROM user_pantry
			WHERE user_id = ?
		"""

		const val COUNT_EXCLUDED_INGREDIENTS_SQL = """
			SELECT COUNT(*)
			FROM user_excluded_ingredients
			WHERE user_id = ?
		"""
	}
}
