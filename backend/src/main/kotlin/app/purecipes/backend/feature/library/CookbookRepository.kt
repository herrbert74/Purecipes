package app.purecipes.backend.feature.library

import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.domain.model.cuisineFromRawValue
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

private const val COOKBOOK_PAGE_MAX = 200

private const val JDBC_USER_ID = 1

private const val JDBC_PAGE_LIMIT = 2

private const val JDBC_PAGE_OFFSET = 3

private const val JDBC_COOKBOOK_ID = 2

private const val JDBC_RECIPE_PAGE_LIMIT = 3

private const val JDBC_RECIPE_PAGE_OFFSET = 4

class CookbookRepository(
	private val dataSource: DataSource,
) {

	fun listCookbooks(userId: Long, pageNumber: Int, pageSize: Int): CookbookListPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, COOKBOOK_PAGE_MAX)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		return dataSource.connection.use { conn ->
			cookbooksListPage(conn, userId, normalizedPageNumber, normalizedPageSize, offset)
		}
	}

	fun createCookbook(userId: Long, name: String): CreateCookbookResult = dataSource.connection.use { conn ->
		if (existsCookbookByNormalizedName(conn, userId, name)) {
			return@use CreateCookbookResult.DuplicateName
		}
		conn.prepareStatement(INSERT_COOKBOOK_SQL, Statement.RETURN_GENERATED_KEYS).use { ps ->
			ps.setLong(1, userId)
			ps.setString(2, name)
			ps.executeUpdate()
			val cookbookId = ps.generatedKeys.use { rs ->
				require(rs.next()) { "Missing cookbook id after insert" }
				rs.getInt(1)
			}
			val createdCookbook = loadCookbookSummary(conn, userId, cookbookId)
				?: error("Cookbook missing after create")
			CreateCookbookResult.Created(createdCookbook)
		}
	}

	fun deleteCookbook(userId: Long, cookbookId: Int): Boolean = dataSource.connection.use { conn ->
		conn.prepareStatement(DELETE_COOKBOOK_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.setLong(2, userId)
			ps.executeUpdate() > 0
		}
	}

	fun listCookbookRecipes(
		userId: Long,
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): SearchResultsPage? {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, COOKBOOK_PAGE_MAX)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		return dataSource.connection.use { conn ->
			if (!isCookbookOwnedByUser(conn, userId, cookbookId)) {
				return@use null
			}
			cookbookRecipesListPage(conn, userId, cookbookId, normalizedPageNumber, normalizedPageSize, offset)
		}
	}

	private fun cookbooksListPage(
		conn: Connection,
		userId: Long,
		normalizedPageNumber: Int,
		normalizedPageSize: Int,
		offset: Int,
	): CookbookListPage {
		val totalMatches = countUserCookbooks(conn, userId)
		val items = fetchCookbookSummariesPage(conn, userId, normalizedPageSize, offset)
		return CookbookListPage(
			items = items,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = totalMatches,
		)
	}

	private fun countUserCookbooks(conn: Connection, userId: Long): Int =
		conn.prepareStatement(COOKBOOKS_COUNT_SQL).use { ps ->
			ps.setLong(JDBC_USER_ID, userId)
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.getInt(1) else 0
			}
		}

	private fun fetchCookbookSummariesPage(
		conn: Connection,
		userId: Long,
		pageSize: Int,
		offset: Int,
	): List<CookbookSummary> =
		conn.prepareStatement(COOKBOOKS_PAGE_SQL).use { ps ->
			ps.setLong(JDBC_USER_ID, userId)
			ps.setInt(JDBC_PAGE_LIMIT, pageSize)
			ps.setInt(JDBC_PAGE_OFFSET, offset)
			ps.executeQuery().use(::readCookbookSummaries)
		}

	private fun cookbookRecipesListPage(
		conn: Connection,
		userId: Long,
		cookbookId: Int,
		normalizedPageNumber: Int,
		normalizedPageSize: Int,
		offset: Int,
	): SearchResultsPage {
		val totalMatches = countCookbookRecipes(conn, userId, cookbookId)
		val items = fetchCookbookRecipeSummariesPage(conn, userId, cookbookId, normalizedPageSize, offset)
		return SearchResultsPage(
			items = items,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = totalMatches,
		)
	}

	private fun countCookbookRecipes(conn: Connection, userId: Long, cookbookId: Int): Int =
		conn.prepareStatement(COOKBOOK_RECIPES_COUNT_SQL).use { ps ->
			ps.setLong(JDBC_USER_ID, userId)
			ps.setInt(JDBC_COOKBOOK_ID, cookbookId)
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.getInt(1) else 0
			}
		}

	private fun fetchCookbookRecipeSummariesPage(
		conn: Connection,
		userId: Long,
		cookbookId: Int,
		pageSize: Int,
		offset: Int,
	): List<RecipeSummary> =
		conn.prepareStatement(COOKBOOK_RECIPES_PAGE_SQL).use { ps ->
			ps.setLong(JDBC_USER_ID, userId)
			ps.setInt(JDBC_COOKBOOK_ID, cookbookId)
			ps.setInt(JDBC_RECIPE_PAGE_LIMIT, pageSize)
			ps.setInt(JDBC_RECIPE_PAGE_OFFSET, offset)
			ps.executeQuery().use(::readCookbookRecipeSummaries)
		}

	fun addRecipeToCookbook(userId: Long, cookbookId: Int, recipeId: Int): AddRecipeToCookbookResult =
		dataSource.connection.use { conn ->
			if (!isCookbookOwnedByUser(conn, userId, cookbookId)) {
				return@use AddRecipeToCookbookResult.CookbookNotFound
			}
			if (!recipeVisibleToUser(conn, userId, recipeId)) {
				return@use AddRecipeToCookbookResult.RecipeNotFound
			}
			if (!isFavorite(conn, userId, recipeId)) {
				return@use AddRecipeToCookbookResult.NotFavorite
			}
			conn.prepareStatement(INSERT_COOKBOOK_RECIPE_SQL).use { ps ->
				ps.setInt(1, cookbookId)
				ps.setInt(2, recipeId)
				ps.executeUpdate()
			}
			touchCookbookUpdatedAt(conn, cookbookId)
			AddRecipeToCookbookResult.Added
		}

	fun removeRecipeFromCookbook(userId: Long, cookbookId: Int, recipeId: Int): Boolean =
		dataSource.connection.use { conn ->
			if (!isCookbookOwnedByUser(conn, userId, cookbookId)) {
				return@use false
			}
			val deleted = conn.prepareStatement(DELETE_COOKBOOK_RECIPE_SQL).use { ps ->
				ps.setInt(1, cookbookId)
				ps.setInt(2, recipeId)
				ps.executeUpdate() > 0
			}
			if (deleted) {
				touchCookbookUpdatedAt(conn, cookbookId)
			}
			deleted
		}

	fun listRecipeCookbooks(userId: Long, recipeId: Int): List<CookbookRef> = dataSource.connection.use { conn ->
		conn.prepareStatement(RECIPE_COOKBOOKS_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeQuery().use(::readCookbookRefs)
		}
	}

	private fun isCookbookOwnedByUser(conn: Connection, userId: Long, cookbookId: Int): Boolean {
		return conn.prepareStatement(COOKBOOK_OWNED_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs -> rs.next() }
		}
	}

	private fun existsCookbookByNormalizedName(conn: Connection, userId: Long, name: String): Boolean {
		return conn.prepareStatement(COOKBOOK_EXISTS_BY_NAME_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setString(2, name)
			ps.executeQuery().use { rs -> rs.next() }
		}
	}

	private fun recipeVisibleToUser(conn: Connection, userId: Long, recipeId: Int): Boolean {
		return conn.prepareStatement(RECIPE_VISIBLE_TO_USER_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs -> rs.next() }
		}
	}

	private fun isFavorite(conn: Connection, userId: Long, recipeId: Int): Boolean {
		return conn.prepareStatement(IS_FAVORITE_FOR_USER_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeQuery().use { rs -> rs.next() && rs.getBoolean(1) }
		}
	}

	private fun touchCookbookUpdatedAt(conn: Connection, cookbookId: Int) {
		conn.prepareStatement(TOUCH_COOKBOOK_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.executeUpdate()
		}
	}

	private fun loadCookbookSummary(conn: Connection, userId: Long, cookbookId: Int): CookbookSummary? {
		return conn.prepareStatement(LOAD_COOKBOOK_SUMMARY_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, cookbookId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				rs.toCookbookSummary()
			}
		}
	}

	private fun readCookbookSummaries(rs: ResultSet): List<CookbookSummary> {
		val out = ArrayList<CookbookSummary>()
		while (rs.next()) {
			out += rs.toCookbookSummary()
		}
		return out
	}

	private fun readCookbookRecipeSummaries(rs: ResultSet): List<RecipeSummary> {
		val out = ArrayList<RecipeSummary>()
		while (rs.next()) {
			val recipeId = rs.getInt("id")
			out += RecipeSummary(
				id = recipeId,
				title = rs.getString("title"),
				cuisine = cuisineFromRawValue(rs.getString("cuisine")),
				imageUrl = rs.getString("image_url"),
				totalTime = rs.getObject("total_time") as? Int,
				measurementSystem = rs.getNullableMeasurementSystem("measurement_system"),
				isFavorite = true,
				isPrivate = rs.getBoolean("is_private"),
			)
		}
		return out
	}

	private fun readCookbookRefs(rs: ResultSet): List<CookbookRef> {
		val out = ArrayList<CookbookRef>()
		while (rs.next()) {
			out += CookbookRef(
				id = rs.getInt("id"),
				name = rs.getString("name"),
			)
		}
		return out
	}

	private fun ResultSet.getNullableMeasurementSystem(columnLabel: String) =
		getString(columnLabel)?.trim()?.takeIf { it.isNotEmpty() }
			?.let { MeasurementSystem.valueOf(it) }

	private fun ResultSet.toCookbookSummary(): CookbookSummary = CookbookSummary(
		id = getInt("id"),
		name = getString("name"),
		recipeCount = getInt("recipe_count"),
		updatedAtEpochMillis = getTimestamp("updated_at").time,
	)

	enum class AddRecipeToCookbookResult {
		Added,
		CookbookNotFound,
		RecipeNotFound,
		NotFavorite,
	}

	sealed interface CreateCookbookResult {
		data class Created(val cookbook: CookbookSummary) : CreateCookbookResult

		data object DuplicateName : CreateCookbookResult
	}

	private companion object {

		const val COOKBOOKS_COUNT_SQL = """
			SELECT COUNT(*)
			FROM cookbooks
			WHERE user_id = ?
		"""

		const val COOKBOOKS_PAGE_SQL = """
			SELECT c.id,
				c.name,
				(
					SELECT COUNT(*)
					FROM cookbook_recipes cr2
					INNER JOIN favorites f2 ON f2.recipe_id = cr2.recipe_id AND f2.user_id = c.user_id
					INNER JOIN recipes r2 ON r2.id = cr2.recipe_id
					WHERE cr2.cookbook_id = c.id
						AND (r2.is_private = FALSE OR r2.created_by_user_id = c.user_id)
				) AS recipe_count,
				c.updated_at
			FROM cookbooks c
			WHERE c.user_id = ?
			ORDER BY c.created_at DESC
			LIMIT ? OFFSET ?
		"""

		const val INSERT_COOKBOOK_SQL = """
			INSERT INTO cookbooks (user_id, name)
			VALUES (?, ?)
		"""

		const val COOKBOOK_EXISTS_BY_NAME_SQL = """
			SELECT 1
			FROM cookbooks
			WHERE user_id = ?
				AND LOWER(TRIM(name)) = LOWER(TRIM(?))
		"""

		const val DELETE_COOKBOOK_SQL = """
			DELETE FROM cookbooks
			WHERE id = ?
				AND user_id = ?
		"""

		const val COOKBOOK_RECIPES_COUNT_SQL = """
			SELECT COUNT(*)
			FROM cookbook_recipes cr
			INNER JOIN favorites f ON f.recipe_id = cr.recipe_id AND f.user_id = ?
			INNER JOIN recipes r ON r.id = cr.recipe_id
			WHERE cr.cookbook_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = f.user_id)
		"""

		const val COOKBOOK_RECIPES_PAGE_SQL = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system, r.is_private
			FROM cookbook_recipes cr
			INNER JOIN favorites f ON f.recipe_id = cr.recipe_id AND f.user_id = ?
			INNER JOIN recipes r ON r.id = cr.recipe_id
			WHERE cr.cookbook_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = f.user_id)
			ORDER BY cr.added_at DESC
			LIMIT ? OFFSET ?
		"""

		const val INSERT_COOKBOOK_RECIPE_SQL = """
			INSERT INTO cookbook_recipes (cookbook_id, recipe_id)
			VALUES (?, ?)
			ON CONFLICT (cookbook_id, recipe_id) DO NOTHING
		"""

		const val DELETE_COOKBOOK_RECIPE_SQL = """
			DELETE FROM cookbook_recipes
			WHERE cookbook_id = ?
				AND recipe_id = ?
		"""

		const val RECIPE_COOKBOOKS_SQL = """
			SELECT c.id, c.name
			FROM cookbook_recipes cr
			INNER JOIN cookbooks c ON c.id = cr.cookbook_id
			INNER JOIN favorites f ON f.recipe_id = cr.recipe_id AND f.user_id = c.user_id
			WHERE c.user_id = ?
				AND cr.recipe_id = ?
			ORDER BY c.name ASC
		"""

		const val COOKBOOK_OWNED_SQL = """
			SELECT 1
			FROM cookbooks
			WHERE id = ?
				AND user_id = ?
		"""

		const val RECIPE_VISIBLE_TO_USER_SQL = """
			SELECT 1
			FROM recipes
			WHERE id = ?
				AND (is_private = FALSE OR created_by_user_id = ?)
		"""

		const val IS_FAVORITE_FOR_USER_SQL = """
			SELECT EXISTS(
				SELECT 1
				FROM favorites
				WHERE user_id = ?
					AND recipe_id = ?
			)
		"""

		const val TOUCH_COOKBOOK_SQL = """
			UPDATE cookbooks
			SET updated_at = CURRENT_TIMESTAMP
			WHERE id = ?
		"""

		const val LOAD_COOKBOOK_SUMMARY_SQL = """
			SELECT c.id,
				c.name,
				(
					SELECT COUNT(*)
					FROM cookbook_recipes cr2
					INNER JOIN favorites f2 ON f2.recipe_id = cr2.recipe_id AND f2.user_id = c.user_id
					INNER JOIN recipes r2 ON r2.id = cr2.recipe_id
					WHERE cr2.cookbook_id = c.id
						AND (r2.is_private = FALSE OR r2.created_by_user_id = c.user_id)
				) AS recipe_count,
				c.updated_at
			FROM cookbooks c
			WHERE c.user_id = ?
				AND c.id = ?
		"""
	}
}
