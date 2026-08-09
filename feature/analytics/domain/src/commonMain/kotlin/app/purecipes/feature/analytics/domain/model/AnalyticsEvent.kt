package app.purecipes.feature.analytics.domain.model

sealed interface AnalyticsEvent {

	val eventName: String

	val properties: Map<String, AnalyticsValue>

	data class SearchPerformed(
		val query: String,
		val resultCount: Int,
		val isEmptyResult: Boolean,
	) : AnalyticsEvent {

		override val eventName = "search_performed"

		override val properties = mapOf(
			"query" to AnalyticsValue.TextValue(query),
			"query_length" to query.length.asAnalyticsValue(),
			"result_count" to resultCount.asAnalyticsValue(),
			"is_empty_result" to isEmptyResult.asAnalyticsValue(),
		)
	}

	data class RecipeViewed(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
	) : AnalyticsEvent {

		override val eventName = "recipe_viewed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
		)
	}

	data class CookingStarted(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
		val stepCount: Int,
	) : AnalyticsEvent {

		override val eventName = "cooking_started"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"origin" to AnalyticsValue.TextValue(origin.value),
			"step_count" to stepCount.asAnalyticsValue(),
		)
	}

	data class FavoriteChanged(
		val recipeId: Int,
		val recipeName: String,
		val isFavorite: Boolean,
		val origin: AnalyticsOrigin,
	) : AnalyticsEvent {

		override val eventName = "favorite_changed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
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
	) : AnalyticsEvent {

		override val eventName = "recipe_saved"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"is_editing" to isEditing.asAnalyticsValue(),
			"has_photo" to hasPhoto.asAnalyticsValue(),
			"ingredient_count" to ingredientCount.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
		)
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
	) : AnalyticsEvent {

		override val eventName = "cooking_step_viewed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"step_index" to stepIndex.asAnalyticsValue(),
			"step_count" to stepCount.asAnalyticsValue(),
		)
	}

	data class CookingCompleted(
		val recipeId: Int,
		val recipeName: String,
		val durationSeconds: Long,
	) : AnalyticsEvent {

		override val eventName = "cooking_completed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"duration_seconds" to durationSeconds.asAnalyticsValue(),
		)
	}

	data class RecipeShared(
		val recipeId: Int,
		val recipeName: String,
		val origin: AnalyticsOrigin,
	) : AnalyticsEvent {

		override val eventName = "recipe_shared"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
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
	) : AnalyticsEvent {

		override val eventName = "recipe_load_failed"

		override val properties = RecipeAnalyticsProperty.identity(recipeId, recipeName) + mapOf(
			"error_kind" to AnalyticsValue.TextValue(errorKind),
		)
	}
}
