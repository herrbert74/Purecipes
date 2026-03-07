package com.purecipes.backend.repository

import com.purecipes.backend.model.RecipeSummaryDto
import java.sql.PreparedStatement
import javax.sql.DataSource

class RecipeRepository(
	private val dataSource: DataSource,
) {
	fun searchByKeyword(keyword: String, limit: Int = 50): List<RecipeSummaryDto> {
		val trimmed = keyword.trim()
		if (trimmed.isEmpty()) return emptyList()

		val like = "%${trimmed.lowercase()}%"
		val sql = """
			SELECT id, title, cuisine, image_url, total_time
			FROM recipes
			WHERE LOWER(title) LIKE ? OR LOWER(cuisine) LIKE ?
			ORDER BY created_at DESC
			LIMIT ?
		""".trimIndent()

		return searchRecipes(sql, like, limit)
	}

	@Suppress("MagicNumber")
	private fun searchRecipes(
		sql: String,
		like: String,
		limit: Int
	): ArrayList<RecipeSummaryDto> = dataSource.connection.use { conn ->
		conn.prepareStatement(sql).use { ps ->
			ps.setString(1, like)
			ps.setString(2, like)
			ps.setInt(3, limit)

			return executeQuery(ps)
		}
	}

	private fun executeQuery(ps: PreparedStatement): ArrayList<RecipeSummaryDto> = ps.executeQuery().use { rs ->
		val results = ArrayList<RecipeSummaryDto>()
		while (rs.next()) {
			results.add(
				RecipeSummaryDto(
					id = rs.getInt("id"),
					title = rs.getString("title"),
					cuisine = rs.getString("cuisine"),
					imageUrl = rs.getString("image_url"),
					totalTime = rs.getObject("total_time") as? Int,
				)
			)
		}
		return results
	}
}
