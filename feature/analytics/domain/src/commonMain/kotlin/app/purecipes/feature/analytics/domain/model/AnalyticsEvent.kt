package app.purecipes.feature.analytics.domain.model

sealed interface AnalyticsEvent {

	val eventName: String

	val properties: Map<String, AnalyticsValue>

	data class SearchPerformed(
		val query: String,
		val resultCount: Int,
		val isEmptyResult: Boolean,
		val hasQuery: Boolean,
		val hasFilters: Boolean,
		val filterCount: Int,
		val cuisines: String,
		val mealTypes: String,
		val cookingTimeRanges: String,
		val difficultyLevels: String,
		val cookingMethods: String,
		val dietaryPreferences: String,
		val calorieRanges: String,
		val nutritionFilters: String,
		val pantryCount: Int,
		val excludedCount: Int,
		val keyIngredientCount: Int,
		val nearMissCount: Int,
		val premiumFiltersApplied: Boolean,
		val isPremiumUser: Boolean,
	) : AnalyticsEvent {

		override val eventName = "search_performed"

		override val properties = mapOf(
			"query" to AnalyticsValue.TextValue(query),
			"query_length" to query.length.asAnalyticsValue(),
			"result_count" to resultCount.asAnalyticsValue(),
			"is_empty_result" to isEmptyResult.asAnalyticsValue(),
			"has_query" to hasQuery.asAnalyticsValue(),
			"has_filters" to hasFilters.asAnalyticsValue(),
			"filter_count" to filterCount.asAnalyticsValue(),
			"cuisines" to AnalyticsValue.TextValue(cuisines),
			"meal_types" to AnalyticsValue.TextValue(mealTypes),
			"cooking_time_ranges" to AnalyticsValue.TextValue(cookingTimeRanges),
			"difficulty_levels" to AnalyticsValue.TextValue(difficultyLevels),
			"cooking_methods" to AnalyticsValue.TextValue(cookingMethods),
			"dietary_preferences" to AnalyticsValue.TextValue(dietaryPreferences),
			"calorie_ranges" to AnalyticsValue.TextValue(calorieRanges),
			"nutrition_filters" to AnalyticsValue.TextValue(nutritionFilters),
			"pantry_count" to pantryCount.asAnalyticsValue(),
			"excluded_count" to excludedCount.asAnalyticsValue(),
			"key_ingredient_count" to keyIngredientCount.asAnalyticsValue(),
			"near_miss_count" to nearMissCount.asAnalyticsValue(),
			"premium_filters_applied" to premiumFiltersApplied.asAnalyticsValue(),
			"is_premium_user" to isPremiumUser.asAnalyticsValue(),
		)

		companion object {

			fun from(context: SearchPerformedContext): SearchPerformed {
				val filters = context.filters
				return SearchPerformed(
					query = context.query,
					resultCount = context.resultCount,
					isEmptyResult = context.resultCount == 0,
					hasQuery = context.query.isNotBlank(),
					hasFilters = !filters.isEmpty,
					filterCount = filters.selectedValueCount(),
					cuisines = filters.cuisines.toJoinedDisplayNames { it.displayName },
					mealTypes = filters.mealTypes.toJoinedDisplayNames { it.displayName },
					cookingTimeRanges = filters.cookingTimeRanges.toJoinedDisplayNames { it.displayName },
					difficultyLevels = filters.difficultyLevels.toJoinedDisplayNames { it.displayName },
					cookingMethods = filters.cookingMethods.toJoinedDisplayNames { it.displayName },
					dietaryPreferences = filters.dietaryPreferences.toJoinedDisplayNames { it.displayName },
					calorieRanges = filters.calorieRanges.toJoinedDisplayNames { it.displayName },
					nutritionFilters = filters.nutritionFilters.toJoinedDisplayNames { it.displayName },
					pantryCount = context.pantryCount,
					excludedCount = context.excludedCount,
					keyIngredientCount = context.keyIngredientCount,
					nearMissCount = context.nearMissCount,
					premiumFiltersApplied = filters.hasPremiumFilters(),
					isPremiumUser = context.isPremiumUser,
				)
			}
		}
	}

