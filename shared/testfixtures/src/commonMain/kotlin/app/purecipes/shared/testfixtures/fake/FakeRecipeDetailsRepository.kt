package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.Ok

class FakeRecipeDetailsRepository(
	private val result: Outcome<RecipeDetails>,
) : RecipeDetailsRepository {

	constructor(recipeDetails: RecipeDetails) : this(Ok(recipeDetails))

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
}
