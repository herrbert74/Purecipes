package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.Ok

class FakeCreatedRecipeRepository(
	initialRecipes: List<RecipeDetails> = emptyList(),
) : CreatedRecipeRepository {

	private val storedRecipes = initialRecipes.toMutableList()

	val savedRequests = mutableListOf<SaveCreatedRecipeRequest>()

	override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> = Ok(storedRecipes.toList())

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
		savedRequests += request
		val recipeId = request.recipeId ?: ((storedRecipes.minOfOrNull(RecipeDetails::id)?.takeIf { it < 0 } ?: 0) - 1)
		val recipe = RecipeDetails(
			id = recipeId,
			title = request.title,
			description = request.description,
			imageUrl = request.imageUrl,
			ingredientGroups = listOf(IngredientGroup(ingredients = request.ingredients)),
			steps = request.steps,
			totalTime = request.totalTime,
			yields = request.yields,
			cuisine = request.cuisine,
		)
		storedRecipes.removeAll { it.id == recipe.id }
		storedRecipes.add(index = 0, element = recipe)
		return Ok(recipe)
	}
}
