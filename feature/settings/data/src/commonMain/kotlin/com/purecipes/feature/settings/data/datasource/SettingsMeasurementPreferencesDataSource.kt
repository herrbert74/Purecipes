package com.purecipes.feature.settings.data.datasource

import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsMeasurementPreferencesDataSource(
	private val settings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
) : MeasurementPreferencesDataSource {

	private val preferencesFlow = MutableStateFlow(loadPreferences())

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
		return settings.getStringOrNull(PREFERENCES_KEY)
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
		settings[PREFERENCES_KEY] = json.encodeToString(preferences)
		preferencesFlow.value = preferences
	}

	private fun String?.defaultMeasurementSystem(): MeasurementSystem {
		return if (this == UNITED_STATES_COUNTRY_CODE) MeasurementSystem.IMPERIAL else MeasurementSystem.METRIC
	}

	private companion object {

		const val PREFERENCES_KEY = "purecipes.measurement.preferences"
		const val UNITED_STATES_COUNTRY_CODE = "US"
	}
}
