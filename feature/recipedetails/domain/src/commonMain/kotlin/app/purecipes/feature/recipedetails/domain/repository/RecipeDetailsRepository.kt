package app.purecipes.feature.recipedetails.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.RecipeDetails

interface RecipeDetailsRepository {

	suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails>
}
