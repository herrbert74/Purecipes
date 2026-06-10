package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

class FakeRecipeDetailsRepository(
	private val recipesById: Map<Int, Outcome<RecipeDetails>> = emptyMap(),
	private val defaultResult: Outcome<RecipeDetails>? = null,
) : RecipeDetailsRepository {

	constructor(result: Outcome<RecipeDetails>) : this(defaultResult = result)

	constructor(recipeDetails: RecipeDetails) : this(defaultResult = Ok(recipeDetails))

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> =
		recipesById[recipeId] ?: defaultResult ?: Err(Failure.ServerError("Recipe $recipeId not found"))
}
