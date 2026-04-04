package com.purecipes.feature.analytics.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsEventTest {

	@Test
	fun `SearchPerformed has correct event name and properties`() {
		val event = AnalyticsEvent.SearchPerformed(query = "pasta", resultCount = 12)

		assertEquals("search_performed", event.eventName)
		assertEquals(
			mapOf(
				"query" to AnalyticsValue.TextValue("pasta"),
				"query_length" to AnalyticsValue.NumberValue(5L),
				"result_count" to AnalyticsValue.NumberValue(12L),
			),
			event.properties,
		)
	}

	@Test
	fun `RecipeViewed has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeViewed(recipeId = 42)

		assertEquals("recipe_viewed", event.eventName)
		assertEquals(
			mapOf("recipe_id" to AnalyticsValue.NumberValue(42L)),
			event.properties,
		)
	}

	@Test
	fun `CookingStarted has correct event name and properties`() {
		val event = AnalyticsEvent.CookingStarted(recipeId = 7)

		assertEquals("cooking_started", event.eventName)
		assertEquals(
			mapOf("recipe_id" to AnalyticsValue.NumberValue(7L)),
			event.properties,
		)
	}

	@Test
	fun `FavoriteChanged has correct event name and properties`() {
		val event = AnalyticsEvent.FavoriteChanged(recipeId = 3, isFavorite = false)

		assertEquals("favorite_changed", event.eventName)
		assertEquals(
			mapOf(
				"recipe_id" to AnalyticsValue.NumberValue(3L),
				"is_favorite" to AnalyticsValue.BooleanValue(false),
			),
			event.properties,
		)
	}

	@Test
	fun `RecipeSaved has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeSaved(recipeId = 99, isEditing = true)

		assertEquals("recipe_saved", event.eventName)
		assertEquals(
			mapOf(
				"recipe_id" to AnalyticsValue.NumberValue(99L),
				"is_editing" to AnalyticsValue.BooleanValue(true),
			),
			event.properties,
		)
	}
}
