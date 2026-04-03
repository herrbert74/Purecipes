package com.purecipes.feature.analytics.domain.model

sealed interface AnalyticsEvent {
	val eventName: String

	val properties: Map<String, AnalyticsValue>

	data class SearchPerformed(
		val query: String,
		val resultCount: Int,
	) : AnalyticsEvent {
		override val eventName = "search_performed"

		override val properties = mapOf(
			"query" to AnalyticsValue.TextValue(query),
			"query_length" to query.length.asAnalyticsValue(),
			"result_count" to resultCount.asAnalyticsValue(),
		)
	}

	data class RecipeViewed(val recipeId: Int) : AnalyticsEvent {
		override val eventName = "recipe_viewed"

		override val properties = mapOf(
			"recipe_id" to recipeId.asAnalyticsValue(),
		)
	}

	data class CookingStarted(val recipeId: Int) : AnalyticsEvent {
		override val eventName = "cooking_started"

		override val properties = mapOf(
			"recipe_id" to recipeId.asAnalyticsValue(),
		)
	}

	data class FavoriteChanged(
		val recipeId: Int,
		val isFavorite: Boolean,
	) : AnalyticsEvent {
		override val eventName = "favorite_changed"

		override val properties = mapOf(
			"recipe_id" to recipeId.asAnalyticsValue(),
			"is_favorite" to isFavorite.asAnalyticsValue(),
		)
	}

	data class RecipeSaved(
		val recipeId: Int,
		val isEditing: Boolean,
	) : AnalyticsEvent {
		override val eventName = "recipe_saved"

		override val properties = mapOf(
			"recipe_id" to recipeId.asAnalyticsValue(),
			"is_editing" to isEditing.asAnalyticsValue(),
		)
	}
}