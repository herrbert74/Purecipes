package app.purecipes.backend.fake

import app.purecipes.backend.auth.SessionService
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession

class FakeSessionService(
	initialSessions: List<AuthenticatedSession> = emptyList(),
	private val createMode: CreateMode = CreateMode.GENERATE_AND_STORE,
) : SessionService {

	enum class CreateMode {
		GENERATE_AND_STORE,
		RETURN_FIRST_OR_GENERATE,
		FAIL,
	}

	private val sessions = linkedMapOf<String, AuthenticatedSession>()
	private var nextUserId = 1L
	private var nextTokenId = 1L

	val session: AuthenticatedSession
		get() = sessions.values.first()

	init {
		initialSessions.forEach { session ->
			sessions[session.accessToken] = session
		}
		nextUserId = sessions.size.toLong() + 1
		nextTokenId = sessions.size.toLong() + 1
	}

	override fun ensureSchema() = Unit

	override fun createSession(
		provider: String,
		externalUserId: String,
		email: String,
		displayName: String,
		firstName: String?,
		familyName: String?,
		profileImageUrl: String?,
	): AuthenticatedSession {
		return when (createMode) {
			CreateMode.GENERATE_AND_STORE ->
				createSession(
					accessToken = "session-token-${nextTokenId++}",
					id = (nextUserId++).toString(),
					email = email,
					displayName = displayName,
					firstName = firstName,
					familyName = familyName,
					profileImageUrl = profileImageUrl,
					provider = provider,
				).also { session ->
					sessions[session.accessToken] = session
				}

			CreateMode.RETURN_FIRST_OR_GENERATE ->
				sessions.values.firstOrNull()
					?: createSession(
						accessToken = "session-token-${nextTokenId++}",
						id = externalUserId,
						email = email,
						displayName = displayName,
						firstName = firstName,
						familyName = familyName,
						profileImageUrl = profileImageUrl,
						provider = provider,
					)

			CreateMode.FAIL -> error("Not needed in this test")
		}
	}

	override fun getSession(accessToken: String): AuthenticatedSession? = sessions[accessToken]

	override fun revokeSession(accessToken: String): Boolean = sessions.remove(accessToken) != null

	companion object {

		fun createSession(
			accessToken: String = "session-token",
			id: String = "1",
			email: String = "user-one@example.com",
			displayName: String = "User One",
			firstName: String? = "User",
			familyName: String? = "One",
			profileImageUrl: String? = null,
			provider: String = "GOOGLE",
		): AuthenticatedSession {
			return AuthenticatedSession(
				accessToken = accessToken,
				expiresAtEpochSeconds = 4_102_444_800,
				user = AuthenticatedBackendUser(
					id = id,
					email = email,
					displayName = displayName,
					firstName = firstName,
					familyName = familyName,
					profileImageUrl = profileImageUrl,
					provider = provider,
				),
			)
		}
	}
}