	data class RecipeViewed(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "recipe_viewed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class CookingStarted(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
		val stepCount: Int,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "cooking_started"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
			"step_count" to stepCount.asAnalyticsValue(),
		)
	}

	data class FavoriteChanged(
		val recipeId: Int,
		val recipeName: String,
		val isFavorite: Boolean,
		val origin: AnalyticsOrigin,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "favorite_changed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"is_favorite" to isFavorite.asAnalyticsValue(),
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class RecipeSaved(
		val recipeId: Int,
		val recipeName: String,
		val isEditing: Boolean,
		val hasPhoto: Boolean,
		val ingredientCount: Int,
		val stepCount: Int,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "recipe_saved"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"is_editing" to isEditing.asAnalyticsValue(),
			"has_photo" to hasPhoto.asAnalyticsValue(),
			"ingredient_count" to ingredientCount.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
		)
	}

	data class RecipePrivacyChanged(
		val recipeId: Int,
		val recipeName: String,
		val isPrivate: Boolean,
		val isEditing: Boolean,
	) : AnalyticsEvent {

		override val eventName = "recipe_privacy_changed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"is_editing" to isEditing.asAnalyticsValue(),
		)
	}

	data class RecipeDeleted(
		val recipeId: Int,
		val recipeName: String,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "recipe_deleted"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate)
	}

	data class SignInCompleted(
		val method: String,
	) : AnalyticsEvent {

		override val eventName = "sign_in_completed"

		override val properties = mapOf(
			"method" to AnalyticsValue.TextValue(method),
		)
	}

	data class SignUpCompleted(
		val method: String,
	) : AnalyticsEvent {

		override val eventName = "sign_up_completed"

		override val properties = mapOf(
			"method" to AnalyticsValue.TextValue(method),
		)
	}

	data object SignOut : AnalyticsEvent {

		override val eventName = "sign_out"

		override val properties = emptyMap<String, AnalyticsValue>()
	}

	data class CookingStepViewed(
		val recipeId: Int,
		val recipeName: String,
		val stepIndex: Int,
		val stepCount: Int,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "cooking_step_viewed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"step_index" to stepIndex.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
		)
	}

	data class CookingCompleted(
		val recipeId: Int,
		val recipeName: String,
		val durationSeconds: Long,
		val stepCount: Int,
		val origin: AnalyticsOrigin,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "cooking_completed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"duration_seconds" to durationSeconds.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class CookingAbandoned(
		val recipeId: Int,
		val recipeName: String,
		val lastStepIndex: Int,
		val stepCount: Int,
		val durationSeconds: Long,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "cooking_abandoned"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"last_step_index" to lastStepIndex.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
			"duration_seconds" to durationSeconds.asAnalyticsValue(),
		)
	}

	data class DeepLinkOpened(
		val linkType: String,
		val recipeId: Int? = null,
		val tokenPresent: Boolean? = null,
	) : AnalyticsEvent {

		override val eventName = "deep_link_opened"

		override val properties = buildMap {
			put("link_type", AnalyticsValue.TextValue(linkType))
			if (recipeId != null) {
				put(RecipeAnalyticsProperty.RECIPE_ID, recipeId.asAnalyticsValue())
			}
			if (tokenPresent != null) {
				put("token_present", tokenPresent.asAnalyticsValue())
			}
		}
	}

	data class AdImpression(
		val placement: String,
	) : AnalyticsEvent {

		override val eventName = "ad_impression"

		override val properties = mapOf(
			"placement" to AnalyticsValue.TextValue(placement),
		)
	}

	data class AdClicked(
		val placement: String,
	) : AnalyticsEvent {

		override val eventName = "ad_clicked"

		override val properties = mapOf(
			"placement" to AnalyticsValue.TextValue(placement),
		)
	}

	data class RecipeShared(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
		val isPrivate: Boolean,
		val shareType: String = AnalyticsShareType.RECIPE,
	) : AnalyticsEvent {

		override val eventName = "recipe_shared"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
			"share_type" to AnalyticsValue.TextValue(shareType),
		)
	}

	data class FavoritesTabSelected(
		val tab: String,
	) : AnalyticsEvent {

		override val eventName = "favorites_tab_selected"

		override val properties = mapOf(
			"tab" to AnalyticsValue.TextValue(tab),
		)
	}

	data class CookbookCreated(
		val cookbookId: Int,
		val cookbookName: String,
	) : AnalyticsEvent {

		override val eventName = "cookbook_created"

		override val properties = CookbookAnalyticsProperty.identity(cookbookId, cookbookName)
	}

	data class CookbookOpened(
		val cookbookId: Int,
		val cookbookName: String,
		val recipeCount: Int?,
	) : AnalyticsEvent {

		override val eventName = "cookbook_opened"

		override val properties = CookbookAnalyticsProperty.identity(cookbookId, cookbookName) + buildMap {
			if (recipeCount != null) {
				put("recipe_count", recipeCount.asAnalyticsValue())
			}
		}
	}

	data class CookbookDeleted(
		val cookbookId: Int,
	) : AnalyticsEvent {

		override val eventName = "cookbook_deleted"

		override val properties = CookbookAnalyticsProperty.identity(cookbookId, cookbookName = null)
	}

	data class RecipeAddedToCookbook(
		val recipeId: Int,
		val recipeName: String,
		val cookbookId: Int,
		val cookbookName: String?,
		val origin: AnalyticsOrigin,
		val isPrivate: Boolean,
	) : AnalyticsEvent {

		override val eventName = "recipe_added_to_cookbook"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) +
			CookbookAnalyticsProperty.identity(cookbookId, cookbookName) +
			mapOf(
				"origin" to AnalyticsValue.TextValue(origin.value),
			)
	}

	data class CookbookShared(
		val cookbookId: Int,
		val cookbookName: String?,
		val origin: AnalyticsOrigin,
		val shareType: String = AnalyticsShareType.COOKBOOK,
	) : AnalyticsEvent {

		override val eventName = "cookbook_shared"

		override val properties = CookbookAnalyticsProperty.identity(cookbookId, cookbookName) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
			"share_type" to AnalyticsValue.TextValue(shareType),
		)
	}

	data class CookbookImportCompleted(
		val importedRecipeCount: Int,
		val cookbookId: Int?,
	) : AnalyticsEvent {

		override val eventName = "cookbook_import_completed"

		override val properties = buildMap {
			put("imported_recipe_count", importedRecipeCount.asAnalyticsValue())
			if (cookbookId != null) {
				put(CookbookAnalyticsProperty.COOKBOOK_ID, cookbookId.asAnalyticsValue())
			}
		}
	}

	data class CookbookImportFailed(
		val errorKind: String,
	) : AnalyticsEvent {

		override val eventName = "cookbook_import_failed"

		override val properties = mapOf(
			"error_kind" to AnalyticsValue.TextValue(errorKind),
		)
	}

	data class PaywallViewed(
		val feature: String,
		val origin: AnalyticsOrigin,
	) : AnalyticsEvent {

		override val eventName = "paywall_viewed"

		override val properties = mapOf(
			"feature" to AnalyticsValue.TextValue(feature),
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class PremiumUpgradeStarted(
		val feature: String,
		val origin: AnalyticsOrigin,
		val plan: String?,
	) : AnalyticsEvent {

		override val eventName = "premium_upgrade_started"

		override val properties = buildMap {
			put("feature", AnalyticsValue.TextValue(feature))
			put("origin", AnalyticsValue.TextValue(origin.value))
			if (!plan.isNullOrBlank()) {
				put("plan", AnalyticsValue.TextValue(plan))
			}
		}
	}

	data class PremiumUpgradeCompleted(
		val feature: String?,
		val plan: String?,
	) : AnalyticsEvent {

		override val eventName = "premium_upgrade_completed"

		override val properties = buildMap {
			if (!feature.isNullOrBlank()) {
				put("feature", AnalyticsValue.TextValue(feature))
			}
			if (!plan.isNullOrBlank()) {
				put("plan", AnalyticsValue.TextValue(plan))
			}
		}
	}

	data class PremiumUpgradeFailed(
		val errorKind: String,
		val feature: String?,
	) : AnalyticsEvent {

		override val eventName = "premium_upgrade_failed"

		override val properties = buildMap {
			put("error_kind", AnalyticsValue.TextValue(errorKind))
			if (!feature.isNullOrBlank()) {
				put("feature", AnalyticsValue.TextValue(feature))
			}
		}
	}

	data class RestorePurchasesCompleted(
		val result: String,
	) : AnalyticsEvent {

		override val eventName = "restore_purchases_completed"

		override val properties = mapOf(
			"result" to AnalyticsValue.TextValue(result),
		)
	}

	data class PremiumFeatureBlocked(
		val feature: String,
		val origin: AnalyticsOrigin,
	) : AnalyticsEvent {

		override val eventName = "premium_feature_blocked"

		override val properties = mapOf(
			"feature" to AnalyticsValue.TextValue(feature),
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class MeasurementChanged(
		val system: String,
	) : AnalyticsEvent {

		override val eventName = "measurement_changed"

		override val properties = mapOf(
			"system" to AnalyticsValue.TextValue(system),
		)
	}

	data class ConsentChanged(
		val state: ConsentState,
	) : AnalyticsEvent {

		override val eventName = "consent_changed"

		override val properties = mapOf(
			"state" to AnalyticsValue.TextValue(state.name.lowercase()),
		)
	}

	data class RecipeLoadFailed(
		val recipeId: Int,
		val errorKind: String,
		val recipeName: String? = null,
		val isPrivate: Boolean? = null,
	) : AnalyticsEvent {

		override val eventName = "recipe_load_failed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName, isPrivate) + mapOf(
			"error_kind" to AnalyticsValue.TextValue(errorKind),
		)
	}
}
