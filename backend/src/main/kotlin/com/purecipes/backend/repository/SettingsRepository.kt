package com.purecipes.backend.repository

import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import java.sql.Connection
import java.sql.ResultSet
import javax.sql.DataSource

class SettingsRepository(
	private val dataSource: DataSource,
) {

	fun getMeasurementPreferences(userId: Long): MeasurementPreferences? {
		return dataSource.connection.use { conn ->
			loadMeasurementPreferences(conn, userId)
		}
	}

	fun saveMeasurementPreferences(
		userId: Long,
		preferences: MeasurementPreferences,
	): MeasurementPreferences = dataSource.connection.use { conn ->
		val originalAutoCommit = conn.autoCommit
		conn.autoCommit = false
		var committed = false
		try {
			saveMeasurementPreferences(conn, userId, preferences)
			conn.commit()
			committed = true
			preferences
		} finally {
			if (!committed) {
				conn.rollback()
			}
			conn.autoCommit = originalAutoCommit
		}
	}

	private fun loadMeasurementPreferences(conn: Connection, userId: Long): MeasurementPreferences? {
		val preferences = conn.prepareStatement(GET_MEASUREMENT_PREFERENCES_SQL).use { ps ->
			ps.setLong(FIRST_PARAMETER_INDEX, userId)
			ps.executeQuery().use(::decodeMeasurementPreferences)
		} ?: return null

		val seenRecipeIds = conn.prepareStatement(GET_SEEN_RECIPE_IDS_SQL).use { ps ->
			ps.setLong(FIRST_PARAMETER_INDEX, userId)
			ps.executeQuery().use(::decodeSeenRecipeIds)
		}

		return preferences.copy(notificationSeenRecipeIds = seenRecipeIds)
	}

	private fun saveMeasurementPreferences(
		conn: Connection,
		userId: Long,
		preferences: MeasurementPreferences,
	) {
		val updatedRows = conn.prepareStatement(UPDATE_MEASUREMENT_PREFERENCES_SQL).use { ps ->
			ps.setString(FIRST_PARAMETER_INDEX, preferences.preferredSystem.name)
			ps.setString(SECOND_PARAMETER_INDEX, preferences.formatHandling.name)
			ps.setString(THIRD_PARAMETER_INDEX, preferences.detectedCountryCode)
			ps.setLong(FOURTH_PARAMETER_INDEX, userId)
			ps.executeUpdate()
		}
		if (updatedRows == 0) {
			conn.prepareStatement(INSERT_MEASUREMENT_PREFERENCES_SQL).use { ps ->
				ps.setLong(FIRST_PARAMETER_INDEX, userId)
				ps.setString(SECOND_PARAMETER_INDEX, preferences.preferredSystem.name)
				ps.setString(THIRD_PARAMETER_INDEX, preferences.formatHandling.name)
				ps.setString(FOURTH_PARAMETER_INDEX, preferences.detectedCountryCode)
				ps.executeUpdate()
			}
		}

		conn.prepareStatement(DELETE_SEEN_RECIPE_IDS_SQL).use { ps ->
			ps.setLong(FIRST_PARAMETER_INDEX, userId)
			ps.executeUpdate()
		}
		conn.prepareStatement(INSERT_SEEN_RECIPE_ID_SQL).use { ps ->
			preferences.notificationSeenRecipeIds.forEach { recipeId ->
				ps.setLong(FIRST_PARAMETER_INDEX, userId)
				ps.setInt(SECOND_PARAMETER_INDEX, recipeId)
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}

	private fun decodeMeasurementPreferences(rs: ResultSet): MeasurementPreferences? {
		if (!rs.next()) {
			return null
		}

		val preferredSystemRaw = rs.getString("preferred_system") ?: return null
		val formatHandlingRaw = rs.getString("format_handling") ?: RecipeFormatHandling.KEEP_AS_IS.name
		return MeasurementPreferences(
			preferredSystem = MeasurementSystem.valueOf(preferredSystemRaw),
			formatHandling = RecipeFormatHandling.valueOf(formatHandlingRaw),
			detectedCountryCode = rs.getString("detected_country_code"),
		)
	}

	private fun decodeSeenRecipeIds(rs: ResultSet): Set<Int> {
		val recipeIds = linkedSetOf<Int>()
		while (rs.next()) {
			recipeIds += rs.getInt("recipe_id")
		}
		return recipeIds
	}

	private companion object {

		const val FIRST_PARAMETER_INDEX = 1
		const val SECOND_PARAMETER_INDEX = 2
		const val THIRD_PARAMETER_INDEX = 3
		const val FOURTH_PARAMETER_INDEX = 4

		const val GET_MEASUREMENT_PREFERENCES_SQL = """
			SELECT preferred_system, format_handling, detected_country_code
			FROM measurement_preferences
			WHERE user_id = ?
		"""

		const val UPDATE_MEASUREMENT_PREFERENCES_SQL = """
			UPDATE measurement_preferences
			SET preferred_system = ?,
				format_handling = ?,
				detected_country_code = ?,
				updated_at = CURRENT_TIMESTAMP
			WHERE user_id = ?
		"""

		const val INSERT_MEASUREMENT_PREFERENCES_SQL = """
			INSERT INTO measurement_preferences (
				user_id,
				preferred_system,
				format_handling,
				detected_country_code,
				updated_at
			)
			VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
		"""

		const val GET_SEEN_RECIPE_IDS_SQL = """
			SELECT recipe_id
			FROM measurement_preference_seen_recipes
			WHERE user_id = ?
			ORDER BY recipe_id
		"""

		const val DELETE_SEEN_RECIPE_IDS_SQL = """
			DELETE FROM measurement_preference_seen_recipes
			WHERE user_id = ?
		"""

		const val INSERT_SEEN_RECIPE_ID_SQL = """
			INSERT INTO measurement_preference_seen_recipes (user_id, recipe_id)
			VALUES (?, ?)
		"""
	}
}
