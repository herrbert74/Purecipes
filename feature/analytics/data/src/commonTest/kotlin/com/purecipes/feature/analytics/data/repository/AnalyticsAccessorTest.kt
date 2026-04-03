package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.repository.ConsentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnalyticsAccessorTest {

	@Test
	fun `trackEvent skips dispatch when consent denies analytics`() {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = AnalyticsAccessor(
			analyticsDataSources = listOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.DENIED),
		)

		accessor.trackEvent(AnalyticsEvent.RecipeViewed(recipeId = 42))

		assertEquals(false, dataSource.lastTrackingEnabled)
		assertNull(dataSource.lastTrackedEventName)
	}

	@Test
	fun `trackEvent dispatches typed properties when consent allows analytics`() {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = AnalyticsAccessor(
			analyticsDataSources = listOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.OBTAINED),
		)

		accessor.trackEvent(AnalyticsEvent.FavoriteChanged(recipeId = 7, isFavorite = true))

		assertEquals(true, dataSource.lastTrackingEnabled)
		assertEquals("favorite_changed", dataSource.lastTrackedEventName)
		assertEquals(
			mapOf(
				"recipe_id" to AnalyticsValue.NumberValue(7),
				"is_favorite" to AnalyticsValue.BooleanValue(true),
			),
			dataSource.lastTrackedProperties,
		)
	}

	@Test
	fun `setUserId propagates enabled state and user id`() {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = AnalyticsAccessor(
			analyticsDataSources = listOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)

		accessor.setUserId("user-123")

		assertEquals(true, dataSource.lastTrackingEnabled)
		assertEquals("user-123", dataSource.lastUserId)
	}

	private class RecordingAnalyticsDataSource : AnalyticsDataSource {

		var lastTrackedEventName: String? = null
		var lastTrackedProperties: Map<String, AnalyticsValue>? = null
		var lastTrackingEnabled: Boolean? = null
		var lastUserId: String? = null

		override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
			lastTrackedEventName = eventName
			lastTrackedProperties = properties
		}

		override fun setTrackingEnabled(isEnabled: Boolean) {
			lastTrackingEnabled = isEnabled
		}

		override fun setUserId(userId: String?) {
			lastUserId = userId
		}
	}

	private class FakeConsentRepository(initialState: ConsentState) : ConsentRepository {

		private val state = MutableStateFlow(initialState)

		override fun observeConsentState(): StateFlow<ConsentState> = state

		override fun currentConsentState(): ConsentState = state.value

		override fun refreshConsent() {
		}

		override fun showConsentForm() {
		}
	}
}