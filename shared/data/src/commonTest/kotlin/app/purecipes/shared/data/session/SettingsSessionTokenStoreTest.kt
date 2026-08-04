package app.purecipes.shared.data.session

import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import com.russhwolf.settings.Settings
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SettingsSessionTokenStoreTest {

	@Test
	fun `migrates legacy session into auth session settings`() {
		val legacySettings = InMemorySettings(
			"purecipes.backend.session" to ENCODED_SESSION,
		)
		val settings = InMemorySettings()

		val store = SettingsSessionTokenStore(
			settings = settings,
			legacySettings = legacySettings,
		)

		store.currentAccessToken() shouldBe "access-token"
		settings.getStringOrNull("purecipes.backend.session") shouldBe ENCODED_SESSION
		legacySettings.getStringOrNull("purecipes.backend.session").shouldBeNull()
	}

	@Test
	fun `keeps existing auth session settings and clears leftover legacy`() {
		val settings = InMemorySettings(
			"purecipes.backend.session" to ENCODED_SESSION,
		)
		val legacySettings = InMemorySettings(
			"purecipes.backend.session" to LEGACY_ENCODED_SESSION,
		)

		val store = SettingsSessionTokenStore(
			settings = settings,
			legacySettings = legacySettings,
		)

		store.currentAccessToken() shouldBe "access-token"
		legacySettings.getStringOrNull("purecipes.backend.session").shouldBeNull()
	}

	@Test
	fun `saveSession writes auth settings and clears legacy`() {
		val settings = InMemorySettings()
		val legacySettings = InMemorySettings(
			"purecipes.backend.session" to ENCODED_SESSION,
		)
		val store = SettingsSessionTokenStore(
			settings = settings,
			legacySettings = legacySettings,
		)

		store.saveSession(SESSION)

		store.currentSession() shouldBe SESSION
		legacySettings.getStringOrNull("purecipes.backend.session").shouldBeNull()
	}

	@Test
	fun `clearSession removes auth and legacy keys`() {
		val settings = InMemorySettings(
			"purecipes.backend.session" to ENCODED_SESSION,
		)
		val legacySettings = InMemorySettings(
			"purecipes.backend.session" to ENCODED_SESSION,
		)
		val store = SettingsSessionTokenStore(
			settings = settings,
			legacySettings = legacySettings,
		)

		store.clearSession()

		store.currentSession().shouldBeNull()
		settings.getStringOrNull("purecipes.backend.session").shouldBeNull()
		legacySettings.getStringOrNull("purecipes.backend.session").shouldBeNull()
	}

	private class InMemorySettings(
		vararg initialValues: Pair<String, String>,
	) : Settings {

		private val values = initialValues.toMap(mutableMapOf())

		override val keys: Set<String>
			get() = values.keys

		override val size: Int
			get() = values.size

		override fun clear() {
			values.clear()
		}

		override fun remove(key: String) {
			values.remove(key)
		}

		override fun hasKey(key: String): Boolean = values.containsKey(key)

		override fun putInt(key: String, value: Int) = Unit

		override fun getInt(key: String, defaultValue: Int): Int = defaultValue

		override fun getIntOrNull(key: String): Int? = null

		override fun putLong(key: String, value: Long) = Unit

		override fun getLong(key: String, defaultValue: Long): Long = defaultValue

		override fun getLongOrNull(key: String): Long? = null

		override fun putString(key: String, value: String) {
			values[key] = value
		}

		override fun getString(key: String, defaultValue: String): String = values[key] ?: defaultValue

		override fun getStringOrNull(key: String): String? = values[key]

		override fun putFloat(key: String, value: Float) = Unit

		override fun getFloat(key: String, defaultValue: Float): Float = defaultValue

		override fun getFloatOrNull(key: String): Float? = null

		override fun putDouble(key: String, value: Double) = Unit

		override fun getDouble(key: String, defaultValue: Double): Double = defaultValue

		override fun getDoubleOrNull(key: String): Double? = null

		override fun putBoolean(key: String, value: Boolean) = Unit

		override fun getBoolean(key: String, defaultValue: Boolean): Boolean = defaultValue

		override fun getBooleanOrNull(key: String): Boolean? = null
	}

	private companion object {

		private val json = Json {
			ignoreUnknownKeys = true
			explicitNulls = false
		}

		val SESSION = AuthenticatedSession(
			accessToken = "access-token",
			expiresAtEpochSeconds = 4_000_000_000,
			user = AuthenticatedBackendUser(
				id = "1",
				email = "a@example.com",
				displayName = "A",
				firstName = null,
				familyName = null,
				profileImageUrl = null,
				provider = "GOOGLE",
			),
		)

		val ENCODED_SESSION = json.encodeToString(SESSION)

		val LEGACY_ENCODED_SESSION = json.encodeToString(
			AuthenticatedSession(
				accessToken = "legacy-token",
				expiresAtEpochSeconds = 1,
				user = AuthenticatedBackendUser(
					id = "2",
					email = "b@example.com",
					displayName = "B",
					provider = "GOOGLE",
				),
			),
		)
	}
}
