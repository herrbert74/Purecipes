package app.purecipes.feature.newrecipe.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.model.CreatedRecipeEvent
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.flow.Flow

interface CreatedRecipeRepository {

	suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>>

	suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails>

	suspend fun deleteCreatedRecipe(recipeId: Int): Outcome<Unit>

	fun observeCreatedRecipeEvents(): Flow<CreatedRecipeEvent>
}
