package app.purecipes.shared.data.session

import app.purecipes.shared.domain.model.AuthenticatedSession
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
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
	private val settings: Settings = createAuthSessionSettings(),
	private val legacySettings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
) : SessionTokenStore {

	init {
		migrateFromLegacySettings()
	}

	override fun currentSession(): AuthenticatedSession? {
		return settings.getStringOrNull(SESSION_KEY)
			?.let { stored -> runCatching { json.decodeFromString<AuthenticatedSession>(stored) }.getOrNull() }
	}

	override fun currentAccessToken(): String? = currentSession()?.accessToken

	override fun saveSession(session: AuthenticatedSession) {
		settings[SESSION_KEY] = json.encodeToString(session)
		legacySettings.remove(SESSION_KEY)
	}

	override fun clearSession() {
		settings.remove(SESSION_KEY)
		legacySettings.remove(SESSION_KEY)
	}

	private fun migrateFromLegacySettings() {
		if (settings.hasKey(SESSION_KEY)) {
			legacySettings.remove(SESSION_KEY)
			return
		}
		val legacySession = legacySettings.getStringOrNull(SESSION_KEY) ?: return
		settings[SESSION_KEY] = legacySession
		legacySettings.remove(SESSION_KEY)
	}

	private companion object {

		const val SESSION_KEY = "purecipes.backend.session"
	}
}
