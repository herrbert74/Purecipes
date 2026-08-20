package app.purecipes.feature.analytics.domain.model

import app.purecipes.shared.domain.model.CalorieRange
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.NutritionFilter
import app.purecipes.shared.domain.model.SearchFilters
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsEventTest {

	@Test
	fun `SearchPerformed has correct event name and properties`() {
		val event = AnalyticsEvent.SearchPerformed.from(
			SearchPerformedContext(
				query = "pasta",
				resultCount = 12,
				filters = SearchFilters(
					cuisines = setOf(Cuisine.ITALIAN, Cuisine.FRENCH),
					mealTypes = setOf(MealType.DINNER),
					calorieRanges = setOf(CalorieRange.LOW),
					nutritionFilters = setOf(NutritionFilter.HIGH_PROTEIN),
				),
				pantryCount = 3,
				excludedCount = 1,
				keyIngredientCount = 2,
				nearMissCount = 4,
				isPremiumUser = true,
			),
		)

		event.eventName shouldBe "search_performed"
		event.properties shouldBe mapOf(
			"query" to AnalyticsValue.TextValue("pasta"),
			"query_length" to AnalyticsValue.NumberValue(5L),
			"result_count" to AnalyticsValue.NumberValue(12L),
			"is_empty_result" to AnalyticsValue.BooleanValue(false),
			"has_query" to AnalyticsValue.BooleanValue(true),
			"has_filters" to AnalyticsValue.BooleanValue(true),
			"filter_count" to AnalyticsValue.NumberValue(5L),
			"cuisines" to AnalyticsValue.TextValue("French,Italian"),
			"meal_types" to AnalyticsValue.TextValue("Dinner"),
			"cooking_time_ranges" to AnalyticsValue.TextValue(""),
			"difficulty_levels" to AnalyticsValue.TextValue(""),
			"cooking_methods" to AnalyticsValue.TextValue(""),
			"dietary_preferences" to AnalyticsValue.TextValue(""),
			"calorie_ranges" to AnalyticsValue.TextValue("Under 300 kcal"),
			"nutrition_filters" to AnalyticsValue.TextValue("High Protein"),
			"pantry_count" to AnalyticsValue.NumberValue(3L),
			"excluded_count" to AnalyticsValue.NumberValue(1L),
			"key_ingredient_count" to AnalyticsValue.NumberValue(2L),
			"near_miss_count" to AnalyticsValue.NumberValue(4L),
			"premium_filters_applied" to AnalyticsValue.BooleanValue(true),
			"is_premium_user" to AnalyticsValue.BooleanValue(true),
		)
	}

	@Test
	fun `SearchPerformed marks empty results`() {
		val event = AnalyticsEvent.SearchPerformed.from(
			SearchPerformedContext(
				query = "",
				resultCount = 0,
				filters = SearchFilters(),
				pantryCount = 0,
				excludedCount = 0,
				keyIngredientCount = 0,
				nearMissCount = 0,
				isPremiumUser = false,
			),
		)

		event.properties["is_empty_result"] shouldBe AnalyticsValue.BooleanValue(true)
		event.properties["has_query"] shouldBe AnalyticsValue.BooleanValue(false)
		event.properties["has_filters"] shouldBe AnalyticsValue.BooleanValue(false)
		event.properties["filter_count"] shouldBe AnalyticsValue.NumberValue(0L)
		event.properties["premium_filters_applied"] shouldBe AnalyticsValue.BooleanValue(false)
		event.properties["is_premium_user"] shouldBe AnalyticsValue.BooleanValue(false)
	}

	@Test
	fun `RecipeViewed has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeViewed(
			recipeId = 42,
			recipeName = "Tomato Pasta",
			origin = AnalyticsOrigin.SEARCH,
			isPrivate = false,
		)

		event.eventName shouldBe "recipe_viewed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"origin" to AnalyticsValue.TextValue("search"),
		)
	}

	@Test
	fun `CookingStarted has correct event name and properties`() {
		val event = AnalyticsEvent.CookingStarted(
			recipeId = 7,
			recipeName = "Tomato Pasta",
			origin = AnalyticsOrigin.RECIPE_DETAILS,
			stepCount = 4,
			isPrivate = true,
		)

		event.eventName shouldBe "cooking_started"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(true),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
			"step_count" to AnalyticsValue.NumberValue(4L),
		)
	}

	@Test
	fun `FavoriteChanged has correct event name and properties`() {
		val event = AnalyticsEvent.FavoriteChanged(
			recipeId = 3,
			recipeName = "Tomato Pasta",
			isFavorite = false,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
			isPrivate = false,
		)

		event.eventName shouldBe "favorite_changed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(3L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"is_favorite" to AnalyticsValue.BooleanValue(false),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `RecipeSaved has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeSaved(
			recipeId = 99,
			recipeName = "Edited Pasta",
			isEditing = true,
			hasPhoto = true,
			ingredientCount = 5,
			stepCount = 3,
			isPrivate = true,
		)

		event.eventName shouldBe "recipe_saved"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(99L),
			"recipe_name" to AnalyticsValue.TextValue("Edited Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(true),
			"is_editing" to AnalyticsValue.BooleanValue(true),
			"has_photo" to AnalyticsValue.BooleanValue(true),
			"ingredient_count" to AnalyticsValue.NumberValue(5L),
			"step_count" to AnalyticsValue.NumberValue(3L),
		)
	}

	@Test
	fun `RecipePrivacyChanged has correct event name and properties`() {
		val event = AnalyticsEvent.RecipePrivacyChanged(
			recipeId = 99,
			recipeName = "Edited Pasta",
			isPrivate = true,
			isEditing = true,
		)

		event.eventName shouldBe "recipe_privacy_changed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(99L),
			"recipe_name" to AnalyticsValue.TextValue("Edited Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(true),
			"is_editing" to AnalyticsValue.BooleanValue(true),
		)
	}

	@Test
	fun `RecipeDeleted has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeDeleted(
			recipeId = 99,
			recipeName = "Tomato Pasta",
			isPrivate = false,
		)

		event.eventName shouldBe "recipe_deleted"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(99L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
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
			recipeName = "Tomato Pasta",
			stepIndex = 2,
			stepCount = 5,
			isPrivate = false,
		)

		event.eventName shouldBe "cooking_step_viewed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"step_index" to AnalyticsValue.NumberValue(2L),
			"step_count" to AnalyticsValue.NumberValue(5L),
		)
	}

	@Test
	fun `CookingCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.CookingCompleted(
			recipeId = 7,
			recipeName = "Tomato Pasta",
			durationSeconds = 120L,
			stepCount = 5,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
			isPrivate = false,
		)

		event.eventName shouldBe "cooking_completed"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"duration_seconds" to AnalyticsValue.NumberValue(120L),
			"step_count" to AnalyticsValue.NumberValue(5L),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `CookingAbandoned has correct event name and properties`() {
		val event = AnalyticsEvent.CookingAbandoned(
			recipeId = 7,
			recipeName = "Tomato Pasta",
			lastStepIndex = 2,
			stepCount = 5,
			durationSeconds = 45L,
			isPrivate = true,
		)

		event.eventName shouldBe "cooking_abandoned"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(true),
			"last_step_index" to AnalyticsValue.NumberValue(2L),
			"step_count" to AnalyticsValue.NumberValue(5L),
			"duration_seconds" to AnalyticsValue.NumberValue(45L),
		)
	}

	@Test
	fun `DeepLinkOpened has correct event name and properties`() {
		val recipeEvent = AnalyticsEvent.DeepLinkOpened(
			linkType = AnalyticsDeepLinkType.RECIPE,
			recipeId = 99,
		)
		val cookbookEvent = AnalyticsEvent.DeepLinkOpened(
			linkType = AnalyticsDeepLinkType.COOKBOOK,
			tokenPresent = true,
		)

		recipeEvent.eventName shouldBe "deep_link_opened"
		recipeEvent.properties shouldBe mapOf(
			"link_type" to AnalyticsValue.TextValue("recipe"),
			"recipe_id" to AnalyticsValue.NumberValue(99L),
		)
		cookbookEvent.eventName shouldBe "deep_link_opened"
		cookbookEvent.properties shouldBe mapOf(
			"link_type" to AnalyticsValue.TextValue("cookbook"),
			"token_present" to AnalyticsValue.BooleanValue(true),
		)
	}

	@Test
	fun `AdImpression and AdClicked have correct event name and properties`() {
		val impression = AnalyticsEvent.AdImpression(placement = AnalyticsAdPlacement.BANNER)
		val clicked = AnalyticsEvent.AdClicked(placement = AnalyticsAdPlacement.INTERSTITIAL)

		impression.eventName shouldBe "ad_impression"
		impression.properties shouldBe mapOf(
			"placement" to AnalyticsValue.TextValue("banner"),
		)
		clicked.eventName shouldBe "ad_clicked"
		clicked.properties shouldBe mapOf(
			"placement" to AnalyticsValue.TextValue("interstitial"),
		)
	}

	@Test
	fun `RecipeShared has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeShared(
			recipeId = 42,
			recipeName = "Tomato Pasta",
			origin = AnalyticsOrigin.RECIPE_DETAILS,
			isPrivate = false,
		)

		event.eventName shouldBe "recipe_shared"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
			"share_type" to AnalyticsValue.TextValue(AnalyticsShareType.RECIPE),
		)
	}

	@Test
	fun `FavoritesTabSelected has correct event name and properties`() {
		val event = AnalyticsEvent.FavoritesTabSelected(tab = AnalyticsFavoritesTab.COOKBOOKS)

		event.eventName shouldBe "favorites_tab_selected"
		event.properties shouldBe mapOf(
			"tab" to AnalyticsValue.TextValue("cookbooks"),
		)
	}

	@Test
	fun `CookbookCreated has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookCreated(
			cookbookId = 10,
			cookbookName = "Weeknight Dinners",
		)

		event.eventName shouldBe "cookbook_created"
		event.properties shouldBe mapOf(
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
			"cookbook_name" to AnalyticsValue.TextValue("Weeknight Dinners"),
		)
	}

	@Test
	fun `CookbookOpened has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookOpened(
			cookbookId = 10,
			cookbookName = "Weeknight Dinners",
			recipeCount = 4,
		)

		event.eventName shouldBe "cookbook_opened"
		event.properties shouldBe mapOf(
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
			"cookbook_name" to AnalyticsValue.TextValue("Weeknight Dinners"),
			"recipe_count" to AnalyticsValue.NumberValue(4L),
		)
	}

	@Test
	fun `CookbookDeleted has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookDeleted(cookbookId = 10)

		event.eventName shouldBe "cookbook_deleted"
		event.properties shouldBe mapOf(
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
		)
	}

	@Test
	fun `RecipeAddedToCookbook has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeAddedToCookbook(
			recipeId = 42,
			recipeName = "Tomato Pasta",
			cookbookId = 10,
			cookbookName = "Weeknight Dinners",
			origin = AnalyticsOrigin.RECIPE_DETAILS,
			isPrivate = false,
		)

		event.eventName shouldBe "recipe_added_to_cookbook"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
			"cookbook_name" to AnalyticsValue.TextValue("Weeknight Dinners"),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `RecipeRemovedFromCookbook has correct event name and properties`() {
		val event = AnalyticsEvent.RecipeRemovedFromCookbook(
			recipeId = 42,
			recipeName = "Tomato Pasta",
			cookbookId = 10,
			cookbookName = "Weeknight Dinners",
			origin = AnalyticsOrigin.FAVORITES,
			isPrivate = false,
		)

		event.eventName shouldBe "recipe_removed_from_cookbook"
		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
			"cookbook_name" to AnalyticsValue.TextValue("Weeknight Dinners"),
			"origin" to AnalyticsValue.TextValue("favorites"),
		)
	}

	@Test
	fun `CookbookShared has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookShared(
			cookbookId = 10,
			cookbookName = "Weeknight Dinners",
			origin = AnalyticsOrigin.FAVORITES,
		)

		event.eventName shouldBe "cookbook_shared"
		event.properties shouldBe mapOf(
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
			"cookbook_name" to AnalyticsValue.TextValue("Weeknight Dinners"),
			"origin" to AnalyticsValue.TextValue("favorites"),
			"share_type" to AnalyticsValue.TextValue(AnalyticsShareType.COOKBOOK),
		)
	}

	@Test
	fun `CookbookImportCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookImportCompleted(
			importedRecipeCount = 3,
			cookbookId = 10,
		)

		event.eventName shouldBe "cookbook_import_completed"
		event.properties shouldBe mapOf(
			"imported_recipe_count" to AnalyticsValue.NumberValue(3L),
			"cookbook_id" to AnalyticsValue.NumberValue(10L),
		)
	}

	@Test
	fun `CookbookImportFailed has correct event name and properties`() {
		val event = AnalyticsEvent.CookbookImportFailed(errorKind = AnalyticsErrorKind.SERVER_ERROR)

		event.eventName shouldBe "cookbook_import_failed"
		event.properties shouldBe mapOf(
			"error_kind" to AnalyticsValue.TextValue("server_error"),
		)
	}

	@Test
	fun `PaywallViewed has correct event name and properties`() {
		val event = AnalyticsEvent.PaywallViewed(
			feature = AnalyticsPremiumFeature.CALORIE_FILTER,
			origin = AnalyticsOrigin.SEARCH,
		)

		event.eventName shouldBe "paywall_viewed"
		event.properties shouldBe mapOf(
			"feature" to AnalyticsValue.TextValue("calorie_filter"),
			"origin" to AnalyticsValue.TextValue("search"),
		)
	}

	@Test
	fun `PremiumUpgradeStarted has correct event name and properties`() {
		val event = AnalyticsEvent.PremiumUpgradeStarted(
			feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
			origin = AnalyticsOrigin.SEARCH,
			plan = AnalyticsSubscriptionPlan.ANNUAL,
		)

		event.eventName shouldBe "premium_upgrade_started"
		event.properties shouldBe mapOf(
			"feature" to AnalyticsValue.TextValue("key_ingredients"),
			"origin" to AnalyticsValue.TextValue("search"),
			"plan" to AnalyticsValue.TextValue("annual"),
		)
	}

	@Test
	fun `PremiumUpgradeCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.PremiumUpgradeCompleted(
			feature = AnalyticsPremiumFeature.SETTINGS_PAYWALL,
			plan = AnalyticsSubscriptionPlan.MONTHLY,
		)

		event.eventName shouldBe "premium_upgrade_completed"
		event.properties shouldBe mapOf(
			"feature" to AnalyticsValue.TextValue("settings_paywall"),
			"plan" to AnalyticsValue.TextValue("monthly"),
		)
	}

	@Test
	fun `PremiumUpgradeFailed has correct event name and properties`() {
		val event = AnalyticsEvent.PremiumUpgradeFailed(
			errorKind = AnalyticsErrorKind.USER_CANCELLED,
			feature = AnalyticsPremiumFeature.NUTRITION_FILTER,
		)

		event.eventName shouldBe "premium_upgrade_failed"
		event.properties shouldBe mapOf(
			"error_kind" to AnalyticsValue.TextValue("user_cancelled"),
			"feature" to AnalyticsValue.TextValue("nutrition_filter"),
		)
	}

	@Test
	fun `RestorePurchasesCompleted has correct event name and properties`() {
		val event = AnalyticsEvent.RestorePurchasesCompleted(
			result = AnalyticsRestoreResult.NOTHING_TO_RESTORE,
		)

		event.eventName shouldBe "restore_purchases_completed"
		event.properties shouldBe mapOf(
			"result" to AnalyticsValue.TextValue("nothing_to_restore"),
		)
	}

	@Test
	fun `PremiumFeatureBlocked has correct event name and properties`() {
		val event = AnalyticsEvent.PremiumFeatureBlocked(
			feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
			origin = AnalyticsOrigin.SEARCH,
		)

		event.eventName shouldBe "premium_feature_blocked"
		event.properties shouldBe mapOf(
			"feature" to AnalyticsValue.TextValue("key_ingredients"),
			"origin" to AnalyticsValue.TextValue("search"),
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

	@Test
	fun `RecipeLoadFailed includes recipe name when known`() {
		val event = AnalyticsEvent.RecipeLoadFailed(
			recipeId = 42,
			errorKind = AnalyticsErrorKind.SERVER_ERROR,
			recipeName = "Tomato Pasta",
			isPrivate = true,
		)

		event.properties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(42L),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(true),
			"error_kind" to AnalyticsValue.TextValue("server_error"),
		)
	}
}
