package com.purecipes.backend.repository

import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
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

	fun getRecipeDetails(recipeId: Int, userId: Long? = null): RecipeDetails? = dataSource.connection.use { conn ->
		val recipeRecord = loadRecipeRecord(conn, recipeId) ?: return@use null
		buildRecipeDetails(conn, recipeRecord, userId)
	}

	fun getRecipesCreatedByUser(userId: Long): List<RecipeDetails> = dataSource.connection.use { conn ->
		loadCreatedRecipeRecords(conn, userId).map { recipeRecord ->
			buildRecipeDetails(conn, recipeRecord, userId)
		}
	}

	fun createRecipe(userId: Long, request: RecipeWriteRequest): RecipeDetails = dataSource.connection.use { conn ->
		runInTransaction(conn) {
			val recipeId = insertRecipe(conn, userId, request)
			writeIngredients(conn, recipeId, request.ingredientGroups)
			writeSteps(conn, recipeId, request.steps)
			buildRecipeDetails(
				conn,
				loadRecipeRecord(conn, recipeId) ?: error("Recipe missing after create"),
				userId,
			)
		}
	}

	fun updateRecipe(
		userId: Long,
		recipeId: Int,
		request: RecipeWriteRequest,
	): RecipeDetails? = dataSource.connection.use { conn ->
		if (!isRecipeOwnedByUser(conn, recipeId, userId)) {
			return@use null
		}

		runInTransaction(conn) {
			updateRecipeRow(conn, recipeId, request)
			deleteRecipeChildren(conn, recipeId)
			writeIngredients(conn, recipeId, request.ingredientGroups)
			writeSteps(conn, recipeId, request.steps)
			buildRecipeDetails(
				conn,
				loadRecipeRecord(conn, recipeId) ?: error("Recipe missing after update"),
				userId,
			)
		}
	}

	private fun <T> runInTransaction(conn: java.sql.Connection, block: () -> T): T {
		val originalAutoCommit = conn.autoCommit
		conn.autoCommit = false
		var committed = false
		try {
			val result = block()
			conn.commit()
			committed = true
			return result
		} finally {
			if (!committed) {
				conn.rollback()
			}
			conn.autoCommit = originalAutoCommit
		}
	}

	fun getFavoriteRecipes(userId: Long): List<RecipeSummary> = dataSource.connection.use { conn ->
		conn.prepareStatement(favoritesSql).use { ps ->
			ps.setLong(1, userId)
			ps.executeQuery().use(::readFavoriteRecipes)
		}
	}

	fun addFavorite(userId: Long, recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeExists(conn, recipeId)) {
			return@use false
		}

		conn.prepareStatement(addFavoriteSql).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeUpdate()
		}
		true
	}

	fun removeFavorite(userId: Long, recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeExists(conn, recipeId)) {
			return@use false
		}

		conn.prepareStatement(removeFavoriteSql).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
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
					description = rs.getNullableString("description"),
					instructions = rs.getNullableString("instructions"),
					totalTime = rs.getObject("total_time") as? Int,
					yields = rs.getNullableString("yields"),
					imageUrl = rs.getNullableString("image_url"),
					cuisine = rs.getNullableString("cuisine"),
					category = rs.getNullableString("category"),
				)
			}
		}
	}

	private fun loadCreatedRecipeRecords(conn: java.sql.Connection, userId: Long): List<RecipeRecord> {
		return conn.prepareStatement(createdRecipesSql).use { ps ->
			ps.setLong(FIRST_PARAMETER_INDEX, userId)
			ps.executeQuery().use(::readRecipeRecords)
		}
	}

	private fun readRecipeRecords(rs: ResultSet): List<RecipeRecord> {
		val records = mutableListOf<RecipeRecord>()
		while (rs.next()) {
			records += rs.toRecipeRecord()
		}
		return records
	}

	private fun ResultSet.toRecipeRecord(): RecipeRecord {
		return RecipeRecord(
			id = getInt("id"),
			title = getString("title"),
			description = getNullableString("description"),
			instructions = getNullableString("instructions"),
			totalTime = getObject("total_time") as? Int,
			yields = getNullableString("yields"),
			imageUrl = getNullableString("image_url"),
			cuisine = getNullableString("cuisine"),
			category = getNullableString("category"),
		)
	}

	private fun buildRecipeDetails(
		conn: java.sql.Connection,
		recipeRecord: RecipeRecord,
		userId: Long?,
	): RecipeDetails {
		val ingredientGroups = loadIngredientGroupsForRecipe(conn, recipeRecord.id)
		val isFavorite = isFavorite(conn, recipeRecord.id, userId)
		val steps = loadStepsForRecipe(conn, recipeRecord.id, recipeRecord.instructions)

		return RecipeDetails(
			id = recipeRecord.id,
			title = recipeRecord.title,
			description = recipeRecord.description ?: buildDescription(
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

	private fun isFavorite(conn: java.sql.Connection, recipeId: Int, userId: Long?): Boolean {
		if (userId == null) {
			return false
		}
		return conn.prepareStatement(isFavoriteSql).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeQuery().use { rs ->
				rs.next() && rs.getBoolean("is_favorite")
			}
		}
	}

	private fun isRecipeOwnedByUser(conn: java.sql.Connection, recipeId: Int, userId: Long): Boolean {
		return conn.prepareStatement(recipeOwnedByUserSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs ->
				rs.next()
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

	private fun deleteRecipeChildren(conn: java.sql.Connection, recipeId: Int) {
		conn.prepareStatement(deleteIngredientsForRecipeSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		conn.prepareStatement(deleteIngredientGroupsSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		conn.prepareStatement(deleteInstructionStepsSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
	}

	private fun writeIngredients(conn: java.sql.Connection, recipeId: Int, ingredientGroups: List<IngredientGroup>) {
		ingredientGroups.forEachIndexed { groupIndex, group ->
			val ingredients = group.ingredients.map(String::trim).filter(String::isNotEmpty)
			if (ingredients.isEmpty()) {
				return@forEachIndexed
			}

			val groupId = insertIngredientGroup(conn, recipeId, group.name, groupIndex)

			conn.prepareStatement(createIngredientSql).use { ps ->
				ingredients.forEachIndexed { ingredientIndex, ingredient ->
					ps.setInt(FIRST_PARAMETER_INDEX, groupId)
					ps.setString(SECOND_PARAMETER_INDEX, ingredient)
					ps.setInt(THIRD_PARAMETER_INDEX, ingredientIndex)
					ps.addBatch()
				}
				ps.executeBatch()
			}
		}
	}

	private fun writeSteps(conn: java.sql.Connection, recipeId: Int, steps: List<String>) {
		val normalizedSteps = steps.map(String::trim).filter(String::isNotEmpty)
		if (normalizedSteps.isEmpty()) {
			return
		}

		conn.prepareStatement(createInstructionStepSql).use { ps ->
			normalizedSteps.forEachIndexed { stepIndex, step ->
				ps.setInt(FIRST_PARAMETER_INDEX, recipeId)
				ps.setString(SECOND_PARAMETER_INDEX, step)
				ps.setInt(THIRD_PARAMETER_INDEX, stepIndex)
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}

	private fun insertRecipe(conn: java.sql.Connection, userId: Long, request: RecipeWriteRequest): Int {
		return conn.prepareStatement(createRecipeSql, Statement.RETURN_GENERATED_KEYS).use { ps ->
			ps.setString(FIRST_PARAMETER_INDEX, request.title.trim())
			ps.setString(SECOND_PARAMETER_INDEX, request.description.trim())
			ps.setString(THIRD_PARAMETER_INDEX, request.steps.joinToString(separator = "\n"))
			ps.setObject(FOURTH_PARAMETER_INDEX, request.totalTime)
			ps.setString(FIFTH_PARAMETER_INDEX, request.yields?.trim())
			ps.setString(SIXTH_PARAMETER_INDEX, request.imageUrl?.trim())
			ps.setString(SEVENTH_PARAMETER_INDEX, request.cuisine?.trim())
			ps.setLong(EIGHTH_PARAMETER_INDEX, userId)
			ps.executeUpdate()
			ps.generatedId()
		}
	}

	private fun updateRecipeRow(conn: java.sql.Connection, recipeId: Int, request: RecipeWriteRequest) {
		conn.prepareStatement(updateRecipeSql).use { ps ->
			ps.setString(FIRST_PARAMETER_INDEX, request.title.trim())
			ps.setString(SECOND_PARAMETER_INDEX, request.description.trim())
			ps.setString(THIRD_PARAMETER_INDEX, request.steps.joinToString(separator = "\n"))
			ps.setObject(FOURTH_PARAMETER_INDEX, request.totalTime)
			ps.setString(FIFTH_PARAMETER_INDEX, request.yields?.trim())
			ps.setString(SIXTH_PARAMETER_INDEX, request.imageUrl?.trim())
			ps.setString(SEVENTH_PARAMETER_INDEX, request.cuisine?.trim())
			ps.setInt(EIGHTH_PARAMETER_INDEX, recipeId)
			ps.executeUpdate()
		}
	}

	private fun insertIngredientGroup(
		conn: java.sql.Connection,
		recipeId: Int,
		groupName: String?,
		groupIndex: Int,
	): Int {
		return conn.prepareStatement(createIngredientGroupSql, Statement.RETURN_GENERATED_KEYS).use { ps ->
			ps.setInt(FIRST_PARAMETER_INDEX, recipeId)
			ps.setString(SECOND_PARAMETER_INDEX, groupName?.trim())
			ps.setInt(THIRD_PARAMETER_INDEX, groupIndex)
			ps.executeUpdate()
			ps.generatedId()
		}
	}

	private fun PreparedStatement.generatedId(): Int {
		return generatedKeys.use { keys ->
			keys.next()
			keys.getInt(FIRST_PARAMETER_INDEX)
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
		val description: String?,
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

		const val FIRST_PARAMETER_INDEX = 1
		const val SECOND_PARAMETER_INDEX = 2
		const val THIRD_PARAMETER_INDEX = 3
		const val FOURTH_PARAMETER_INDEX = 4
		const val FIFTH_PARAMETER_INDEX = 5
		const val SIXTH_PARAMETER_INDEX = 6
		const val SEVENTH_PARAMETER_INDEX = 7
		const val EIGHTH_PARAMETER_INDEX = 8

		const val addFavoriteSql = """
			INSERT INTO favorites (user_id, recipe_id)
			VALUES (?, ?)
			ON CONFLICT (user_id, recipe_id) DO NOTHING
		"""

		const val favoritesSql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time
			FROM favorites f
			INNER JOIN recipes r ON r.id = f.recipe_id
			WHERE f.user_id = ?
			ORDER BY f.created_at DESC
		"""

		const val createdRecipesSql = """
			SELECT id, title, description, instructions, total_time, yields, image_url, cuisine, category
			FROM recipes
			WHERE created_by_user_id = ?
			ORDER BY created_at DESC, id DESC
		"""

		const val isFavoriteSql = """
			SELECT EXISTS(
				SELECT 1
				FROM favorites
				WHERE user_id = ?
					AND recipe_id = ?
			) AS is_favorite
		"""

		const val recipeSql = """
			SELECT id, title, description, instructions, total_time, yields, image_url, cuisine, category
			FROM recipes
			WHERE id = ?
		"""

		const val createRecipeSql = """
			INSERT INTO recipes (title, description, instructions, total_time, yields, image_url, cuisine, created_by_user_id)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
		"""

		const val updateRecipeSql = """
			UPDATE recipes
			SET title = ?,
				description = ?,
				instructions = ?,
				total_time = ?,
				yields = ?,
				image_url = ?,
				cuisine = ?
			WHERE id = ?
		"""

		const val recipeExistsSql = """
			SELECT 1
			FROM recipes
			WHERE id = ?
		"""

		const val recipeOwnedByUserSql = """
			SELECT 1
			FROM recipes
			WHERE id = ?
				AND created_by_user_id = ?
		"""

		const val removeFavoriteSql = """
			DELETE FROM favorites
			WHERE user_id = ?
				AND recipe_id = ?
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

		const val deleteIngredientsForRecipeSql = """
			DELETE FROM ingredients
			WHERE ingredient_group_id IN (
				SELECT id
				FROM ingredient_groups
				WHERE recipe_id = ?
			)
		"""

		const val deleteIngredientGroupsSql = """
			DELETE FROM ingredient_groups
			WHERE recipe_id = ?
		"""

		const val deleteInstructionStepsSql = """
			DELETE FROM instruction_steps
			WHERE recipe_id = ?
		"""

		const val createIngredientGroupSql = """
			INSERT INTO ingredient_groups (recipe_id, name, order_index)
			VALUES (?, ?, ?)
		"""

		const val createIngredientSql = """
			INSERT INTO ingredients (ingredient_group_id, ingredient, order_index)
			VALUES (?, ?, ?)
		"""

		const val createInstructionStepSql = """
			INSERT INTO instruction_steps (recipe_id, step, order_index)
			VALUES (?, ?, ?)
		"""
	}
}
