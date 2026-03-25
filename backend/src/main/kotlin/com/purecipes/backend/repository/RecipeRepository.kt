package com.purecipes.backend.repository

import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class RecipeRepository(
	private val dataSource: DataSource,
) {

	fun searchByKeyword(keyword: String, limit: Int = 50): List<RecipeSummary> {
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

	fun getRecipeDetails(recipeId: Int): RecipeDetails? = dataSource.connection.use { conn ->
		val recipeRecord = loadRecipeRecord(conn, recipeId) ?: return@use null
		val ingredientGroups = loadIngredientGroupsForRecipe(conn, recipeId)
		val isFavorite = isFavorite(conn, recipeId)
		val steps = loadStepsForRecipe(conn, recipeId, recipeRecord.instructions)

		RecipeDetails(
			id = recipeRecord.id,
			title = recipeRecord.title,
			description = buildDescription(
				cuisine = recipeRecord.cuisine,
				category = recipeRecord.category,
				totalTime = recipeRecord.totalTime,
				yields = recipeRecord.yields,
			),
			imageUrl = recipeRecord.imageUrl,
			ingredientGroups = ingredientGroups,
			steps = steps,
			totalTime = recipeRecord.totalTime,
			yields = recipeRecord.yields,
			cuisine = recipeRecord.cuisine,
			isFavorite = isFavorite,
		)
	}

	fun getFavoriteRecipes(): List<RecipeSummary> = dataSource.connection.use { conn ->
		conn.prepareStatement(favoritesSql).use { ps ->
			ps.executeQuery().use(::readFavoriteRecipes)
		}
	}

	fun addFavorite(recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeExists(conn, recipeId)) {
			return@use false
		}

		conn.prepareStatement(addFavoriteSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		true
	}

	fun removeFavorite(recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeExists(conn, recipeId)) {
			return@use false
		}

		conn.prepareStatement(removeFavoriteSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		true
	}

	private fun loadRecipeRecord(conn: java.sql.Connection, recipeId: Int): RecipeRecord? {
		return conn.prepareStatement(recipeSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null

				RecipeRecord(
					id = rs.getInt("id"),
					title = rs.getString("title"),
					instructions = rs.getString("instructions"),
					totalTime = rs.getObject("total_time") as? Int,
					yields = rs.getString("yields"),
					imageUrl = rs.getString("image_url"),
					cuisine = rs.getString("cuisine"),
					category = rs.getString("category"),
				)
			}
		}
	}

	@Suppress("MagicNumber")
	private fun searchRecipes(
		sql: String,
		like: String,
		limit: Int
	): ArrayList<RecipeSummary> = dataSource.connection.use { conn ->
		conn.prepareStatement(sql).use { ps ->
			ps.setString(1, like)
			ps.setString(2, like)
			ps.setInt(3, limit)

			return executeQuery(ps)
		}
	}

	private fun executeQuery(ps: PreparedStatement): ArrayList<RecipeSummary> = ps.executeQuery().use { rs ->
		val results = ArrayList<RecipeSummary>()
		while (rs.next()) {
			results.add(
				RecipeSummary(
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

	private fun loadIngredientGroupsForRecipe(
		conn: java.sql.Connection,
		recipeId: Int,
	): List<IngredientGroup> = loadIngredientGroups(
		ps = conn.prepareStatement(ingredientGroupsSql).also {
			it.setInt(1, recipeId)
		},
	)

	private fun loadIngredientGroups(ps: PreparedStatement): List<IngredientGroup> = ps.use { statement ->
		statement.executeQuery().use { rs ->
			val groupsById = linkedMapOf<Int, IngredientGroupAccumulator>()

			while (rs.next()) {
				val groupId = rs.getInt("group_id")
				val group = groupsById.getOrPut(groupId) {
					IngredientGroupAccumulator(name = rs.getNullableString("group_name"))
				}
				rs.getNullableString("ingredient")?.let(group.ingredients::add)
			}

			groupsById.values.map { accumulator ->
				IngredientGroup(
					name = accumulator.name,
					ingredients = accumulator.ingredients,
				)
			}
		}
	}

	private fun loadStepsForRecipe(
		conn: java.sql.Connection,
		recipeId: Int,
		instructions: String?,
	): List<String> = loadSteps(
		ps = conn.prepareStatement(stepsSql).also {
			it.setInt(1, recipeId)
		},
		instructions = instructions,
	)

	private fun loadSteps(ps: PreparedStatement, instructions: String?): List<String> = ps.use { statement ->
		statement.executeQuery().use { rs ->
			val steps = mutableListOf<String>()
			while (rs.next()) {
				rs.getNullableString("step")
					?.takeIf { it.isNotBlank() }
					?.let(steps::add)
			}

			if (steps.isNotEmpty()) {
				steps
			} else {
				instructions
					.orEmpty()
					.lineSequence()
					.map(String::trim)
					.filter(String::isNotEmpty)
					.toList()
			}
		}
	}

	private fun isFavorite(conn: java.sql.Connection, recipeId: Int): Boolean {
		return conn.prepareStatement(isFavoriteSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeQuery().use { rs ->
				rs.next() && rs.getBoolean("is_favorite")
			}
		}
	}

	private fun recipeExists(conn: java.sql.Connection, recipeId: Int): Boolean {
		return conn.prepareStatement(recipeExistsSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeQuery().use { rs ->
				rs.next()
			}
		}
	}

	private fun buildDescription(
		cuisine: String?,
		category: String?,
		totalTime: Int?,
		yields: String?,
	): String {
		val introParts = listOfNotNull(
			cuisine?.takeIf { it.isNotBlank() },
			category?.takeIf { it.isNotBlank() },
		)
		val intro = when {
			introParts.isEmpty() -> "Recipe"
			else -> introParts.joinToString(separator = " ", postfix = " recipe")
		}

		val details = buildList {
			totalTime?.let { add("ready in $it minutes") }
			yields?.takeIf { it.isNotBlank() }?.let(::add)
		}

		return if (details.isEmpty()) {
			"$intro."
		} else {
			"$intro, ${details.joinToString()}."
		}
	}

	private fun ResultSet.getNullableString(columnLabel: String): String? =
		getString(columnLabel)?.trim()?.takeIf { it.isNotEmpty() }

	private fun readFavoriteRecipes(rs: ResultSet): List<RecipeSummary> {
		val results = ArrayList<RecipeSummary>()
		while (rs.next()) {
			results.add(
				RecipeSummary(
					id = rs.getInt("id"),
					title = rs.getString("title"),
					cuisine = rs.getString("cuisine"),
					imageUrl = rs.getString("image_url"),
					totalTime = rs.getObject("total_time") as? Int,
					isFavorite = true,
				)
			)
		}
		return results
	}

	private data class RecipeRecord(
		val id: Int,
		val title: String,
		val instructions: String?,
		val totalTime: Int?,
		val yields: String?,
		val imageUrl: String?,
		val cuisine: String?,
		val category: String?,
	)

	private data class IngredientGroupAccumulator(
		val name: String?,
		val ingredients: MutableList<String> = mutableListOf(),
	)

	private companion object {

		const val addFavoriteSql = """
			INSERT INTO favorites (recipe_id)
			VALUES (?)
			ON CONFLICT (recipe_id) DO NOTHING
		"""

		const val favoritesSql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time
			FROM favorites f
			INNER JOIN recipes r ON r.id = f.recipe_id
			ORDER BY f.created_at DESC
		"""

		const val isFavoriteSql = """
			SELECT EXISTS(
				SELECT 1
				FROM favorites
				WHERE recipe_id = ?
			) AS is_favorite
		"""

		const val recipeSql = """
			SELECT id, title, instructions, total_time, yields, image_url, cuisine, category
			FROM recipes
			WHERE id = ?
		"""

		const val recipeExistsSql = """
			SELECT 1
			FROM recipes
			WHERE id = ?
		"""

		const val removeFavoriteSql = """
			DELETE FROM favorites
			WHERE recipe_id = ?
		"""

		const val ingredientGroupsSql = """
			SELECT g.id AS group_id, g.name AS group_name, i.ingredient AS ingredient
			FROM ingredient_groups g
			LEFT JOIN ingredients i ON i.ingredient_group_id = g.id
			WHERE g.recipe_id = ?
			ORDER BY g.order_index ASC, i.order_index ASC
		"""

		const val stepsSql = """
			SELECT step
			FROM instruction_steps
			WHERE recipe_id = ?
			ORDER BY order_index ASC
		"""
	}
}
