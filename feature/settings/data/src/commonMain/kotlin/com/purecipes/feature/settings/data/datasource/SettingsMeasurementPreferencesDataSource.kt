package com.purecipes.feature.settings.data.datasource

import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

class SettingsMeasurementPreferencesDataSource(
	private val settings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) : MeasurementPreferencesDataSource {

	private val preferencesFlow = sharedPreferencesFlow(
		preferencesKey = preferencesKey,
		preferences = loadPreferences(),
	)

	override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = preferencesFlow

	override fun getMeasurementPreferences(): MeasurementPreferences = preferencesFlow.value

	override fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
		persist(preferences)
	}

	override fun resetMeasurementPreferences() {
		persist(defaultPreferences())
	}

	override fun markMismatchNotificationSeen(recipeId: Int) {
		persist(
			preferencesFlow.value.copy(
				notificationSeenRecipeIds = preferencesFlow.value.notificationSeenRecipeIds + recipeId,
			),
		)
	}

	private fun loadPreferences(): MeasurementPreferences {
		return settings.getStringOrNull(preferencesKey)
			?.let { stored -> runCatching { json.decodeFromString<MeasurementPreferences>(stored) }.getOrNull() }
			?: defaultPreferences()
	}

	private fun defaultPreferences(): MeasurementPreferences {
		val countryCode = detectRegionCode()?.uppercase()?.takeIf { it.isNotBlank() }
		return MeasurementPreferences(
			preferredSystem = countryCode.defaultMeasurementSystem(),
			detectedCountryCode = countryCode,
		)
	}

	private fun persist(preferences: MeasurementPreferences) {
		settings[preferencesKey] = json.encodeToString(preferences)
		preferencesFlow.value = preferences
	}

	private fun String?.defaultMeasurementSystem(): MeasurementSystem {
		return if (this == UNITED_STATES_COUNTRY_CODE) MeasurementSystem.IMPERIAL else MeasurementSystem.METRIC
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.measurement.preferences"
		const val UNITED_STATES_COUNTRY_CODE = "US"

		val sharedPreferencesFlows = mutableMapOf<String, MutableStateFlow<MeasurementPreferences>>()

		fun sharedPreferencesFlow(
			preferencesKey: String,
			preferences: MeasurementPreferences,
		): MutableStateFlow<MeasurementPreferences> {
			return sharedPreferencesFlows.getOrPut(preferencesKey) {
				MutableStateFlow(preferences)
			}
		}
	}
}
