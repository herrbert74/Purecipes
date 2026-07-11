package app.purecipes.backend.auth

import app.purecipes.backend.db.APP_USERS_ADD_IS_PREMIUM_SQL
import app.purecipes.backend.db.APP_USERS_TABLE_SQL
import app.purecipes.backend.db.AUTH_SESSIONS_TABLE_SQL
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import java.security.MessageDigest
import java.security.SecureRandom
import java.sql.Connection
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import javax.sql.DataSource

private val secureRandom = SecureRandom()

interface SessionService {

	fun ensureSchema()

	fun createSession(
		provider: String,
		externalUserId: String,
		email: String,
		displayName: String,
		firstName: String?,
		familyName: String?,
		profileImageUrl: String?,
	): AuthenticatedSession

	fun getSession(accessToken: String): AuthenticatedSession?

	fun revokeSession(accessToken: String): Boolean
}

class JdbcSessionService(
	private val dataSource: DataSource,
	private val clock: () -> Instant = { Instant.now() },
) : SessionService {

	override fun ensureSchema() {
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(APP_USERS_TABLE_SQL)
				statement.execute(APP_USERS_ADD_IS_PREMIUM_SQL)
				statement.execute(AUTH_SESSIONS_TABLE_SQL)
			}
		}
	}

	override fun createSession(
		provider: String,
		externalUserId: String,
		email: String,
		displayName: String,
		firstName: String?,
		familyName: String?,
		profileImageUrl: String?,
	): AuthenticatedSession {
		val normalizedRequest = NormalizedSessionRequest(
			provider = provider.trim().uppercase(),
			externalUserId = externalUserId.trim(),
			email = email.trim().lowercase(),
			displayName = displayName.trim().ifBlank {
				email.trim().lowercase().substringBefore('@').replaceFirstChar {
					if (it.isLowerCase()) it.titlecase() else it.toString()
				}
			},
			firstName = firstName,
			familyName = familyName,
			profileImageUrl = profileImageUrl,
		)
		val accessToken = generateAccessToken()
		val accessTokenHash = accessToken.sha256()
		val expiresAt = clock().plus(DEFAULT_SESSION_DURATION_DAYS, ChronoUnit.DAYS)

		dataSource.connection.use { connection ->
			connection.autoCommit = false
			var committed = false
			try {
				val userId = connection.findOrCreateUser(normalizedRequest)
				connection.createSessionRecord(userId, accessTokenHash, expiresAt)
				connection.commit()
				committed = true
				return createAuthenticatedSession(accessToken, expiresAt, userId, normalizedRequest)
			} finally {
				if (!committed) {
					connection.rollback()
				}
				connection.autoCommit = true
			}
		}
	}

	override fun getSession(accessToken: String): AuthenticatedSession? {
		val normalizedToken = accessToken.trim()
		if (normalizedToken.isBlank()) {
			return null
		}
		dataSource.connection.use { connection ->
			return connection.findActiveSession(normalizedToken)
		}
	}

	override fun revokeSession(accessToken: String): Boolean {
		val normalizedToken = accessToken.trim()
		if (normalizedToken.isBlank()) {
			return false
		}
		dataSource.connection.use { connection ->
			connection.prepareStatement(REVOKE_SESSION_SQL).use { statement ->
				statement.setTimestamp(1, Timestamp.from(clock()))
				statement.setString(2, normalizedToken.sha256())
				return statement.executeUpdate() > 0
			}
		}
	}

	private fun Connection.findOrCreateUser(request: NormalizedSessionRequest): Long {
		prepareStatement(FIND_USER_SQL).use { statement ->
			statement.setString(INDEX_FIRST, request.provider)
			statement.setString(INDEX_SECOND, request.externalUserId)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) {
					val userId = resultSet.getLong("id")
					updateUser(userId, request)
					return userId
				}
			}
		}

		prepareStatement(CREATE_USER_SQL).use { statement ->
			statement.setString(INDEX_FIRST, request.provider)
			statement.setString(INDEX_SECOND, request.externalUserId)
			statement.setString(INDEX_THIRD, request.email)
			statement.setString(INDEX_FOURTH, request.displayName)
			statement.setString(INDEX_FIFTH, request.firstName)
			statement.setString(INDEX_SIXTH, request.familyName)
			statement.setString(INDEX_SEVENTH, request.profileImageUrl)
			statement.executeQuery().use { resultSet ->
				resultSet.next()
				return resultSet.getLong(INDEX_FIRST)
			}
		}
	}

	private fun Connection.updateUser(userId: Long, request: NormalizedSessionRequest) {
		prepareStatement(UPDATE_USER_SQL).use { statement ->
			statement.setString(INDEX_FIRST, request.email)
			statement.setString(INDEX_SECOND, request.displayName)
			statement.setString(INDEX_THIRD, request.firstName)
			statement.setString(INDEX_FOURTH, request.familyName)
			statement.setString(INDEX_FIFTH, request.profileImageUrl)
			statement.setLong(INDEX_SIXTH, userId)
			statement.executeUpdate()
		}
	}

	private fun Connection.createSessionRecord(userId: Long, accessTokenHash: String, expiresAt: Instant) {
		prepareStatement(CREATE_SESSION_SQL).use { statement ->
			statement.setLong(INDEX_FIRST, userId)
			statement.setString(INDEX_SECOND, accessTokenHash)
			statement.setTimestamp(INDEX_THIRD, Timestamp.from(expiresAt))
			statement.executeUpdate()
		}
	}

	private fun Connection.findActiveSession(normalizedToken: String): AuthenticatedSession? {
		prepareStatement(GET_SESSION_SQL).use { statement ->
			statement.setString(INDEX_FIRST, normalizedToken.sha256())
			statement.executeQuery().use { resultSet ->
				if (!resultSet.next()) {
					return null
				}
				return resultSet.toAuthenticatedSession(normalizedToken)
			}
		}
	}

	private fun java.sql.ResultSet.toAuthenticatedSession(accessToken: String): AuthenticatedSession {
		return AuthenticatedSession(
			accessToken = accessToken,
			expiresAtEpochSeconds = getTimestamp("expires_at").toInstant().epochSecond,
			user = AuthenticatedBackendUser(
				id = getLong("id").toString(),
				email = getString("email"),
				displayName = getString("display_name"),
				firstName = getString("first_name"),
				familyName = getString("family_name"),
				profileImageUrl = getString("profile_image_url"),
				provider = getString("provider"),
			),
		)
	}

	private fun createAuthenticatedSession(
		accessToken: String,
		expiresAt: Instant,
		userId: Long,
		request: NormalizedSessionRequest,
	): AuthenticatedSession {
		return AuthenticatedSession(
			accessToken = accessToken,
			expiresAtEpochSeconds = expiresAt.epochSecond,
			user = AuthenticatedBackendUser(
				id = userId.toString(),
				email = request.email,
				displayName = request.displayName,
				firstName = request.firstName,
				familyName = request.familyName,
				profileImageUrl = request.profileImageUrl,
				provider = request.provider,
			),
		)
	}

	private companion object {

		private const val DEFAULT_SESSION_DURATION_DAYS = 30L
		private const val INDEX_FIRST = 1
		private const val INDEX_SECOND = 2
		private const val INDEX_THIRD = 3
		private const val INDEX_FOURTH = 4
		private const val INDEX_FIFTH = 5
		private const val INDEX_SIXTH = 6
		private const val INDEX_SEVENTH = 7

		private const val FIND_USER_SQL = """
			SELECT id
			FROM app_users
			WHERE provider = ? AND external_user_id = ?
		"""

		private const val CREATE_USER_SQL = """
			INSERT INTO app_users (
				provider,
				external_user_id,
				email,
				display_name,
				first_name,
				family_name,
				profile_image_url
			)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			RETURNING id
		"""

		private const val UPDATE_USER_SQL = """
			UPDATE app_users
			SET email = ?, display_name = ?, first_name = ?, family_name = ?, profile_image_url = ?, updated_at = CURRENT_TIMESTAMP
			WHERE id = ?
		"""

		private const val CREATE_SESSION_SQL = """
			INSERT INTO auth_sessions (user_id, access_token_hash, expires_at)
			VALUES (?, ?, ?)
		"""

		private const val GET_SESSION_SQL = """
			SELECT app_users.id, app_users.email, app_users.display_name, app_users.first_name, app_users.family_name,
				app_users.profile_image_url, app_users.provider, auth_sessions.expires_at
			FROM auth_sessions
			INNER JOIN app_users ON app_users.id = auth_sessions.user_id
			WHERE auth_sessions.access_token_hash = ?
				AND auth_sessions.revoked_at IS NULL
				AND auth_sessions.expires_at > CURRENT_TIMESTAMP
		"""

		private const val REVOKE_SESSION_SQL = """
			UPDATE auth_sessions
			SET revoked_at = ?
			WHERE access_token_hash = ? AND revoked_at IS NULL
		"""

	}
}

private data class NormalizedSessionRequest(
	val provider: String,
	val externalUserId: String,
	val email: String,
	val displayName: String,
	val firstName: String?,
	val familyName: String?,
	val profileImageUrl: String?,
)

private fun generateAccessToken(): String {
	val bytes = ByteArray(TOKEN_BYTE_LENGTH)
	secureRandom.nextBytes(bytes)
	return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}

private fun String.sha256(): String {
	val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
	return buildString(digest.size * HEX_CHAR_COUNT_PER_BYTE) {
		digest.forEach { byte -> append("%02x".format(byte)) }
	}
}

private const val TOKEN_BYTE_LENGTH = 32
private const val HEX_CHAR_COUNT_PER_BYTE = 2
