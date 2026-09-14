package app.purecipes.backend.feature.featurerequests

import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.domain.model.featureRequestStatusFromRawValue
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

private const val FEATURE_REQUEST_PAGE_MAX = 200

class FeatureRequestRepository(
	private val dataSource: DataSource,
) {

	fun listFeatureRequests(
		userId: Long,
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	): FeatureRequestListPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, FEATURE_REQUEST_PAGE_MAX)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		return dataSource.connection.use { conn ->
			FeatureRequestListPage(
				items = fetchFeatureRequestsPage(conn, userId, sort, status, normalizedPageSize, offset),
				pageNumber = normalizedPageNumber,
				pageSize = normalizedPageSize,
				totalMatches = countFeatureRequests(conn, status),
			)
		}
	}

	fun getFeatureRequest(userId: Long, requestId: Int): FeatureRequest? = dataSource.connection.use { conn ->
		loadFeatureRequest(conn, userId, requestId)
	}

	fun createFeatureRequest(userId: Long, title: String, description: String): FeatureRequest =
		dataSource.connection.use { conn ->
			conn.prepareStatement(INSERT_FEATURE_REQUEST_SQL, Statement.RETURN_GENERATED_KEYS).use { ps ->
				ps.setLong(1, userId)
				ps.setString(2, title)
				ps.setString(THIRD_PARAMETER_INDEX, description)
				ps.setString(FOURTH_PARAMETER_INDEX, FeatureRequestStatus.OPEN.name)
				ps.executeUpdate()
				val requestId = ps.generatedKeys.use { rs ->
					require(rs.next()) { "Missing feature request id after insert" }
					rs.getInt(1)
				}
				loadFeatureRequest(conn, userId, requestId) ?: error("Feature request missing after create")
			}
		}

	fun toggleVote(userId: Long, requestId: Int): FeatureRequest? = dataSource.connection.use { conn ->
		if (!featureRequestExists(conn, requestId)) {
			return@use null
		}
		val deleted = conn.prepareStatement(DELETE_FEATURE_REQUEST_VOTE_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, requestId)
			ps.executeUpdate() > 0
		}
		if (!deleted) {
			conn.prepareStatement(INSERT_FEATURE_REQUEST_VOTE_SQL).use { ps ->
				ps.setLong(1, userId)
				ps.setInt(2, requestId)
				ps.executeUpdate()
			}
		}
		loadFeatureRequest(conn, userId, requestId)
	}

	fun listComments(requestId: Int): List<FeatureRequestComment>? = dataSource.connection.use { conn ->
		if (!featureRequestExists(conn, requestId)) {
			return@use null
		}
		conn.prepareStatement(FEATURE_REQUEST_COMMENTS_SQL).use { ps ->
			ps.setInt(1, requestId)
			ps.executeQuery().use(::readComments)
		}
	}

	fun addComment(userId: Long, requestId: Int, body: String): FeatureRequestComment? =
		dataSource.connection.use { conn ->
			if (!featureRequestExists(conn, requestId)) {
				return@use null
			}
			val authorDisplayName = loadDisplayName(conn, userId) ?: return@use null
			conn.prepareStatement(INSERT_FEATURE_REQUEST_COMMENT_SQL, Statement.RETURN_GENERATED_KEYS).use { ps ->
				ps.setInt(1, requestId)
				ps.setLong(2, userId)
				ps.setString(THIRD_PARAMETER_INDEX, authorDisplayName)
				ps.setString(FOURTH_PARAMETER_INDEX, body)
				ps.executeUpdate()
				val commentId = ps.generatedKeys.use { rs ->
					require(rs.next()) { "Missing comment id after insert" }
					rs.getInt(1)
				}
				loadComment(conn, commentId) ?: error("Comment missing after create")
			}
		}

	private fun fetchFeatureRequestsPage(
		conn: Connection,
		userId: Long,
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageSize: Int,
		offset: Int,
	): List<FeatureRequest> {
		val sql = featureRequestsPageSql(sort = sort, hasStatusFilter = status != null)
		return conn.prepareStatement(sql).use { ps ->
			var parameterIndex = 1
			ps.setLong(parameterIndex++, userId)
			if (status != null) {
				ps.setString(parameterIndex++, status.name)
			}
			ps.setInt(parameterIndex, pageSize)
			ps.setInt(parameterIndex + 1, offset)
			ps.executeQuery().use(::readFeatureRequests)
		}
	}

	private fun countFeatureRequests(conn: Connection, status: FeatureRequestStatus?): Int {
		val sql = if (status == null) FEATURE_REQUESTS_COUNT_SQL else FEATURE_REQUESTS_COUNT_BY_STATUS_SQL
		return conn.prepareStatement(sql).use { ps ->
			if (status != null) {
				ps.setString(1, status.name)
			}
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.getInt(1) else 0
			}
		}
	}

	private fun loadFeatureRequest(conn: Connection, userId: Long, requestId: Int): FeatureRequest? =
		conn.prepareStatement(LOAD_FEATURE_REQUEST_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, requestId)
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.toFeatureRequest() else null
			}
		}

	private fun featureRequestExists(conn: Connection, requestId: Int): Boolean =
		conn.prepareStatement(FEATURE_REQUEST_EXISTS_SQL).use { ps ->
			ps.setInt(1, requestId)
			ps.executeQuery().use { rs -> rs.next() }
		}

	private fun loadDisplayName(conn: Connection, userId: Long): String? =
		conn.prepareStatement(LOAD_DISPLAY_NAME_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.getString("display_name") else null
			}
		}

	private fun loadComment(conn: Connection, commentId: Int): FeatureRequestComment? =
		conn.prepareStatement(LOAD_FEATURE_REQUEST_COMMENT_SQL).use { ps ->
			ps.setInt(1, commentId)
			ps.executeQuery().use { rs ->
				if (rs.next()) rs.toFeatureRequestComment() else null
			}
		}

	private fun readFeatureRequests(rs: ResultSet): List<FeatureRequest> {
		val out = ArrayList<FeatureRequest>()
		while (rs.next()) {
			out += rs.toFeatureRequest()
		}
		return out
	}

	private fun readComments(rs: ResultSet): List<FeatureRequestComment> {
		val out = ArrayList<FeatureRequestComment>()
		while (rs.next()) {
			out += rs.toFeatureRequestComment()
		}
		return out
	}

	private fun ResultSet.toFeatureRequest(): FeatureRequest = FeatureRequest(
		id = getInt("id"),
		title = getString("title"),
		description = getString("description"),
		status = featureRequestStatusFromRawValue(getString("status")),
		voteCount = getInt("vote_count"),
		commentCount = getInt("comment_count"),
		createdAtEpochMillis = getTimestamp("created_at").time,
		votedByCurrentUser = getBoolean("voted_by_current_user"),
	)

	private fun ResultSet.toFeatureRequestComment(): FeatureRequestComment = FeatureRequestComment(
		id = getInt("id"),
		requestId = getInt("request_id"),
		authorDisplayName = getString("author_display_name"),
		body = getString("body"),
		createdAtEpochMillis = getTimestamp("created_at").time,
	)

	private fun featureRequestsPageSql(sort: FeatureRequestSort, hasStatusFilter: Boolean): String {
		val statusClause = if (hasStatusFilter) "WHERE fr.status = ?" else ""
		val orderClause = when (sort) {
			FeatureRequestSort.TOP_VOTES -> "ORDER BY vote_count DESC, fr.created_at DESC"
			FeatureRequestSort.NEWEST -> "ORDER BY fr.created_at DESC"
		}
		return """
			$FEATURE_REQUEST_SELECT_SQL
			$statusClause
			$orderClause
			LIMIT ? OFFSET ?
		"""
	}

	private companion object {

		const val THIRD_PARAMETER_INDEX = 3

		const val FOURTH_PARAMETER_INDEX = 4

		const val FEATURE_REQUEST_SELECT_SQL = """
			SELECT fr.id,
				fr.title,
				fr.description,
				fr.status,
				fr.created_at,
				(
					SELECT COUNT(*)
					FROM feature_request_votes v
					WHERE v.request_id = fr.id
				) AS vote_count,
				(
					SELECT COUNT(*)
					FROM feature_request_comments c
					WHERE c.request_id = fr.id
				) AS comment_count,
				EXISTS(
					SELECT 1
					FROM feature_request_votes mv
					WHERE mv.request_id = fr.id
						AND mv.user_id = ?
				) AS voted_by_current_user
			FROM feature_requests fr
		"""

		const val LOAD_FEATURE_REQUEST_SQL = """
			$FEATURE_REQUEST_SELECT_SQL
			WHERE fr.id = ?
		"""

		const val FEATURE_REQUESTS_COUNT_SQL = """
			SELECT COUNT(*)
			FROM feature_requests
		"""

		const val FEATURE_REQUESTS_COUNT_BY_STATUS_SQL = """
			SELECT COUNT(*)
			FROM feature_requests
			WHERE status = ?
		"""

		const val FEATURE_REQUEST_EXISTS_SQL = """
			SELECT 1
			FROM feature_requests
			WHERE id = ?
		"""

		const val INSERT_FEATURE_REQUEST_SQL = """
			INSERT INTO feature_requests (created_by_user_id, title, description, status)
			VALUES (?, ?, ?, ?)
		"""

		const val INSERT_FEATURE_REQUEST_VOTE_SQL = """
			INSERT INTO feature_request_votes (user_id, request_id)
			VALUES (?, ?)
			ON CONFLICT (user_id, request_id) DO NOTHING
		"""

		const val DELETE_FEATURE_REQUEST_VOTE_SQL = """
			DELETE FROM feature_request_votes
			WHERE user_id = ?
				AND request_id = ?
		"""

		const val FEATURE_REQUEST_COMMENTS_SQL = """
			SELECT id, request_id, author_display_name, body, created_at
			FROM feature_request_comments
			WHERE request_id = ?
			ORDER BY created_at ASC, id ASC
		"""

		const val INSERT_FEATURE_REQUEST_COMMENT_SQL = """
			INSERT INTO feature_request_comments (request_id, user_id, author_display_name, body)
			VALUES (?, ?, ?, ?)
		"""

		const val LOAD_FEATURE_REQUEST_COMMENT_SQL = """
			SELECT id, request_id, author_display_name, body, created_at
			FROM feature_request_comments
			WHERE id = ?
		"""

		const val LOAD_DISPLAY_NAME_SQL = """
			SELECT display_name
			FROM app_users
			WHERE id = ?
		"""
	}
}
