package app.purecipes.feature.recipedetails.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.RecipeActivityEvent
import kotlinx.coroutines.flow.Flow

interface RecipeActivityRepository {

	suspend fun record(recipeId: Int, event: RecipeActivityEvent): Outcome<Unit>

	fun observeRecipeViews(): Flow<Int>
}
