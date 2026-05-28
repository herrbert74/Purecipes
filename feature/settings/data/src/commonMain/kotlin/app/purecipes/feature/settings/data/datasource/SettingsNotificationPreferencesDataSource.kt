package app.purecipes.feature.settings.data.datasource

import app.purecipes.shared.domain.model.NotificationPreferences
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

@Inject
@ContributesBinding(AppScope::class)
class SettingsNotificationPreferencesDataSource(
	private val settings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) : NotificationPreferencesDataSource {

	private val preferencesFlow = sharedPreferencesFlow(
		preferencesKey = preferencesKey,
		preferences = loadPreferences(),
	)

	override fun observeNotificationPreferences(): Flow<NotificationPreferences> = preferencesFlow

	override fun saveNotificationPreferences(preferences: NotificationPreferences) {
		persist(preferences)
	}

	private fun loadPreferences(): NotificationPreferences {
		return settings.getStringOrNull(preferencesKey)
			?.let { stored ->
				runCatching { json.decodeFromString<NotificationPreferences>(stored) }.getOrNull()
			}
			?: NotificationPreferences()
	}

	private fun persist(preferences: NotificationPreferences) {
		settings[preferencesKey] = json.encodeToString(preferences)
		preferencesFlow.value = preferences
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.notification.preferences"

		val sharedPreferencesFlows = mutableMapOf<String, MutableStateFlow<NotificationPreferences>>()

		fun sharedPreferencesFlow(
			preferencesKey: String,
			preferences: NotificationPreferences,
		): MutableStateFlow<NotificationPreferences> {
			return sharedPreferencesFlows.getOrPut(preferencesKey) {
				MutableStateFlow(preferences)
			}
		}
	}
}
