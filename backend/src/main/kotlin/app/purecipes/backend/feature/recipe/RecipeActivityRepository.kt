package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.RecipeActivityEvent
import javax.sql.DataSource

private const val USER_ID_PARAMETER = 1
private const val RECIPE_ID_PARAMETER = 2
private const val EVENT_PARAMETER = 3
private const val VISIBLE_RECIPE_ID_PARAMETER = 4
private const val OWNER_USER_ID_PARAMETER = 5

class RecipeActivityRepository(
	private val dataSource: DataSource,
) {

	fun record(userId: Long, recipeId: Int, event: RecipeActivityEvent): Boolean {
		val sql = """
			INSERT INTO recipe_activity (user_id, recipe_id, event)
			SELECT ?, ?, ?
			FROM recipes r
			WHERE r.id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
		""".trimIndent()
		return dataSource.connection.use { connection ->
			connection.prepareStatement(sql).use { statement ->
				statement.setLong(USER_ID_PARAMETER, userId)
				statement.setInt(RECIPE_ID_PARAMETER, recipeId)
				statement.setString(EVENT_PARAMETER, event.name)
				statement.setInt(VISIBLE_RECIPE_ID_PARAMETER, recipeId)
				statement.setLong(OWNER_USER_ID_PARAMETER, userId)
				statement.executeUpdate() > 0
			}
		}
	}
}
