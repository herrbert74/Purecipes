package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.model.CreatedRecipeEvent
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeCreatedRecipeRepository(
	initialRecipes: List<RecipeDetails> = emptyList(),
	private val deleteCreatedRecipeResult: Outcome<Unit> = Ok(Unit),
) : CreatedRecipeRepository {

	private val storedRecipes = initialRecipes.toMutableList()
	private val createdRecipeEvents = MutableSharedFlow<CreatedRecipeEvent>(extraBufferCapacity = 1)

	val savedRequests = mutableListOf<SaveCreatedRecipeRequest>()
	val deletedRecipeIds = mutableListOf<Int>()
	var getCreatedRecipesCallCount: Int = 0
		private set
	var deleteCreatedRecipeCallCount: Int = 0

	override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> {
		getCreatedRecipesCallCount += 1
		return Ok(storedRecipes.toList())
	}

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
			isPrivate = request.isPrivate,
			appliances = request.appliances,
		)
		storedRecipes.removeAll { it.id == recipe.id }
		storedRecipes.add(index = 0, element = recipe)
		createdRecipeEvents.tryEmit(CreatedRecipeEvent.Saved(recipe.id))
		return Ok(recipe)
	}

	override suspend fun deleteCreatedRecipe(recipeId: Int): Outcome<Unit> {
		deleteCreatedRecipeCallCount += 1
		deletedRecipeIds += recipeId
		if (deleteCreatedRecipeResult.getError() == null) {
			storedRecipes.removeAll { it.id == recipeId }
			createdRecipeEvents.tryEmit(CreatedRecipeEvent.Deleted(recipeId))
		}
		return deleteCreatedRecipeResult
	}

	override fun observeCreatedRecipeEvents(): Flow<CreatedRecipeEvent> = createdRecipeEvents.asSharedFlow()
}
