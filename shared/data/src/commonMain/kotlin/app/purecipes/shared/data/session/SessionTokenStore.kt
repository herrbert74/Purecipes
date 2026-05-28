package app.purecipes.shared.data.session

import app.purecipes.shared.domain.model.AuthenticatedSession
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface SessionTokenStore {

	fun currentSession(): AuthenticatedSession?

	fun currentAccessToken(): String?

	fun saveSession(session: AuthenticatedSession)

	fun clearSession()
}

@Inject
@ContributesBinding(AppScope::class)
class SettingsSessionTokenStore(
	private val settings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
) : SessionTokenStore {

	override fun currentSession(): AuthenticatedSession? {
		return settings.getStringOrNull(SESSION_KEY)
			?.let { stored -> runCatching { json.decodeFromString<AuthenticatedSession>(stored) }.getOrNull() }
	}

	override fun currentAccessToken(): String? = currentSession()?.accessToken

	override fun saveSession(session: AuthenticatedSession) {
		settings[SESSION_KEY] = json.encodeToString(session)
	}

	override fun clearSession() {
		settings.remove(SESSION_KEY)
	}

	private companion object {
		const val SESSION_KEY = "purecipes.backend.session"
	}
}
