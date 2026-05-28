package app.purecipes.feature.measurement.data.datasource

import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeFormatHandling
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class MeasurementPreferencesLocalDataSourceTest {

	@Test
	fun `preferences stay in sync across datasource instances`() = runTest {
		val preferencesKey = "measurement.preferences.test.${Random.nextInt()}"
		val firstDataSource = MeasurementPreferencesLocalDataSource(preferencesKey = preferencesKey)
		val secondDataSource = MeasurementPreferencesLocalDataSource(preferencesKey = preferencesKey)
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
