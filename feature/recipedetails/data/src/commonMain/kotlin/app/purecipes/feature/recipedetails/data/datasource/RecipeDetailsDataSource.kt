package app.purecipes.feature.recipedetails.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.RecipeDetails

interface RecipeDetailsDataSource {

	interface Remote {
		suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails>
	}
}
