package app.purecipes.backend.feature.library

import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookShareToken
import app.purecipes.shared.domain.model.CookbookSummary
import java.sql.Connection
import java.util.UUID
import javax.sql.DataSource

class CookbookShareRepository(
	private val dataSource: DataSource,
) {

	private val cookbookRepository = CookbookRepository(dataSource)
	private val favoritesRepository = FavoritesRepository(dataSource)

	fun createOrGetShare(userId: Long, cookbookId: Int): CreateShareResult = dataSource.connection.use { conn ->
		if (!isCookbookOwnedByUser(conn, userId, cookbookId)) {
			return@use CreateShareResult.CookbookNotFound
		}
		val existingToken = findShareTokenForCookbook(conn, cookbookId, userId)
		if (existingToken != null) {
			return@use CreateShareResult.Created(CookbookShareToken(existingToken))
		}
		val token = UUID.randomUUID().toString()
		conn.prepareStatement(INSERT_SHARE_SQL).use { ps ->
			ps.setString(JDBC_SHARE_TOKEN_VALUE, token)
			ps.setInt(JDBC_SHARE_COOKBOOK_ID, cookbookId)
			ps.setLong(JDBC_SHARE_OWNER_USER_ID, userId)
			ps.executeUpdate()
		}
		CreateShareResult.Created(CookbookShareToken(token))
	}

	fun importShare(userId: Long, token: String): ImportShareResult = dataSource.connection.use { conn ->
		importShareOnConnection(conn, userId, token)
	}

	private fun importShareOnConnection(conn: Connection, userId: Long, token: String): ImportShareResult {
		val share = loadShareIfValid(conn, token)
		return when {
			share == null -> ImportShareResult.ShareNotFound
			share.createdByUserId == userId -> ImportShareResult.CannotImportOwnCookbook
			else -> {
				val existingImport = findImportedCookbookId(conn, userId, token)
				if (existingImport != null) {
					importedResultForExisting(conn, userId, existingImport)
				} else {
					importNewCookbookFromShare(conn, userId, token, share)
				}
			}
		}
	}

	private fun loadShareIfValid(conn: Connection, token: String): ShareRow? {
		if (!isValidShareTokenFormat(token)) {
			return null
		}
		return loadShare(conn, token)
	}

	private fun importedResultForExisting(
		conn: Connection,
		userId: Long,
		importedCookbookId: Int,
	): ImportShareResult {
		val cookbook = loadCookbookSummaryForUser(conn, userId, importedCookbookId)
			?: return ImportShareResult.ShareNotFound
		return ImportShareResult.Imported(
			CookbookImportResult(
				cookbook = cookbook,
				recipesImported = NO_RECIPES_IMPORTED,
				recipesSkipped = NO_RECIPES_SKIPPED,
				alreadyImported = true,
			),
		)
	}

	private fun importNewCookbookFromShare(
		conn: Connection,
		userId: Long,
		token: String,
		share: ShareRow,
	): ImportShareResult {
		val sourceName = loadCookbookName(conn, share.cookbookId)
		if (sourceName == null) {
			return ImportShareResult.ShareNotFound
		}
		val recipeIds = loadShareableRecipeIds(conn, share.cookbookId)
		val importName = resolveImportCookbookName(conn, userId, sourceName)
		val createResult = cookbookRepository.createCookbook(userId, importName)
		return if (createResult !is CookbookRepository.CreateCookbookResult.Created) {
			ImportShareResult.ImportFailed
		} else {
			val counts = importRecipesIntoCookbook(userId, createResult.cookbook.id, recipeIds)
			conn.prepareStatement(INSERT_IMPORT_SQL).use { ps ->
				ps.setLong(JDBC_USER_ID, userId)
				ps.setString(JDBC_SHARE_TOKEN, token)
				ps.setInt(JDBC_IMPORTED_COOKBOOK_ID, createResult.cookbook.id)
				ps.executeUpdate()
			}
			loadCookbookSummaryForUser(conn, userId, createResult.cookbook.id)?.let { summary ->
				ImportShareResult.Imported(
					CookbookImportResult(
						cookbook = summary,
						recipesImported = counts.imported,
						recipesSkipped = counts.skipped,
						alreadyImported = false,
					),
				)
			} ?: ImportShareResult.ImportFailed
		}
	}

	private fun importRecipesIntoCookbook(
		userId: Long,
		importedCookbookId: Int,
		recipeIds: List<Int>,
	): RecipeImportCounts {
		var recipesImported = NO_RECIPES_IMPORTED
		var recipesSkipped = NO_RECIPES_SKIPPED
		for (recipeId in recipeIds) {
			if (!favoritesRepository.addFavorite(userId, recipeId)) {
				recipesSkipped += 1
				continue
			}
			val added = cookbookRepository.addRecipeToCookbook(userId, importedCookbookId, recipeId) ==
				CookbookRepository.AddRecipeToCookbookResult.Added
			if (added) {
				recipesImported += 1
			} else {
				recipesSkipped += 1
			}
		}
		return RecipeImportCounts(recipesImported, recipesSkipped)
	}

	private data class RecipeImportCounts(
		val imported: Int,
		val skipped: Int,
	)

	private fun resolveImportCookbookName(conn: Connection, userId: Long, sourceName: String): String {
		val baseName = sourceName.trim().ifEmpty { DEFAULT_IMPORTED_COOKBOOK_NAME }
		if (!existsCookbookByNormalizedName(conn, userId, baseName)) {
			return baseName
		}
		var candidate: String? = null
		var suffix = 2
		while (suffix <= MAX_IMPORT_NAME_SUFFIX_ATTEMPTS && candidate == null) {
			val nextCandidate = "$baseName ($suffix)"
			if (!existsCookbookByNormalizedName(conn, userId, nextCandidate)) {
				candidate = nextCandidate
			}
			suffix += 1
		}
		return candidate ?: "$baseName (${System.currentTimeMillis()})"
	}

	private fun existsCookbookByNormalizedName(conn: Connection, userId: Long, name: String): Boolean {
		return conn.prepareStatement(COOKBOOK_EXISTS_BY_NAME_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setString(2, name)
			ps.executeQuery().use { rs -> rs.next() }
		}
	}

	private fun loadCookbookSummaryForUser(conn: Connection, userId: Long, cookbookId: Int): CookbookSummary? {
		return conn.prepareStatement(LOAD_COOKBOOK_SUMMARY_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, cookbookId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				CookbookSummary(
					id = rs.getInt("id"),
					name = rs.getString("name"),
					recipeCount = rs.getInt("recipe_count"),
					updatedAtEpochMillis = rs.getTimestamp("updated_at").time,
				)
			}
		}
	}

	private fun isCookbookOwnedByUser(conn: Connection, userId: Long, cookbookId: Int): Boolean {
		return conn.prepareStatement(COOKBOOK_OWNED_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs -> rs.next() }
		}
	}

	private fun findShareTokenForCookbook(conn: Connection, cookbookId: Int, userId: Long): String? {
		return conn.prepareStatement(FIND_SHARE_BY_COOKBOOK_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				rs.getString("token")
			}
		}
	}

	private fun loadShare(conn: Connection, token: String): ShareRow? {
		return conn.prepareStatement(LOAD_SHARE_SQL).use { ps ->
			ps.setString(1, token)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				ShareRow(
					cookbookId = rs.getInt("cookbook_id"),
					createdByUserId = rs.getLong("created_by_user_id"),
				)
			}
		}
	}

	private fun findImportedCookbookId(conn: Connection, userId: Long, token: String): Int? {
		return conn.prepareStatement(FIND_IMPORT_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setString(2, token)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				rs.getInt("imported_cookbook_id")
			}
		}
	}

	private fun loadCookbookName(conn: Connection, cookbookId: Int): String? {
		return conn.prepareStatement(LOAD_COOKBOOK_NAME_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null
				rs.getString("name")
			}
		}
	}

	private fun loadShareableRecipeIds(conn: Connection, cookbookId: Int): List<Int> {
		return conn.prepareStatement(SHAREABLE_RECIPE_IDS_SQL).use { ps ->
			ps.setInt(1, cookbookId)
			ps.executeQuery().use { rs ->
				val ids = ArrayList<Int>()
				while (rs.next()) {
					ids += rs.getInt("recipe_id")
				}
				ids
			}
		}
	}

	private fun isValidShareTokenFormat(token: String): Boolean = SHARE_TOKEN_REGEX.matches(token)

	private data class ShareRow(
		val cookbookId: Int,
		val createdByUserId: Long,
	)

	sealed interface CreateShareResult {
		data class Created(val share: CookbookShareToken) : CreateShareResult

		data object CookbookNotFound : CreateShareResult
	}

	sealed interface ImportShareResult {
		data class Imported(val result: CookbookImportResult) : ImportShareResult

		data object ShareNotFound : ImportShareResult

		data object CannotImportOwnCookbook : ImportShareResult

		data object ImportFailed : ImportShareResult
	}

	private companion object {

		const val DEFAULT_IMPORTED_COOKBOOK_NAME = "Shared cookbook"

		const val MAX_IMPORT_NAME_SUFFIX_ATTEMPTS = 50

		const val NO_RECIPES_IMPORTED = 0

		const val NO_RECIPES_SKIPPED = 0

		const val JDBC_SHARE_TOKEN_VALUE = 1

		const val JDBC_SHARE_COOKBOOK_ID = 2

		const val JDBC_SHARE_OWNER_USER_ID = 3

		const val JDBC_USER_ID = 1

		const val JDBC_SHARE_TOKEN = 2

		const val JDBC_IMPORTED_COOKBOOK_ID = 3

		val SHARE_TOKEN_REGEX = Regex(
			"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
		)

		const val INSERT_SHARE_SQL = """
			INSERT INTO cookbook_shares (token, cookbook_id, created_by_user_id)
			VALUES (?, ?, ?)
		"""

		const val FIND_SHARE_BY_COOKBOOK_SQL = """
			SELECT token
			FROM cookbook_shares
			WHERE cookbook_id = ?
				AND created_by_user_id = ?
			ORDER BY created_at DESC
			LIMIT 1
		"""

		const val LOAD_SHARE_SQL = """
			SELECT cookbook_id, created_by_user_id
			FROM cookbook_shares
			WHERE token = ?
		"""

		const val FIND_IMPORT_SQL = """
			SELECT imported_cookbook_id
			FROM cookbook_share_imports
			WHERE user_id = ?
				AND share_token = ?
		"""

		const val INSERT_IMPORT_SQL = """
			INSERT INTO cookbook_share_imports (user_id, share_token, imported_cookbook_id)
			VALUES (?, ?, ?)
		"""

		const val LOAD_COOKBOOK_NAME_SQL = """
			SELECT name
			FROM cookbooks
			WHERE id = ?
		"""

		const val SHAREABLE_RECIPE_IDS_SQL = """
			SELECT cr.recipe_id
			FROM cookbook_recipes cr
			INNER JOIN recipes r ON r.id = cr.recipe_id
			WHERE cr.cookbook_id = ?
			ORDER BY cr.added_at DESC
		"""

		const val COOKBOOK_OWNED_SQL = """
			SELECT 1
			FROM cookbooks
			WHERE id = ?
				AND user_id = ?
		"""

		const val COOKBOOK_EXISTS_BY_NAME_SQL = """
			SELECT 1
			FROM cookbooks
			WHERE user_id = ?
				AND LOWER(TRIM(name)) = LOWER(TRIM(?))
		"""

		const val LOAD_COOKBOOK_SUMMARY_SQL = """
			SELECT c.id,
				c.name,
				(
					SELECT COUNT(*)
					FROM cookbook_recipes cr2
					INNER JOIN favorites f2 ON f2.recipe_id = cr2.recipe_id AND f2.user_id = c.user_id
					WHERE cr2.cookbook_id = c.id
				) AS recipe_count,
				c.updated_at
			FROM cookbooks c
			WHERE c.user_id = ?
				AND c.id = ?
		"""
	}
}
