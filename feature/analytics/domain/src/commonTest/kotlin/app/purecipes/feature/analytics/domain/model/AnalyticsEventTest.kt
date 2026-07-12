package app.purecipes.feature.analytics.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsEventTest {

	@Test
	fun `SearchPerformed has correct event name and properties`() {
		val event = AnalyticsEvent.SearchPerformed(query = "pasta", resultCount = 12)

		event.eventName shouldBe "search_performed"
		event.properties shouldBe mapOf(
			"query" to AnalyticsValue.TextValue("pasta"),
			"query_length" to AnalyticsValue.NumberValue(5L),
			"result_count" to AnalyticsValue.NumberValue(12L),
		)
	}

	@Test
	fun `RecipeViewed has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeViewed(
			recipeId = 42,
			origin = AnalyticsOrigin.SEARCH,
		)

		event.eventName shouldBe "recipe_viewed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"origin" to AnalyticsValue.TextValue("search"),
		)
	}

	@Test
	fun `CookingStarted has correct event name and properties`() {
		val event = AnalyticsEvent.CookingStarted(
			recipeId = 7,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
		)

		event.eventName shouldBe "cooking_started"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `FavoriteChanged has correct event name and properties`() {
		val event = AnalyticsEvent.FavoriteChanged(
			recipeId = 3,
			isFavorite = false,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
		)

		event.eventName shouldBe "favorite_changed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(3L),
			"is_favorite" to AnalyticsValue.BooleanValue(false),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `RecipeSaved has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeSaved(recipeId = 99, isEditing = true)

		event.eventName shouldBe "recipe_saved"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(99L),
			"is_editing" to AnalyticsValue.BooleanValue(true),
		)
	}
}
