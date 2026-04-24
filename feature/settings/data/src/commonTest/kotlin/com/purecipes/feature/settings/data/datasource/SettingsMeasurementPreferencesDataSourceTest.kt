package com.purecipes.feature.settings.data.datasource

import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class SettingsMeasurementPreferencesDataSourceTest {

	@Test
	fun `preferences stay in sync across datasource instances`() = runTest {
		val preferencesKey = "settings.measurement.test.${Random.nextInt()}"
		val firstDataSource = SettingsMeasurementPreferencesDataSource(preferencesKey = preferencesKey)
		val secondDataSource = SettingsMeasurementPreferencesDataSource(preferencesKey = preferencesKey)
		val updatedPreferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.IMPERIAL,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
			detectedCountryCode = "US",
			notificationSeenRecipeIds = setOf(3),
		)

		firstDataSource.saveMeasurementPreferences(updatedPreferences)

		secondDataSource.getMeasurementPreferences() shouldBe updatedPreferences
	}
}
