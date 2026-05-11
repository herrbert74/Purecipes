package app.purecipes.shared.datatestfixtures.fake

import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedSession

class FakeSessionTokenStore(
	initialSession: AuthenticatedSession? = null,
) : SessionTokenStore {

	private var session: AuthenticatedSession? = initialSession

	override fun currentSession(): AuthenticatedSession? = session

	override fun currentAccessToken(): String? = session?.accessToken

	override fun saveSession(session: AuthenticatedSession) {
		this.session = session
	}

	override fun clearSession() {
		session = null
	}
}
