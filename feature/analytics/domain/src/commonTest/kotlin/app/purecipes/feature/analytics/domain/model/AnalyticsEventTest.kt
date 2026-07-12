package app.purecipes.feature.analytics.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsEventTest {

	@Test
	fun `SearchPerformed has correct event name and properties`() {
		val event = AnalyticsEvent.SearchPerformed(
			query = "pasta",
			resultCount = 12,
			isEmptyResult = false,
		)

		event.eventName shouldBe "search_performed"
		event.properties shouldBe mapOf(
			"query" to AnalyticsValue.TextValue("pasta"),
			"query_length" to AnalyticsValue.NumberValue(5L),
			"result_count" to AnalyticsValue.NumberValue(12L),
			"is_empty_result" to AnalyticsValue.BooleanValue(false),
		)
	}

	@Test
	fun `SearchPerformed marks empty results`() {
		val event = AnalyticsEvent.SearchPerformed(
			query = "",
			resultCount = 0,
			isEmptyResult = true,
		)

		event.properties["is_empty_result"] shouldBe AnalyticsValue.BooleanValue(true)
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
			stepCount = 4,
		)

		event.eventName shouldBe "cooking_started"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
			"step_count" to AnalyticsValue.NumberValue(4L),
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
		val event = AnalyticsEvent.RecipeSaved(
			recipeId = 99,
			isEditing = true,
			hasPhoto = true,
			ingredientCount = 5,
			stepCount = 3,
		)

		event.eventName shouldBe "recipe_saved"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(99L),
			"is_editing" to AnalyticsValue.BooleanValue(true),
			"has_photo" to AnalyticsValue.BooleanValue(true),
			"ingredient_count" to AnalyticsValue.NumberValue(5L),
			"step_count" to AnalyticsValue.NumberValue(3L),
		)
	}

	@Test
	fun `SignInCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.SignInCompleted(method = AnalyticsAuthMethod.GOOGLE)

		event.eventName shouldBe "sign_in_completed"
		event.properties shouldBe mapOf(
			"method" to AnalyticsValue.TextValue("google"),
		)
	}

	@Test
	fun `SignUpCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.SignUpCompleted(method = AnalyticsAuthMethod.EMAIL)

		event.eventName shouldBe "sign_up_completed"
		event.properties shouldBe mapOf(
			"method" to AnalyticsValue.TextValue("email"),
		)
	}

	@Test
	fun `SignOut has correct event name and empty properties`() {
		val event = AnalyticsEvent.SignOut

		event.eventName shouldBe "sign_out"
		event.properties shouldBe emptyMap()
	}

	@Test
	fun `CookingStepViewed has correct event name and properties`() {
		val event = AnalyticsEvent.CookingStepViewed(
			recipeId = 7,
			stepIndex = 2,
			stepCount = 5,
		)

		event.eventName shouldBe "cooking_step_viewed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"step_index" to AnalyticsValue.NumberValue(2L),
			"step_count" to AnalyticsValue.NumberValue(5L),
		)
	}

	@Test
	fun `CookingCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.CookingCompleted(
			recipeId = 7,
			durationSeconds = 120L,
		)

		event.eventName shouldBe "cooking_completed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"duration_seconds" to AnalyticsValue.NumberValue(120L),
		)
	}

	@Test
	fun `RecipeShared has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeShared(
			recipeId = 42,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
		)

		event.eventName shouldBe "recipe_shared"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `MeasurementChanged has correct event name and properties`() {
		val event = AnalyticsEvent.MeasurementChanged(system = AnalyticsMeasurementSystem.METRIC)

		event.eventName shouldBe "measurement_changed"
		event.properties shouldBe mapOf(
			"system" to AnalyticsValue.TextValue("metric"),
		)
	}

	@Test
	fun `ConsentChanged has correct event name and properties`() {
		val event = AnalyticsEvent.ConsentChanged(state = ConsentState.OBTAINED)

		event.eventName shouldBe "consent_changed"
		event.properties shouldBe mapOf(
			"state" to AnalyticsValue.TextValue("obtained"),
		)
	}

	@Test
	fun `RecipeLoadFailed has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeLoadFailed(
			recipeId = 42,
			errorKind = AnalyticsErrorKind.SERVER_ERROR,
		)

		event.eventName shouldBe "recipe_load_failed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"error_kind" to AnalyticsValue.TextValue("server_error"),
		)
	}
}
